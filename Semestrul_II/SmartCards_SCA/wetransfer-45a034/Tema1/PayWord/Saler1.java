package PayWord;

import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Saler1 {
    private static ServerSocket server;
    private static int port = 4021;
    static int[] menu={100,600,570,400,400,300};
    private static ArrayList<CommitBank> commits=new ArrayList<>();

    public Saler1()
    {

    }
    public static int[] coinCharge(int[] coins, int n,int ind1,int ind2,int ind3,int cardLimit) {

        int result = 0;
        int [] index={0,0,0};
        int [] valueForEveryCoin={ind1,ind2,ind3};
        while (n != 0) {
            for (int i=coins.length - 1 ; i>=0 ; i--) {
                if(valueForEveryCoin[i]+result<cardLimit) {
                    if (coins[i] <= n) {
                        n = n - coins[i];
                        i++; //neutralizing i-- with i++.
                        result++;
                    } else {
                        index[i] = result;
                        result = 0;
                    }
                }
                else{
                    index[i]=index[i]+(cardLimit-valueForEveryCoin[i]);
                    int rest=result-(cardLimit-valueForEveryCoin[i]);
                    int r=0;
                    if(i-1>=0)
                    {
                        r=(int)((rest*coins[i])/coins[i-1]);
                    }
                    if(i-1>=0) {
                        valueForEveryCoin[i - 1] = r + valueForEveryCoin[i - 1];

                        if (i - 2 >= 0) {
                            if(valueForEveryCoin[i - 2] + rest - r * coins[i - 1]<=cardLimit)
                                valueForEveryCoin[i - 2] = valueForEveryCoin[i - 2] + rest - r * coins[i - 1];
                            else
                            {
                                index[0]=index[1]=index[2]=-1;
                                return index;
                            }
                        }
                    }
                    else if (i - 2 >= 0)
                    {
                        if(r + valueForEveryCoin[i - 2]<=cardLimit)
                            valueForEveryCoin[i - 2]=r + valueForEveryCoin[i - 2];
                        else
                        {
                            index[0]=index[1]=index[2]=-1;
                            return index;
                        }
                    }
                    else
                    {
                        index[0]=index[1]=index[2]=-1;
                        return index;
                    }
                }
            }
        }
        return index;
    }

    public static LastPayment makeCharge(LastPayment lp, int[] charger) throws NoSuchAlgorithmException {
        int i,j;
        LastPayment lastPayment;
        String hash1="",hash2="",hash3="";
        String intermediar = null;
        SHA sha;
        int ok=0;
        for(i=0;i<=2;i++)
        {
            ok=0;
            while(charger[i]>0)
            {
                if(i==0) {
                    if(ok==0)
                    {
                        intermediar=lp.hash1;
                        ok=1;
                    }
                    sha = new SHA(intermediar);
                    intermediar=sha.encode();
                    charger[i]--;
                }
                else if(i==1)
                {
                        if(ok==0)
                        {
                            intermediar=lp.hash2;
                            ok=1;
                        }
                        sha = new SHA(intermediar);
                        intermediar=sha.encode();
                        charger[i]--;

                }
                else
                {
                    if(ok==0)
                    {
                        intermediar=lp.hash3;
                        ok=1;
                    }
                    sha = new SHA(intermediar);
                    intermediar=sha.encode();
                    charger[i]--;

                }
                if(i==0)hash1=intermediar;
                else if(i==1)hash2=intermediar;
                else hash3=intermediar;
            }
            if(charger[i]==0)
            {
                if(i==0)hash1=lp.hash1;
                else if(i==1)hash2=lp.hash2;
                else hash3=lp.hash3;
            }
        }
        lastPayment=new LastPayment(hash1,hash2,hash3);
        return lastPayment;

    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InterruptedException {
        int salerPort = 4021;
        int serverPort = 4020;
        server = new ServerSocket(port);
        int firstPay=0;
        int steps=0;
        boolean ok=true;
        while (ok) {
            if(steps==0) {
                //creating socket and waiting for client connection
                try {
                    Socket socket = server.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(Arrays.toString(menu));
                    oos.close();
                    socket.close();
                }
                catch(ConnectException conexionFinished)
                {
                    System.err.println("Vanzatorul s-a inchis.");
                }
            }
            else {
                try {
                    Socket socket = server.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Commit comm = (Commit) ois.readObject();//primesc clientIdentity
                    System.out.println("Am primit commitul");
                    if (!comm.iWantToBuy) {
                        ok = false;


                    }
                    if (ok && menu[comm.token.whatToBuy] < comm.token.valueToBuy)//verificam ca userul sa nu cumpere mai mult decat detine vanzatorul
                    {
                        System.out.println(menu[comm.token.whatToBuy] + " " + comm.token.valueToBuy + " sunt in cazul menu[comm.token.whatToBuy]<comm.token.valueToBuy");
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject("Message from saler: Vrei sa cumperi mai mult decat am din produsul ales");
                        oos.close();
                        socket.close();
                    } else if (ok) {
                        if (firstPay == 0) {
                            SHA sha = new SHA(comm.payWordCertif);
                            if (!comm.token.lastpay.hash1.equals(sha.encode()))
                                System.err.println("First hash of coin 1 is not correct");
                            sha = new SHA(comm.token.lastpay.hash1);
                            if (!comm.token.lastpay.hash2.equals(sha.encode()))
                                System.err.println("First hash of coin 2 is not correct");
                            sha = new SHA(comm.token.lastpay.hash2);
                            if (!comm.token.lastpay.hash3.equals(sha.encode()))
                                System.err.println("First hash of coin 3 is not correct");
                        }

                        //cat trebuie sa ia din fiecare moneda
                        int[] detCharge = coinCharge(new int[]{1, 3, 5}, comm.token.valueToBuy, comm.token.ind1, comm.token.ind2, comm.token.ind3, comm.cardLimit);

                        if (detCharge[0] == -1) {
                            try {
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject("Message from saler: Nu ai destui bani pentru a cumpara un produs.");
                                oos.close();
                                socket.close();
                            }catch(ConnectException conexionFinished)
                            {
                                System.err.println("Vanzatorul s-a inchis.");
                            }
                        }
                        else {
                            String detCargeString = Arrays.toString(detCharge);

                            //facem charge din contul clientului
                            LastPayment whereToArrive = makeCharge(comm.token.lastpay, detCharge);
                            System.out.println("Am facut charge din cont.");
                            CommitBank cb = new CommitBank(comm, detCharge, whereToArrive);
                            commits.add(cb);//creez un nou commit de trimis la banca si il adaug in vectorul de commituri


                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(detCargeString);//trimit cat charge am facut din fiecare moneda la client
                            oos.close();
                            ois.close();
                            socket.close();
                            firstPay = 1;
                        }

                    }
                } catch (ConnectException conexionFinished) {
                    System.err.println("Vanzatorul s-a inchis.");
                }
            }
            steps++;
        }
        server.close();

        //ne conectam la server si cerem sa valideze commiturile
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        commits.add(commits.get(0));
        socket = new Socket(host.getHostName(), 9876);
        //trimitem vectorul de commituri
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(commits);

        //primesc platile acceptate sau refuzate de la server
        ois = new ObjectInputStream(socket.getInputStream());
        ArrayList<String> acc = (ArrayList<String>) ois.readObject();
        for (int i=0;i<acc.size();i++)
        {
            System.out.println(acc.get(i));
        }

        //close resources
        ois.close();
        oos.close();
        Thread.sleep(100);
        socket.close();
    }
}

