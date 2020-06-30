import PayWord.RSA;

import java.math.BigInteger;
import java.net.*;
import java.io.*;

public class BankServer {
    static BigInteger publicKeyE;
    static BigInteger publicKeyN;
    static int serverPort = 4020;
    static RSA myRsa;
    static String userPublicKeyE;
    static String userPublicKeyN;
    public static void main(String[] args)
    {
        ServerSocket serverSocket = null;
        ObjectOutputStream toClient = null;
        ObjectInputStream fromClient = null;
        myRsa=new RSA(1024);
        publicKeyE=myRsa.getE();
        publicKeyN=myRsa.getN();
        try {
            serverSocket = new ServerSocket(serverPort);
            while(true) {

                Socket socket = serverSocket.accept();
                System.out.println("Just connected to " +
                        socket.getRemoteSocketAddress());
//                fromClient = new ObjectInputStream(
//                        new BufferedInputStream(socket.getInputStream()));
//                toClient = new ObjectOutputStream(
//                        new BufferedOutputStream(socket.getOutputStream()));


                //primesc cheia
                byte [] message = new byte[0];
                int messageLength=socket.getInputStream().read(message);
                message = new byte[messageLength];

                userPublicKeyE=new String(message,"UTF-8");
                socket.getInputStream().read(message);
                userPublicKeyN=new String(message,"UTF-8");
//                userPublicKeyN=userPublicKeyE.substring(0,1);
//                userPublicKeyE=userPublicKeyE.substring(1);
                System.out.println("usr keyN "+userPublicKeyN);
                System.out.println("usr keyE "+userPublicKeyE);

//                //Trimit cheia
//                String keyToSend=publicKeyE.toString();
//                toClient.writeObject(keyToSend);
//                toClient.flush();
//                keyToSend=new String(publicKeyN.toString());
//                toClient.writeObject(keyToSend);
//                toClient.flush();
//
//                //primesc identitatea clientului
//
//                String clientIdentity="";
//                socket.getInputStream().read(message);
//                System.out.println("cl idendity"+message);


                //primesc obiect
//                PayWord.Card msgRequest = (PayWord.Card) fromClient.readObject();
//
//
//                //scriu obiect
//                System.out.println(msgRequest.clientName);
//                toClient.writeObject(new PayWord.Card("Vasilache","1231","16/8",122,100));
//
//                toClient.flush();


            }
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

