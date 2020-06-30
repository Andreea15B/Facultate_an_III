import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Random;

public class Merchant {
    public static void main(String... argv) throws Exception {

        // generating RSA keys
        KeyPair pair = RSA.generateKeyRSA();
        PublicKey merchantPublicKey = pair.getPublic(); // PubKM
        PrivateKey merchantPrivateKey = pair.getPrivate();

        int port = 4021;
        ServerSocket server = new ServerSocket(port);
        System.out.println("The merchant is listening on port " + port);
        // creating socket and waiting for client connection
        Socket socket = server.accept();

        ObjectOutputStream oos;
        ObjectInputStream ois;

        // receiving the client's public key (PubKC)
        ois = new ObjectInputStream(socket.getInputStream());
        PublicKey clientPublicKey = (PublicKey) ois.readObject();
        System.out.println("\nReceived the client's public key: " + clientPublicKey);

        // sending the merchant's public key to client
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(merchantPublicKey);
        System.out.println("\nSent the public key of the merchant to the client: \n" + merchantPublicKey);

        // receiving the encrypted AES secret key from client
        ois = new ObjectInputStream(socket.getInputStream());
        byte[] encrypted_AESsecretKey = (byte[]) ois.readObject();
        // decrypting the received key
        String string_decrypted_AESsecretKey = RSA.decrypt(encrypted_AESsecretKey, merchantPrivateKey);
        byte[] decrypted_AESsecretKey = Base64.getDecoder().decode(string_decrypted_AESsecretKey);
        SecretKeySpec clientSecretKey = new SecretKeySpec(decrypted_AESsecretKey, 0, decrypted_AESsecretKey.length, "AES"); // constructs a AES SecretKey from byte[]
        System.out.println("Received client's secret key: " + clientSecretKey);

        Random rand = new Random();
        int Sid = rand.nextInt(1000); // unique session ID

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] Sid_digest = md5.digest(Integer.toString(Sid).getBytes());
        String Sid_digest_string = Base64.getEncoder().encodeToString(Sid_digest);
        byte[] Sid_signed = RSA.encrypt(Sid_digest_string, merchantPrivateKey); // encrypting the Sid with the merchant's private key
        String Sid_signed_string = Base64.getEncoder().encodeToString(Sid_signed);
        String Sid_message = Sid + "-" + Sid_signed_string; // concatenate the Sid with the signed Sid
        byte[] Sid_message_encrypted = AES.encrypt(Sid_message, clientSecretKey); // encrypting the message to be sent to the client

        // sending Sid to client
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(Sid_message_encrypted);
        System.out.println("\nSent the Sid and the signed Sid to the client: \n" + Sid_message);

        socket.close();
        InetAddress host = InetAddress.getLocalHost();
        socket = new Socket(host.getHostName(), 4040);
        System.out.println("The merchant is connected to PG.");

        // receiving the PG public key
        ois = new ObjectInputStream(socket.getInputStream());
        PublicKey pgPublicKey = (PublicKey) ois.readObject();
        System.out.println("\nReceived PG public key: " + pgPublicKey);

        // sending the public key to PG
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(merchantPublicKey);
        System.out.println("\nSent the public key of the merchant to PG: \n" + merchantPublicKey);
    }
}
