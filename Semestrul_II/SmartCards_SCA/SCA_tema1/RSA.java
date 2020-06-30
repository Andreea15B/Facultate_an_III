import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSA {
    public static KeyPair generateKeyRSA() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        return pair;
    }

    public static byte[] encrypt(String plainText, Key key) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptCipher.doFinal(plainText.getBytes());
    }

    public static String decrypt(byte[] cipherText, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
    }
}