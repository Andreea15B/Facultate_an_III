package PayWord;

import java.io.*;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Client1 {
    static BigInteger publicKeyE;
    static BigInteger publicKeyN;
    static BigInteger publicKeyD;
    static String serverPublicKeyE;
    static String serverPublicKeyN;
    static String serverPublicKeyD;
    static RSA myRsa;
    static Card myCard;
    static String myIdentity;
    static String paywordCertif;
    static int indexCoin1,indexCoin2,indexCoin3;
    static ArrayList<ArrayList<String>> coins= new ArrayList<>();
    public Client1(String nume,String cardNo,String expDate,int cvv,int cLimit)
    {
        this.myRsa=new RSA(1024);
        publicKeyE=myRsa.getE();
        publicKeyN=myRsa.getN();
        publicKeyD=myRsa.getD();
        this.myCard=new Card(nume,cardNo,expDate,cvv,cLimit);
        indexCoin1=indexCoin2=indexCoin3=0;
    }
    static void generateCoins(String firstHash) throws NoSuchAlgorithmException {
        String rad1,rad2,rad3;
        SHA sha=new SHA(firstHash);
        rad1=sha.encode();
        sha=new SHA(rad1);
        rad2=sha.encode();
        sha=new SHA(rad2);
        rad3=sha.encode();
        int i;
        int j;
        for(j=0;j<3;j++) {
            ArrayList<String> coin = new ArrayList<>();
            if(j==0)coin.add(rad1);
            else if(j==1)coin.add(rad2);
            else if(j==2)coin.add(rad3);

            for (i = 1; i < myCard.creditLimit+1; i++) {
                sha = new SHA(coin.get(i - 1));
                coin.add(sha.encode());
            }
            coins.add(coin);
        }

    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
        Client1 usr=new Client1("Vasile","1231534209321295","16/8",122,100);
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        //step==0
        socket = new Socket(host.getHostName(), 9876);
        //trimit publicKeyE la server
        oos = new ObjectOutputStream(socket.getOutputStream());
        String keyToSendE=publicKeyE.toString();
        oos.writeObject(keyToSendE);

        //primesc publicKeyE de la server
        ois = new ObjectInputStream(socket.getInputStream());
        String message = (String) ois.readObject();
        serverPublicKeyE=message;

        //close resources
        ois.close();
        oos.close();
        Thread.sleep(100);
        socket.close();

        //step==1
        socket = new Socket(host.getHostName(), 9876);

        //trimit publicKeyD la server
        oos = new ObjectOutputStream(socket.getOutputStream());
        String keyToSendD=publicKeyD.toString();
        oos.writeObject(keyToSendD);

        //primesc publicKeyD de la server
        ois = new ObjectInputStream(socket.getInputStream());
        message = (String) ois.readObject();
        serverPublicKeyD=message;

        //close resources
        ois.close();
        oos.close();
        Thread.sleep(100);
        socket.close();
        //step==2

        socket = new Socket(host.getHostName(), 9876);
        //trimit publicKeyN la server
        oos = new ObjectOutputStream(socket.getOutputStream());
        String keyToSendN=publicKeyN.toString();
        oos.writeObject(keyToSendN);

        //primesc publicKeyN de la server
        ois = new ObjectInputStream(socket.getInputStream());
        message = (String) ois.readObject();
        serverPublicKeyN=message;

        //close resources
        ois.close();
        oos.close();
        Thread.sleep(100);
        socket.close();
        //step==3
        socket = new Socket(host.getHostName(), 9876);


        //trimit identitatea mea criptata cu rsa la server
        // criptez indentitatea cu SHA2 pentru a fi verificat certificatul payword primit de la banca

        myRsa=new RSA(new BigInteger(serverPublicKeyN),new BigInteger(serverPublicKeyE),new BigInteger(serverPublicKeyD));
        myIdentity=myRsa.encrypt(myCard.toString());
        SHA sha=new SHA(myCard.toString());
        paywordCertif=sha.encode();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(myIdentity);

        //primesc de la server certificatul payword
        ois = new ObjectInputStream(socket.getInputStream());
        message = (String) ois.readObject();

        myRsa=new RSA(publicKeyN,publicKeyE,publicKeyD);
        String recivedCP;
        recivedCP=myRsa.decrypt(message);

        if(!recivedCP.equals(paywordCertif))
            System.err.println("paywordul generat in user nu e acelasi cu cel primit de la server");
        else System.out.println("MyPayword: " + paywordCertif);

        //close resources
        ois.close();
        oos.close();
        Thread.sleep(100);

        generateCoins(paywordCertif);

        //step==0 in saler
        socket = new Socket(host.getHostName(), 4021);
        ois = new ObjectInputStream(socket.getInputStream());
        message = (String) ois.readObject();
        System.out.println("Meniul: "+message);
        ois.close();
        Thread.sleep(100);

        //step>1 in saler= face cumparaturi
        int k=1;
        int buyLimit=5;
        while(k<=buyLimit+1) {
            try {
                socket = new Socket(host.getHostName(), 4021);
                oos = new ObjectOutputStream(socket.getOutputStream());
                LastPayment lp = new LastPayment(coins.get(0).get(indexCoin1), coins.get(1).get(indexCoin2), coins.get(2).get(indexCoin3));
                Token myCurentTK = new Token(k, lp, 117,indexCoin1,indexCoin2,indexCoin3);
                if(k==buyLimit+1) {
                    Commit current = new Commit(paywordCertif, myCurentTK, myCard.creditLimit, false);
                    oos.writeObject(current);
                    System.out.println("Am terminat cumparaturile!");
                    Thread.sleep(100);
                    oos.close();
                }
                else if(k<=buyLimit) {
                    try {
                        Commit current = new Commit(paywordCertif, myCurentTK, myCard.creditLimit, true);
                        oos.writeObject(current);
                        ois = new ObjectInputStream(socket.getInputStream());
                        int[] howManyCharged = new int[3];
                        String howMany;
                        howMany = (String) ois.readObject();
                        if (Pattern.compile("([0-9])").matcher(howMany).find()) {
                            List<String> list = Arrays.asList(howMany.substring(1, howMany.length() - 1).split(", "));
                            howManyCharged[0] = Integer.parseInt(String.valueOf(Integer.parseInt(list.get(0))));
                            howManyCharged[1] = Integer.parseInt(String.valueOf(Integer.parseInt(list.get(1))));
                            howManyCharged[2] = Integer.parseInt(String.valueOf(Integer.parseInt(list.get(2))));
                            indexCoin1 = howManyCharged[0] + indexCoin1;
                            indexCoin2 = howManyCharged[1] + indexCoin2;
                            indexCoin3 = howManyCharged[2] + indexCoin3;
                        } else {
                            System.err.println(howMany);
                            System.exit(1);
                        }
                        System.out.println("La plata "+ k+" am consumat: \n"+
                        howManyCharged[0]+" bani de valoarea 1"+"\n"+
                                howManyCharged[1]+" bani de valoarea 3"+"\n"+
                                howManyCharged[2]+" bani de valoarea 5\n");
                        ois.close();
                        oos.close();
                    }
                    catch(ConnectException conexionFinished) {
                        System.err.println("Conexiunea cu vanzatorul s-a terminat pentru ca incerci sa faci o plata, dar nu mai ai bani pe card.");
                    }

                }
                k++;
            }
            catch(ConnectException conexionFinished)
            {
                System.out.println("Conexiunea cu vanzatorul s-a terminat");
            }
        }

    }

}
