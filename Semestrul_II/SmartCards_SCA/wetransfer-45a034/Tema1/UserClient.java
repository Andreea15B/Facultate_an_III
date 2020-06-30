import PayWord.Card;
import PayWord.RSA;

import java.io.*;
import java.math.BigInteger;
import java.net.*;


public class UserClient {
        static BigInteger publicKeyE;
        static BigInteger publicKeyN;

        static int serverPort = 4020;
        static Socket socket = null;
        static String serverPublicKeyE;
        static String serverPublicKeyN;
        static ObjectOutputStream toServer = null;
        static ObjectInputStream fromServer = null;
        int salerPort = 4021;
        Socket socket1 = null;
        static RSA myRsa;
        ObjectOutputStream toServer1 = null;
        ObjectInputStream fromServer1 = null;
        static Card myCard;
        static String myIdentity;
        public UserClient(String nume,String cardNo,String expDate,int cvv,int cLimit)
        {
            this.myRsa=new RSA(1024);
            publicKeyE=myRsa.getE();
            publicKeyN=myRsa.getN();
            this.myCard=new Card(nume,cardNo,expDate,cvv,cLimit);
        }
    public static void main(String[] args)
        {
            byte[] resultBuff;
            byte[] buff;
            UserClient usr=new UserClient("Vasile","1231","16/8",122,100);
            try {
                //conexiunea
                InetAddress serverHost = InetAddress.getByName("localhost");
                System.out.println("Connecting to server on port " + serverPort);
                socket = new Socket(serverHost, serverPort);
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());


                //trimit cheia mea la server
                String keyToSendE=publicKeyE.toString();
                socket.getOutputStream().write(keyToSendE.getBytes());
                socket.getOutputStream().flush();

                String keyToSendN=publicKeyN.toString();
                socket.getOutputStream().write(keyToSendN.getBytes());
                socket.getOutputStream().flush();

                //primesc cheia lui

                resultBuff = new byte[0];
                buff = new byte[1024];
                int k = -1;
                while((k = socket.getInputStream().read(buff, 0, buff.length)) > -1) {
                    byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size = bytes already read + bytes last read
                    System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy previous bytes
                    System.arraycopy(buff, 0, tbuff, resultBuff.length, k);  // copy current lot
                    resultBuff = tbuff; // call the temp buffer as your result buff
                }
                serverPublicKeyE=new String(resultBuff,"UTF-8");

                resultBuff = new byte[0];
                buff = new byte[1024];
                k = -1;
                while((k = socket.getInputStream().read(buff, 0, buff.length)) > -1) {
                    byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size = bytes already read + bytes last read
                    System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy previous bytes
                    System.arraycopy(buff, 0, tbuff, resultBuff.length, k);  // copy current lot
                    resultBuff = tbuff; // call the temp buffer as your result buff
                }
                serverPublicKeyN=new String(resultBuff,"UTF-8");

                serverPublicKeyN=serverPublicKeyE.substring(0,1);
                serverPublicKeyE=serverPublicKeyE.substring(5);
                System.out.println("srv keyN "+serverPublicKeyN);
                System.out.println("srv keyE "+serverPublicKeyE);

//                //trimit identitatea mea
//                toServer.reset();
//                myRsa=new PayWord.RSA(new BigInteger(serverPublicKeyN),new BigInteger(serverPublicKeyE));
//                myIdentity=myRsa.encrypt(myCard.toString());
//                System.out.println(myIdentity);
//                toServer.writeBytes(myIdentity);
//                toServer.flush();

                //trimit obiect
//                PayWord.Card msgToSend = new PayWord.Card("Vasile","1231","16/8",122,100);
//                toServer.writeObject(msgToSend);
//                toServer.flush();
//                //iau de la server obiect
//
//                PayWord.Card msgFromReply = (PayWord.Card) fromServer.readObject();
//                System.out.println(msgFromReply.clientName +" i am conected with bankserver.i am client");

            } catch (IOException e) {
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