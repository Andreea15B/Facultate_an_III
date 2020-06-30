package customer;

import utils.CryptoUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import javax.crypto.*;

public class Customer {
    private final int MERCHANT_PORT = 3030;

    private final String CARD_N    = "400.300.200.100";
    private final String CARD_EXP  = "3/6/2020";
    private final String C_CODE    = "890";
    private final String AMOUNT    = "300";
    private final String NC        = "CUSTOMER";
    private final String M         = "MERCHANT";
    private final String ODER_DESC = "001101";

    private String SID;

    private PublicKey  publicKey;
    private PrivateKey privateKey;
    private SecretKey  secretKey;

    private Socket             mSocket;
    private ObjectInputStream  fromM;
    private ObjectOutputStream toM;

    private PublicKey mPublicKey;
    private PublicKey pgPublicKey;

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

            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void run() {
        boolean retStatus;

        retStatus = performSetupSubProtocol();

        if (retStatus) {
            retStatus = performExchangeSubProtocol();
        }

        if (retStatus) {
            System.out.println("E-Commerce Transaction Succeeded!");
        }
        else {
            System.err.println("E-Commerce Transaction Failed!");
        }
    }

    private boolean performSetupSubProtocol() {
        try {
            this.mSocket = new Socket("localhost", this.MERCHANT_PORT);
            System.out.println("Connected to Merchant on Socket: " + this.mSocket.toString());

            this.fromM = new ObjectInputStream(this.mSocket.getInputStream());
            this.toM = new ObjectOutputStream(this.mSocket.getOutputStream());

            CryptoUtils cryptoUtils = new CryptoUtils();

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            /* Merchant's Public Key -> Customer */
            this.mPublicKey = (PublicKey)fromM.readObject();
            System.out.println("Received Merchant's Public Key: " + mPublicKey);

            /* Customer's Public Key -> Merchant */
            this.toM.writeObject(this.publicKey);
            System.out.println("Public Key Sent to Merchant!");

            /* Customer's Secret Key -> Merchant */
            byte[] encryptedSecretKey = cryptoUtils.encryptWithPubKey(
                    Base64.getEncoder().encodeToString(this.secretKey.getEncoded()),
                    this.mPublicKey
            );
            this.toM.writeObject(encryptedSecretKey);
            System.out.println("Secret Key Sent to Merchant!");

            /* Merchant's SID -> Customer */
            byte[] sIdMsgEncr = (byte[])this.fromM.readObject();

            String sIdMsg = cryptoUtils.decryptWithSecretKey(sIdMsgEncr, this.secretKey);
            String[] sIdMsgArr = sIdMsg.split("-", 2);

            this.SID = sIdMsgArr[0];
            byte[] sIdDigestEncr = Base64.getDecoder().decode(sIdMsgArr[1].getBytes());

            System.out.println("Received SID from Merchant: " + this.SID);

            String sIdDigestStr = cryptoUtils.decryptWithPubKey(sIdDigestEncr, this.mPublicKey);
            byte[] sIdDigestByteArr = Base64.getDecoder().decode(sIdDigestStr.getBytes());

            byte[] sIdDigestComputedByteArr = md5.digest(this.SID.getBytes());

            if (!Arrays.equals(sIdDigestComputedByteArr, sIdDigestByteArr)) {
                System.err.println("Setup Sub-Protocol Failed: SID Hash Mismatch!");
                return false;
            }

            System.out.println("SID Hash Match! Setup Sub-Protocol Succeeded!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Setup Sub-Protocol Failed!");
            return false;
        }
    }

    private boolean performExchangeSubProtocol() {
        try {
            CryptoUtils cryptoUtils = new CryptoUtils();

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            String publicKeyStr = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

            /* Client's PM, PO -> Merchant */
            final String PI_FOR_DIGEST = this.CARD_N + this.CARD_EXP + this.C_CODE + this.SID + this.AMOUNT + publicKeyStr + this.NC + this.M;

            byte[] piDigestByteArr = md5.digest(PI_FOR_DIGEST.getBytes());
            String piDigestStr = Base64.getEncoder().encodeToString(piDigestByteArr);
            byte[] piDigestSignedByteArr = cryptoUtils.encryptWithPrivKey(piDigestStr, this.privateKey);
            String piDigestSignedStr = Base64.getEncoder().encodeToString(piDigestSignedByteArr);

            final String PM = this.CARD_N + "-" + this.CARD_EXP + "-" + this.C_CODE + "-" + this.SID + "-" + this.AMOUNT + "-" + publicKeyStr + "-" + this.NC + "-" + this.M + "-" + piDigestSignedStr;

            final String PO_FOR_DIGEST = this.ODER_DESC + this.SID + this.AMOUNT + this.NC;

            byte[] poDigestByteArr = md5.digest(PO_FOR_DIGEST.getBytes());
            String poDigestStr = Base64.getEncoder().encodeToString(poDigestByteArr);
            byte[] poDigestSignedByteArr = cryptoUtils.encryptWithPrivKey(poDigestStr, this.privateKey);
            String poDigestSignedStr = Base64.getEncoder().encodeToString(poDigestSignedByteArr);

            final String PO = this.ODER_DESC + "-" + this.SID + "-" + this.AMOUNT + "-" + this.NC + "-" + poDigestSignedStr;

            final String MSG = PM + "_" + PO;
            byte[] encrMsg = cryptoUtils.encryptWithSecretKey(MSG, this.secretKey);

            this.toM.writeObject(encrMsg);
            System.out.println("PM and PO sent to Merchant!");

            /* Payment Gateway's Public Key -> Customer */
            this.pgPublicKey = (PublicKey)this.fromM.readObject();
            System.out.println("Received Payment Gateway's Public Key: " + this.pgPublicKey);

            /* Payment Gateway's Response -> Customer */
            byte[] respMsgEncr = (byte[])this.fromM.readObject();

            String respMsg = cryptoUtils.decryptWithSecretKey(respMsgEncr, this.secretKey);

            String[] respMsgArr = respMsg.split("-");
            final String RESP_PG = respMsgArr[0];
            final String SID_PG = respMsgArr[1];
            final byte[] SIG_PG = Base64.getDecoder().decode(respMsgArr[2].getBytes());

            System.out.println("Received Payment Gateway's Response from Merchant: " + RESP_PG);

            String respMsgForDigest = RESP_PG + SID_PG + this.AMOUNT + this.NC;

            byte[] respMsgDigestByteArr = md5.digest(respMsgForDigest.getBytes());

            String sigPgDecrStr = cryptoUtils.decryptWithPubKey(SIG_PG, this.pgPublicKey);
            byte[] sigPgDecrByteArr = Base64.getDecoder().decode(sigPgDecrStr.getBytes());

            if (!Arrays.equals(respMsgDigestByteArr, sigPgDecrByteArr)) {
                System.err.println("Exchange Sub-Protocol Failed: Payment Gateway's Response Hash Mismatch!");
                return false;
            }

            System.out.println("Payment Gateway's Response Hash Match!");

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
