import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Saler1 {
    static ArrayList<Commit> commit=new ArrayList<>();
    public static void Commit(Card Identitate, Token token){
        Commit obj=new Commit();
        obj.card=Identitate;
        obj.token=token;
        commit.add(obj);

    }



    public static void main(String[] args) {
        int salerPort = 4021;
        int serverPort = 4020;
        ServerSocket serverSocket = null;
        ObjectOutputStream toClient = null;
        ObjectInputStream fromClient = null;
//        try {
//                serverSocket = new ServerSocket(salerPort);
//                //while(true) {
//                Socket socket = serverSocket.accept();
//                System.out.println("Just connected to " +
//                        socket.getRemoteSocketAddress());
//                toClient = new ObjectOutputStream(
//                        new BufferedOutputStream(socket.getOutputStream()));
//                fromClient = new ObjectInputStream(
//                        new BufferedInputStream(socket.getInputStream()));
//                Card msgRequest = (Card) fromClient.readObject();
//                int number = msgRequest.cvv;
//                toClient.writeObject(new Card("Vasile","1231","16/8",122,100));
//                toClient.flush();
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            //}
//        }
//        catch(IOException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        catch(ClassNotFoundException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }

        Socket socket2 = null;
        try {
            System.out.println("i am salerclient");
            int number = 16;
            InetAddress serverHost = InetAddress.getByName("localhost");
            System.out.println("Connecting to server on port " + salerPort);
            socket2 = new Socket(serverHost, serverPort);
            System.out.println("Just connected to " + socket2.getRemoteSocketAddress());

            ObjectOutputStream toServer2 = new ObjectOutputStream(
                    new BufferedOutputStream(socket2.getOutputStream()));
//            ObjectInputStream fromServer2 = new ObjectInputStream(
//                    new BufferedInputStream(socket2.getInputStream()));

            Card msgToSend1 = new Card("Vasile","1231","16/8",122,100);
//            toServer2.writeObject(msgToSend1);
//            toServer2.flush();
            //Commit send to Bank

            Token token=new Token("100",12);
            Commit(msgToSend1,token);
            int c=commit.size()-1;
            toServer2.writeObject(commit.get(c));
            toServer2.flush();
            // This will block until t he corresponding ObjectOutputStream
            // in the server has written an object and flushed the header

//            Card msgFromReply1 = (Card) fromServer2.readObject();
//            System.out.println(number + " * " + number + " = " + msgFromReply1.cvv +" i am conected with bankserver.i am saler");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (socket2 != null) {
                try {
                    socket2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

