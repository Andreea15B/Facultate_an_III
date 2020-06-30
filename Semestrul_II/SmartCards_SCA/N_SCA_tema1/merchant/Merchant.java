package merchant;

import sun.plugin2.message.Message;
import utils.CryptoUtils;

import java.io.*;
import java.lang.annotation.Retention;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.bind.DatatypeConverter;

public class Merchant {
    private final int PORT                 = 3030;
    private final int PAYMENT_GATEWAY_PORT = 4040;

    private final String SID = "9";

    private ServerSocket socket;
    private PublicKey    publicKey;
    private PrivateKey   privateKey;
    private SecretKey    secretKey;

    private Socket             cSocket;
    private ObjectInputStream  fromC;
    private ObjectOutputStream toC;
    private PublicKey          cPublicKey;
    private SecretKey          cSecretKey;

    private Socket             pgSocket;
    private ObjectInputStream  fromPg;
    private ObjectOutputStream toPg;
    private PublicKey          pgPublicKey;
    
    public boolean init() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);

            KeyPair keyPair = keyPairGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            System.out.println("RSA Public Key: " + this.publicKey + "\nRSA Private Key: " + this.privateKey);
            
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            this.secretKey = keyGen.generateKey();
            System.out.println("AES Secret Key: " + this.secretKey);

            this.socket = new ServerSocket(this.PORT);
            System.out.println("Merchant listening at PORT " + this.PORT);

            return true;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void run() {
        boolean retCode;

        retCode = performSetupSubProtocol();

        if (retCode) {
            retCode = performExchangeSubProtocol();
        }

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

    private boolean performSetupSubProtocol() {
        try {
            this.cSocket = this.socket.accept();
            System.out.println("Customer Socket: " + this.cSocket.toString());

            this.toC = new ObjectOutputStream(this.cSocket.getOutputStream());
            this.fromC = new ObjectInputStream(this.cSocket.getInputStream());

            CryptoUtils cryptoUtils = new CryptoUtils();

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            /* Merchant's Public Key -> Customer */
            this.toC.writeObject(this.publicKey);
            System.out.println("Public Key Sent!");

            /* Customer's Public Key -> Merchant */
            this.cPublicKey = (PublicKey)this.fromC.readObject();
            System.out.println("Received Customer's Public Key: " + this.cPublicKey);

            /* Customer's Secret Key -> Merchant */
            byte[] cEncrSecretKey = (byte[])this.fromC.readObject();
            byte[] cDecrSecretKey = Base64.getDecoder().decode(
                    cryptoUtils.decryptWithPrivKey(cEncrSecretKey, this.privateKey)
            );
            this.cSecretKey = new SecretKeySpec(cDecrSecretKey, 0, cDecrSecretKey.length, "AES");
            System.out.println("Received Customer's Secret Key: " + this.cSecretKey);

            /* Merchant SID -> Customer */
            byte[] sIdDigestByteArr = md5.digest(this.SID.getBytes());
            String sIdDigestStr = Base64.getEncoder().encodeToString(sIdDigestByteArr);
            byte[] sIdDigestSignedByteArr = cryptoUtils.encryptWithPrivKey(sIdDigestStr, this.privateKey);
            String sIdDigestSignedStr = Base64.getEncoder().encodeToString(sIdDigestSignedByteArr);

            String sIdMsg = this.SID + "-" + sIdDigestSignedStr;
            byte[] sIdMsgEncr = cryptoUtils.encryptWithSecretKey(sIdMsg, this.cSecretKey);

            this.toC.writeObject(sIdMsgEncr);
            System.out.println("SID Sent to Customer: " + sIdMsg);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exchange Sub-Protocol Failed!");
            return false;
        }
    }

    private boolean performExchangeSubProtocol() {
        try {
            CryptoUtils cryptoUtils = new CryptoUtils();

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            /* Client's PM, PO -> Merchant */
            byte[] encrMsg = (byte[])this.fromC.readObject();

            String msg = cryptoUtils.decryptWithSecretKey(encrMsg, this.cSecretKey);

            String[] msgArr = msg.split("_");
            final String PM = msgArr[0];
            final String PO = msgArr[1];

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

            final String PM_FOR_DIGEST = CARD_N_PM + CARD_EXP_PM + C_CODE_PM + SID_PM + AMOUNT_PM + C_PUBLIC_KEY_PM + NC_PM + M_PM;

            byte[] pmDigestByteArr = md5.digest(PM_FOR_DIGEST.getBytes());

            String pmDigestDecrStr = cryptoUtils.decryptWithPubKey(pmDigestEncr, this.cPublicKey);
            byte[] pmDigestDecrByteArr = Base64.getDecoder().decode(pmDigestDecrStr.getBytes());

            if (!Arrays.equals(pmDigestByteArr, pmDigestDecrByteArr)) {
                System.err.println("Exchange Sub-Protocol Failed: PM Hash Mismatch!");
                return false;
            }

            System.out.println("PM Hash Match!");

            String[] poArr = PO.split("-");
            final String ORDER_DESC_PO = poArr[0];
            final String SID_PO = poArr[1];
            final String AMOUNT_PO = poArr[2];
            final String NC_PO = poArr[3];
            byte[] poDigestEncr = Base64.getDecoder().decode(poArr[4].getBytes());

            final String PO_FOR_DIGEST = ORDER_DESC_PO + SID_PO + AMOUNT_PO + NC_PO;

            byte[] poDigestByteArr = md5.digest(PO_FOR_DIGEST.getBytes());

            String poDigestDecrStr = cryptoUtils.decryptWithPubKey(poDigestEncr, this.cPublicKey);
            byte[] poDigestDecrByteArr = Base64.getDecoder().decode(poDigestDecrStr);

            if (!Arrays.equals(poDigestByteArr, poDigestDecrByteArr)) {
                System.err.println("Exchange Sub-Protocol Failed: PO Hash Mismatch!");
                return false;
            }

            System.out.println("PO Hash Match!");

            this.pgSocket = new Socket("localhost", this.PAYMENT_GATEWAY_PORT);
            System.out.println("Connected to Payment Gateway on Socket: " + this.pgSocket.toString());

            this.fromPg = new ObjectInputStream(this.pgSocket.getInputStream());
            this.toPg = new ObjectOutputStream(this.pgSocket.getOutputStream());

            /* Payment Gateway's Public Key -> Merchant */
            this.pgPublicKey = (PublicKey)fromPg.readObject();
            System.out.println("Received Payment Gateway's Public Key: " + this.pgPublicKey);

            /* Merchant's Public Key -> Payment Gateway */
            this.toPg.writeObject(this.publicKey);
            System.out.println("Public Key Sent to Payment Gateway!");

            /* Merchant's Secret Key -> Payment Gateway */
            byte[] encryptedSecretKey = cryptoUtils.encryptWithPubKey(
                    Base64.getEncoder().encodeToString(this.secretKey.getEncoded()),
                    this.pgPublicKey
            );
            this.toPg.writeObject(encryptedSecretKey);
            System.out.println("Secret Key Sent to Payment Gateway!");

            /* Merchant's PM and SigM(SID, PukC, Amount) -> Payment Gateway */
            final String SIG_M_FOR_DIGEST = SID_PM + C_PUBLIC_KEY_PM + AMOUNT_PM;

            byte[] sigMDigestByteArr = md5.digest(SIG_M_FOR_DIGEST.getBytes());
            String sigMDigestStr = Base64.getEncoder().encodeToString(sigMDigestByteArr);
            byte[] sigMDigestEncrByteArr = cryptoUtils.encryptWithPrivKey(sigMDigestStr, this.privateKey);
            String sigMDigestEncrStr = Base64.getEncoder().encodeToString(sigMDigestEncrByteArr);

            msg = PM + "_" + sigMDigestEncrStr;
            encrMsg = cryptoUtils.encryptWithSecretKey(msg, this.secretKey);

            this.toPg.writeObject(encrMsg);
            System.out.println("PM and SigM Sent to Payment Gateway!");

            /* Payment Gateway's Resp, Sid, SigP -> Merchant */
            byte[] respEncr = (byte[])this.fromPg.readObject();

            String resp = cryptoUtils.decryptWithSecretKey(respEncr, this.secretKey);

            String[] respArr = resp.split("-");
            final String RESP_PG = respArr[0];
            final String SID_PG = respArr[1];
            byte[] RESP_SIG_PG = Base64.getDecoder().decode(respArr[2].getBytes());

            System.out.println("Received Payment Gateway Response: " + RESP_PG);

            final String RESP_FOR_DIGEST = RESP_PG + SID_PG + AMOUNT_PM + NC_PM;

            byte[] respDigestByteArr = md5.digest(RESP_FOR_DIGEST.getBytes());

            String sigPgDecrStr = cryptoUtils.decryptWithPubKey(RESP_SIG_PG, this.pgPublicKey);
            byte[] sigPgDecrByteArr = Base64.getDecoder().decode(sigPgDecrStr.getBytes());

            if (!Arrays.equals(respDigestByteArr, sigPgDecrByteArr)) {
                System.err.println("Exchange Sub-Protocol Failed: Payment Gateway's Response Hash Mismatch!");
                return false;
            }

            System.out.println("Payment Gateway's Response Hash Match!");

            this.pgSocket.close();

            /* Merchant's Public Key -> Payment Gateway */
            this.toC.writeObject(this.pgPublicKey);
            System.out.println("PG Public Key Sent to Customer!");

            /* Resp, SID, SigPG -> Customer */
            respEncr = cryptoUtils.encryptWithSecretKey(resp, this.cSecretKey);
            this.toC.writeObject(respEncr);
            System.out.println("PG Response Sent to Customer!");

            this.cSocket.close();

            System.out.println("Exchange Sub-Protocol Succeeded!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exchange Sub-Protocol Failed!");
            return false;
        }
    }
}
