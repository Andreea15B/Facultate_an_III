package utils;

import sun.plugin2.gluegen.runtime.CPU;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    public byte[] encryptWithPubKey(final String plainText, final PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(plainText.getBytes());
    }

    public byte[] encryptWithPrivKey(final String plainText, final PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(plainText.getBytes());
    }

    public byte[] encryptWithSecretKey(final String plainText, final SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainText.getBytes());
    }
    
    public String decryptWithPrivKey(final byte[] cryptoText, final PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        return new String(cipher.doFinal(cryptoText), StandardCharsets.UTF_8);
    }

    public String decryptWithPubKey(final byte[] cryptoText, final PublicKey base64PublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, base64PublicKey);

        return new String(cipher.doFinal(cryptoText), StandardCharsets.UTF_8);
    }

    public String decryptWithSecretKey(final byte[] cryptoText, final SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return new String(cipher.doFinal(cryptoText), StandardCharsets.UTF_8);
    }
}
