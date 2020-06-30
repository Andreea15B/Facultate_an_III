package paymentgateway;

import utils.CryptoUtils;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class PaymentGateway {
    private final int PORT = 4040;

    private ServerSocket socket;
    private PrivateKey   privateKey;
    private PublicKey    publicKey;

    private Socket             mSocket;
    private ObjectInputStream  fromM;
    private ObjectOutputStream toM;
    private PublicKey          mPublicKey;
    private SecretKey          mSecretKey;
    
    public boolean init() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);

            KeyPair keyPair = keyPairGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            System.out.println("RSA Public Key: " + this.publicKey + "\nRSA Private Key: " + this.privateKey);

            this.socket = new ServerSocket(this.PORT);
            System.out.println("Payment Gateway listening at PORT " + this.PORT);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void run() {
        boolean retCode;

        retCode = performExchangeSubProtocol();

        if (retCode) {
            System.out.println("E-Commerce Transaction Succeeded!");
        } else {
            System.err.println("E-Commerce Transaction Failed!");
        }
    }
    
    public void deinit() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean performExchangeSubProtocol() {
        try {
            this.mSocket = this.socket.accept();
            System.out.println("Merchant Connected on Socket: " + this.mSocket.toString());

            this.toM = new ObjectOutputStream(this.mSocket.getOutputStream());
            this.fromM = new ObjectInputStream(this.mSocket.getInputStream());

            CryptoUtils cryptoUtils = new CryptoUtils();

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            /* Payment Gateway's Public Key -> Merchant */
            this.toM.writeObject(this.publicKey);
            System.out.println("Public Key Sent to Merchant!");

            /* Merchant's Public Key -> Payment Gateway */
            this.mPublicKey = (PublicKey)this.fromM.readObject();
            System.out.println("Received Merchant's Public Key: " + this.mPublicKey);

            /* Merchant's Secret Key -> Payment Gateway */
            byte[] mEncrSecretKey = (byte[])this.fromM.readObject();
            byte[] mDecrSecretKey = Base64.getDecoder().decode(
                    cryptoUtils.decryptWithPrivKey(mEncrSecretKey, this.privateKey)
            );
            this.mSecretKey = new SecretKeySpec(mDecrSecretKey, 0, mDecrSecretKey.length, "AES");
            System.out.println("Received Merchant's Secret Key: " + this.mSecretKey);

            /* Merchant's PM and SigM(SID, PukC, Amount) -> Payment Gateway */
            byte[] encrMsg = (byte[])this.fromM.readObject();

            String msg = cryptoUtils.decryptWithSecretKey(encrMsg, this.mSecretKey);

            String[] msgArr = msg.split("_", 2);
            final String PM = msgArr[0];
            final byte[] SIG_M = Base64.getDecoder().decode(msgArr[1].getBytes());

            System.out.println("Received Customer's PM: " + PM);

            String[] pmArr = PM.split("-");
            final String CARD_N_PM = pmArr[0];
            final String CARD_EXP_PM = pmArr[1];
            final String C_CODE_PM = pmArr[2];
            final String SID_PM = pmArr[3];
            final String AMOUNT_PM = pmArr[4];
            final String C_PUBLIC_KEY_PM = pmArr[5];
            final String NC_PM = pmArr[6];
            final String M_PM = pmArr[7];
            byte[] pmDigestEncr = Base64.getDecoder().decode(pmArr[8].getBytes());

            byte[] cPublicKeyByteArr = Base64.getDecoder().decode(C_PUBLIC_KEY_PM.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cPublicKeyByteArr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey cPublicKey = keyFactory.generatePublic(keySpec);

            final String PM_FOR_DIGEST = CARD_N_PM + CARD_EXP_PM + C_CODE_PM + SID_PM + AMOUNT_PM + C_PUBLIC_KEY_PM + NC_PM + M_PM;

            byte[] pmDigestByteArr = md5.digest(PM_FOR_DIGEST.getBytes());

            String pmDigestDecrStr = cryptoUtils.decryptWithPubKey(pmDigestEncr, cPublicKey);
            byte[] pmDigestDecrByteArr = Base64.getDecoder().decode(pmDigestDecrStr.getBytes());

            if (!Arrays.equals(pmDigestByteArr, pmDigestDecrByteArr)) {
                System.err.println("Exchange Sub-Protocol Failed: Customer's PM Hash Mismatch!");
                return false;
            }

            System.out.println("Customer's PM Hash Match!");

            final String SIG_M_FOR_DIGEST = SID_PM + C_PUBLIC_KEY_PM + AMOUNT_PM;

            byte[] sigMDigestByteArr = md5.digest(SIG_M_FOR_DIGEST.getBytes());

            String sigMDigestDecrStr = cryptoUtils.decryptWithPubKey(SIG_M, this.mPublicKey);
            byte[] sigMDigestDecrByteArr = Base64.getDecoder().decode(sigMDigestDecrStr.getBytes());

            if (!Arrays.equals(sigMDigestByteArr, sigMDigestDecrByteArr)) {
                System.err.println("Exchange Sub-Protocol Failed: SigM Hash Mismatch!");
                return false;
            }

            System.out.println("SigM Hash Match!");

            /* Payment Gateway's Resp, Sid, SigP -> Merchant */
            final String PG_RESP = "YES";

            final String RESP_FOR_DIGEST = PG_RESP + SID_PM + AMOUNT_PM + NC_PM;

            byte[] respDigestByteArr = md5.digest(RESP_FOR_DIGEST.getBytes());
            String respDigestStr = Base64.getEncoder().encodeToString(respDigestByteArr);
            byte[] respDigestEncrByteArr = cryptoUtils.encryptWithPrivKey(respDigestStr, this.privateKey);
            String respDigestEncrStr = Base64.getEncoder().encodeToString(respDigestEncrByteArr);

            msg = PG_RESP + "-" + SID_PM + "-" + respDigestEncrStr;
            encrMsg = cryptoUtils.encryptWithSecretKey(msg, this.mSecretKey);

            this.toM.writeObject(encrMsg);
            System.out.println("Response Sent to Merchant!");

            this.mSocket.close();

            System.out.println("Exchange Sub-Protocol Succeeded!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exchange Sub-Protocol Failed!");
            return false;
        }
    }
}
