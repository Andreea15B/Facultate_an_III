import java.math.BigInteger;
import java.net.*;
import java.io.*;

public class BankServer {
    static BigInteger publicKey;
    static int serverPort = 4020;
    static ServerSocket serverSocket = null;
    static ObjectOutputStream toClient = null;
    static ObjectInputStream fromClient = null;
    public static void main(String[] args)
    {
        RSA rsaServer=new RSA(1024);
        publicKey=rsaServer.getE();
        try {
            serverSocket = new ServerSocket(serverPort);
            while(true) {

                Socket socket = serverSocket.accept();
                System.out.println("Just connected to " +
                        socket.getRemoteSocketAddress());
                toClient = new ObjectOutputStream(
                        new BufferedOutputStream(socket.getOutputStream()));
                fromClient = new ObjectInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                //primesc cheia
//                String userPublicKey=fromClient.readLine();
//                System.out.println(userPublicKey+" user public key");
                Commit msgCommit= (Commit) fromClient.readObject();
                SHA sha=new SHA(msgCommit.toString());
                System.out.println(sha);
                System.out.println(msgCommit.card + " " + msgCommit.token.toString());
//                //Trimit cheia
////                String keyToSend=publicKey.toString();
////                toClient.writeBytes(keyToSend);
////                toClient.flush();
//
//                //primesc obiect
//                Card msgRequest = (Card) fromClient.readObject();
//
//
//                //scriu obiect
//                System.out.println(msgRequest.clientName);
//                toClient.writeObject(new Card("Vasilache","1231","16/8",122,100));
//
//                toClient.flush();
//
//                String keyToSend=publicKey.toString();
//                toClient.writeBytes(keyToSend);
//                toClient.flush();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

