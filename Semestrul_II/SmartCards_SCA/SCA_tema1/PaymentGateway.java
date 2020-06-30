import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PaymentGateway {
    public static void main(String... argv) throws Exception {
        // generating RSA keys
        KeyPair pair = RSA.generateKeyRSA();
        PublicKey pgPublicKey = pair.getPublic();
        PrivateKey pgPrivateKey = pair.getPrivate();

        int port = 4040;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Payment Gateway listening on port " + port);
        // creating socket and waiting for client connection
        Socket socket = server.accept();

        ObjectOutputStream oos;
        ObjectInputStream ois;

        // sending the public key to the merchant
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(pgPublicKey);
        System.out.println("\nSent the public key of the PG to the merchant: \n" + pgPublicKey);

        // receiving the merchant's public key
        ois = new ObjectInputStream(socket.getInputStream());
        PublicKey merchantPublicKey = (PublicKey) ois.readObject();
        System.out.println("\nReceived merchant's public key: " + merchantPublicKey);

    }
}
