package PayWord;

import PayWord.RSA;
import PayWord.SHA;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Server1 {

    private static ServerSocket server;
    private static int port = 9876;
    static BigInteger publicKeyE;
    static BigInteger publicKeyN;
    static BigInteger publicKeyD;
    static RSA myRsa;
    static String userPublicKeyE;
    static String userPublicKeyN;
    static String userPublicKeyD;
    static ArrayList<ArrayList<String>> payments= new ArrayList<>();

    public static LastPayment checkPayments(LastPayment lp, int[] charger) throws NoSuchAlgorithmException {
        LastPayment lastPayment;

        String hash1 = null, hash2 = null, hash3 = null;
        String intermediar = null;
        SHA sha;
        int ok;
        for(int i=0; i<=2; i++) {
            ok = 0;
            while(charger[i] > 0) {
                if(i==0) {
                    if(ok==0) {
                        intermediar = lp.hash1;
                        if(!payments.get(i).toString().contains(intermediar)) payments.get(i).add(intermediar);
                        else{
                            lastPayment=new LastPayment(hash1,hash2,hash3);
                            return lastPayment;
                        }
                        ok=1;
                    }
                    sha = new SHA(intermediar);
                    intermediar=sha.encode();
                    if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                    else{
                        lastPayment=new LastPayment(hash1,hash2,hash3);
                        return lastPayment;
                    }
                    charger[i]--;
                }
                else if(i==1)
                {
                    if(ok==0)
                    {
                        intermediar=lp.hash2;
                        if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                        else{
                            lastPayment=new LastPayment(hash1,hash2,hash3);
                            return lastPayment;
                        }
                        ok=1;
                    }
                    sha = new SHA(intermediar);
                    intermediar=sha.encode();
                    if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                    else{
                        lastPayment=new LastPayment(hash1,hash2,hash3);
                        return lastPayment;
                    }
                    charger[i]--;

                }
                else
                {
                    if(ok==0)
                    {
                        intermediar=lp.hash3;
                        if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                        else{
                            lastPayment=new LastPayment(hash1,hash2,hash3);
                            return lastPayment;
                        }
                        ok=1;
                    }
                    sha = new SHA(intermediar);
                    intermediar=sha.encode();
                    if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                    else{
                        lastPayment=new LastPayment(hash1,hash2,hash3);
                        return lastPayment;
                    }
                    charger[i]--;

                }
                if(i==0)
                {
                    hash1=intermediar;
                    if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                    else{
                        lastPayment=new LastPayment(hash1,hash2,hash3);
                        return lastPayment;
                    }
                }
                else if(i==1){
                    hash2=intermediar;
                    if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                    else{
                        lastPayment=new LastPayment(hash1,hash2,hash3);
                        return lastPayment;
                    }
                }
                else {
                    hash3=intermediar;
                    if(!payments.get(i).toString().contains(intermediar))payments.get(i).add(intermediar);
                    else{
                        lastPayment=new LastPayment(hash1,hash2,hash3);
                        return lastPayment;
                    }
                }
            }
            if(charger[i]==0)
            {
                if(i==0){
                    hash1=lp.hash1;
                    payments.get(i).add(hash1);
                }
                else if(i==1){
                    hash2=lp.hash2;
                    payments.get(i).add(hash2);
                }
                else {
                    hash3=lp.hash3;
                    payments.get(i).add(hash3);
                }
            }
        }
        lastPayment=new LastPayment(hash1,hash2,hash3);
        return lastPayment;

    }
    public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //create the socket server object
        myRsa=new RSA(1024);
        publicKeyE=myRsa.getE();
        publicKeyN=myRsa.getN();
        publicKeyD=myRsa.getD();
        server = new ServerSocket(port);
        int step=0;
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            //creating socket and waiting for client connection

            Socket socket = server.accept();
            if(step==0) {//trimit publicKeyE
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //convert ObjectInputStream object to String

                String message = (String) ois.readObject();
                //create ObjectOutputStream object
                userPublicKeyE=message;
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(publicKeyE.toString());
                ois.close();
                oos.close();
                socket.close();
            }
            else if(step==1) {//trimit publicKeyD
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //convert ObjectInputStream object to String

                String message = (String) ois.readObject();
                //create ObjectOutputStream object
                userPublicKeyD=message;
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(publicKeyD.toString());
                ois.close();
                oos.close();
                socket.close();
            }
            else if(step==2) {//trimit publicKeyN
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //convert ObjectInputStream object to String

                String message = (String) ois.readObject();
                userPublicKeyN=message;
                //create ObjectOutputStream object

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(publicKeyN.toString());
                System.out.println("Am facut schimb de cheie publica cu Userul.");
                ois.close();
                oos.close();
                socket.close();
            }
            else if(step==3)//trimit payWordul
            {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                String message = (String) ois.readObject();//primesc clientIdentity
                String userIdentity=myRsa.decrypt(message);

                SHA sha=new SHA(userIdentity);
                String payWord=sha.encode();
                myRsa=new RSA(new BigInteger(userPublicKeyN),new BigInteger(userPublicKeyE),new BigInteger(userPublicKeyD));
                String clientIdentity=myRsa.encrypt(payWord);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(clientIdentity);
                System.out.println("Am autentificat userul si i-am trimis payWordul.");
                ois.close();
                oos.close();
                socket.close();

            }
            else if(step==4)
            {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ArrayList<String> acceptance=new ArrayList<>();
                ArrayList<CommitBank> commitsToCheck = (ArrayList<CommitBank>) ois.readObject();//primesc clientIdentity
                int i;

                //initializam vectorul de plati pentru fiecare moneda
                ArrayList<String>rad=new ArrayList<>();
                SHA sha=new SHA(commitsToCheck.get(0).commit.payWordCertif);
                String rad1=sha.encode();
                rad.add(rad1);
                payments.add(rad);
                sha=new SHA(rad1);
                String rad2=sha.encode();
                rad.add(rad2);
                payments.add(rad);
                sha=new SHA(rad2);
                String rad3=sha.encode();
                rad.add(rad3);
                payments.add(rad);

                for(i=0;i<commitsToCheck.size();i++)
                {
                    LastPayment lp=checkPayments(commitsToCheck.get(i).commit.token.lastpay,commitsToCheck.get(i).charge);
                    if(!lp.toString().equals(commitsToCheck.get(i).whereToArrive.toString()))
                    {
                        acceptance.add("Plata "+ (i+1)+" nu a fost acceptata.");
                    }
                    acceptance.add("Plata "+ (i+1)+" a fost acceptata.");
                }

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(acceptance);
                System.out.println("Am verificat toate platile.");
                ois.close();
                oos.close();
                socket.close();
            }
            step++;
            if(step==5)break;
        }
        //close the ServerSocket object
        server.close();
    }

}
