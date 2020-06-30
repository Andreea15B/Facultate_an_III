import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

public class Client {
    public static void main(String... argv) throws Exception {
        // generating RSA keys
        KeyPair pair = RSA.generateKeyRSA();
        PublicKey clientPublicKey = pair.getPublic(); // PubKC
        PrivateKey clientPrivateKey = pair.getPrivate();

        SecretKey AESsecretKey = AES.generateKeyAES(); // K key

        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 4021);
        System.out.println("The client is connected to merchant.");

        ObjectOutputStream oos;
        ObjectInputStream ois;

        // sending the public key to the merchant
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(clientPublicKey);
        System.out.println("\nSent the public key of the client to the merchant: \n" + clientPublicKey);

        // receiving the merchant's public key
        ois = new ObjectInputStream(socket.getInputStream());
        PublicKey merchantPublicKey = (PublicKey) ois.readObject();
        System.out.println("\nReceived the merchant's public key: " + merchantPublicKey);


        // encrypting the AES secret key with the merchant's public key
        String string_AESsecretKey = Base64.getEncoder().encodeToString(AESsecretKey.getEncoded());
        byte[] encrypted_AESsecretKey = RSA.encrypt(string_AESsecretKey, merchantPublicKey);

        // sending the AES secret key to the merchant
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(encrypted_AESsecretKey);
        System.out.println("\nSent the encrypted AES secret key of the client to the merchant: " + encrypted_AESsecretKey);

        // receiving Sid and signed Sid from merchant
        ois = new ObjectInputStream(socket.getInputStream());
        byte[] Sid_message_encrypted = (byte[]) ois.readObject();
        System.out.println("\nReceived the Sid and signed Sid from merchant: " + Sid_message_encrypted);

        String Sid_message = AES.decrypt(Sid_message_encrypted, AESsecretKey);
        String[] Sid_message_splitted = Sid_message.split("-", 2);
        String Sid = Sid_message_splitted[0];
        byte[] Sid_digest_encrypted = Base64.getDecoder().decode(Sid_message_splitted[1].getBytes());
        System.out.println("\nSid from merchant: " + Sid);

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        String Sid_digest_string = RSA.decrypt(Sid_digest_encrypted, merchantPublicKey);
        byte[] Sid_digest = Base64.getDecoder().decode(Sid_digest_string.getBytes());
        byte[] Sid_digest_computed = md5.digest(Sid.getBytes());

        if (!Arrays.equals(Sid_digest_computed, Sid_digest))
            System.err.println("The Setup Sub-Protocol has failed because the Sid and signed Sid don't match!");
        else System.out.println("The Setup Sub-Protocol has succeeded - the Sid and signed Sid match!");
    }

}
