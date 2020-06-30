import java.io.*;
import java.math.BigInteger;
import java.net.*;


    public class UserClient {
        static BigInteger publicKey;
        static String serverPublicKey;
        int serverPort = 4020;
        Socket socket = null;
        ObjectOutputStream toServer = null;
        ObjectInputStream fromServer = null;
        int salerPort = 4021;
        Socket socket1 = null;
        ObjectOutputStream toServer1 = null;
        ObjectInputStream fromServer1 = null;
        Card myCard;
        public UserClient(String nume,String cardNo,String expDate,int cvv,int cLimit)
        {
            RSA rsaClient=new RSA(1024);
            publicKey=rsaClient.getE();
            this.myCard=new Card(nume,cardNo,expDate,cvv,cLimit);
        }
        public void connectWithBank()
        {
            try {
                //conexiunea
                InetAddress serverHost = InetAddress.getByName("localhost");
                System.out.println("Connecting to server on port " + serverPort);
                socket = new Socket(serverHost, serverPort);
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());

                //trimit cheia mea la server
                toServer = new ObjectOutputStream(
                        new BufferedOutputStream(socket.getOutputStream()));
                String keyToSend=publicKey.toString();
                toServer.writeBytes(keyToSend);
                toServer.flush();

                //primesc cheia lui
//                serverPublicKey= fromServer.readLine();
//                System.out.println(serverPublicKey+" server public key");


                //trimit obiect
                Card msgToSend = new Card("Vasile","1231","16/8",122,100);
                toServer.writeObject(msgToSend);
                toServer.flush();
                //iau de la server obiect
                fromServer = new ObjectInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                Card msgFromReply = (Card) fromServer.readObject();
                System.out.println(msgFromReply.clientName +" i am conected with bankserver.i am client");
                String serverPublicKey= fromServer.readLine();
                System.out.println(serverPublicKey+" server public key");

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void connectWithSaler()
        {
            try {
                int number = 16;
                InetAddress salerHost = InetAddress.getByName("localhost");
                System.out.println("Connecting to server on port " + salerPort);
                socket1 = new Socket(salerHost, salerPort);
                System.out.println("Just connected to " + socket1.getRemoteSocketAddress());
                toServer1 = new ObjectOutputStream(
                        new BufferedOutputStream(socket1.getOutputStream()));
                Card msgToSend1 = new Card("Vasile","1231","16/8",122,100);
                toServer1.writeObject(msgToSend1);
                toServer1.flush();

                // This will block until the corresponding ObjectOutputStream
                // in the server has written an object and flushed the header
                fromServer1 = new ObjectInputStream(
                        new BufferedInputStream(socket1.getInputStream()));
                Card msgFromReply1 = (Card) fromServer1.readObject();
                System.out.println(number + " * " + number + " = " + msgFromReply1.cvv+" i am conected with seler.i am client");


            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            } finally {
                if (socket1 != null) {
                    try {
                        socket1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }