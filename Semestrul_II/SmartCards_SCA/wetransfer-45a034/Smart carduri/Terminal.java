import com.sun.javacard.apduio.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;


public class Terminal {

    private byte pinCorect[]   = {0x05, 0x00, 0x02, 0x05};
    private byte pinIncorect[] = {0x01, 0x02, 0x03, 0x04};
    private CadClientInterface cad;

    Terminal() throws Exception{
        Socket sock = new Socket("localhost", 9025);
        InputStream is = sock.getInputStream();
        OutputStream os = sock.getOutputStream();
        cad=CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);
        cad.powerUp();
    }

    private boolean selectApplet(boolean debug) throws Exception {
        Apdu apdu = new Apdu();

        apdu.command[0] = (byte) 0x00; //CLA
        apdu.command[1] = (byte) 0xA4; //INS
        apdu.command[2] = (byte) 0x04; //P1
        apdu.command[3] = (byte) 0x00; //P2

        byte[] dataIn = {(byte)0xA0, 0x00, 0x00, 0x00, 0x62, 0x3, 0x1, (byte)0xc, 0x6, 0x1, 0x7F};
        apdu.setDataIn(dataIn, 0x0A);
        apdu.Le = 0x7F;

        cad.exchangeApdu(apdu);
        if(debug)
            System.out.println(apdu);

        if(apdu.getSw1Sw2()[0] == (byte) 0x90 && apdu.getSw1Sw2()[1] == (byte)0x00)
            return true;
        else
            return false;
    }

    private short getBalance(boolean debug) throws Exception {
        Apdu apdu = new Apdu();

        apdu.command[0] = (byte) 0x80; //CLA
        apdu.command[1] = (byte) 0x50; //Balans INS
        apdu.command[2] = (byte) 0x00;
        apdu.command[3] = (byte) 0x00;

        apdu.Le = 0x04;
        cad.exchangeApdu(apdu);

        short a =((short)(apdu.getDataOut()[0] & 0xFF));
        short b =((short)(apdu.getDataOut()[1] & 0xFF));
        short balance = (short)((a<<8)|b);

        if(debug)
            System.out.println(apdu);

        return balance;
    }

    private boolean debit(short value, boolean debug) throws Exception {

        byte a = (byte) (value >> 8);
        byte b = (byte) (value & 0xFF);

        Apdu apdu = new Apdu();

        apdu.command[0] = (byte) 0x80; //CLA
        apdu.command[1] = (byte) 0x40; //Debit INS
        apdu.command[2] = a;
        apdu.command[3] = b;

        apdu.Le = 0x7F;
        cad.exchangeApdu(apdu);

        if(debug)
            System.out.println(apdu);

        if(apdu.getSw1Sw2()[0] == (byte) 0x90 && apdu.getSw1Sw2()[1] == (byte)0x00)
            return true;
        else
            return false;

    }

    private boolean verify(byte[] pin, boolean debug) throws Exception {
        Apdu apdu = new Apdu();

        apdu.command[0] = (byte) 0x80;
        apdu.command[1] = (byte) 0x20;
        apdu.command[2] = (byte) 0x00;
        apdu.command[3] = (byte) 0x00;
        apdu.setDataIn(pin);

        apdu.Le = 0x7F;
        cad.exchangeApdu(apdu);

        if(debug)
            System.out.println(apdu);

        if(apdu.getSw1Sw2()[0] == (byte) 0x90 && apdu.getSw1Sw2()[1] == (byte)0x00)
            return true;
        else
            return false;
    }

    private boolean verifyCrypto(byte[] pin, boolean debug) throws Exception {

        Apdu apdu = new Apdu();
        apdu.command[0] = (byte) 0x80;
        apdu.command[1] = (byte) 0x21;
        apdu.command[2] = (byte) 0x00;
        apdu.command[3] = (byte) 0x00;
        apdu.setDataIn(encript(pin));

        apdu.Le = 0x7F;
        cad.exchangeApdu(apdu);

        if(debug)
            System.out.println(apdu);

        if(apdu.getSw1Sw2()[0] == (byte) 0x90 && apdu.getSw1Sw2()[1] == (byte)0x00)
            return true;
        else
            return false;
    }

    private byte[] encript(byte[] pin) throws Exception {
        return new byte[]{0x00, 0x00, 0x00, 0x00};
    }

    private void genKeys() throws Exception {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();

        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = (RSAPublicKeySpec) fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = (RSAPrivateKeySpec) fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);

        byte[] modulus  = pub.getModulus().toByteArray();
        byte[] publicExponent = pub.getPublicExponent().toByteArray();
        byte[] privateExponent = priv.getPrivateExponent().toByteArray();

        System.out.println("Modulus");
        for(short index=0; index<modulus.length; index++){
            System.out.print((byte)modulus[index]+" ");
        }
        System.out.println("\npublic");
        for(short index=0; index<publicExponent.length; index++){
            System.out.print(publicExponent[index]+" ");
        }
        System.out.println("\nprivate");
        for(short index=0; index<publicExponent.length; index++){
            System.out.print(privateExponent[index]+" ");
        }
    }

    private boolean credit(short value, boolean debug) throws Exception {
        Apdu apdu = new Apdu();

        byte a = (byte) (value >> 8);
        byte b = (byte) (value & 0xFF);

        apdu.command[0] = (byte) 0x80;
        apdu.command[1] = (byte) 0x30;
        apdu.command[2] = a;
        apdu.command[3] = b;

        apdu.Le = 0x7F;
        cad.exchangeApdu(apdu);

        if(debug)
            System.out.println(apdu);

        if(apdu.getSw1Sw2()[0] == (byte) 0x90 && apdu.getSw1Sw2()[1] == (byte)0x00)
            return true;
        else
            return false;
    }

    private byte[] getCVM(boolean debug) throws Exception{
        Apdu apdu = new Apdu();

        apdu.command[0] = (byte) 0x80;
        apdu.command[1] = (byte) 0x22;
        apdu.command[2] = (byte) 0x00;
        apdu.command[3] = (byte) 0x00;

        apdu.Le = 0x0A;
        cad.exchangeApdu(apdu);

        if(debug)
            System.out.println(apdu);

        return apdu.getDataOut();
    }

    private boolean verifyCondition(short value, short X, short Y, byte condition){
        switch(condition){
            case 0x06:  return (value<=X);
            case 0x08:  return (value<=Y);
            case 0x09:  return (value >Y);
            default: return false;
        }
    }

    private boolean verifyPin(boolean criptat, boolean corect) throws Exception {
        if(criptat){
            if(corect)
                return verifyCrypto(pinCorect, false);
            else
                return verifyCrypto(pinIncorect, false);
        }
        else{
            if(corect)
                return verify(pinCorect, false);
            else
                return verify(pinIncorect, false);
        }
    }

    private boolean verifyMethod(byte code, boolean corect) throws Exception {
        switch(code){
            case 0x31: return true;                     // -0011111 No Cvm
            case 0x01: return verifyPin(false, corect); // -0000001 Pin Plain
            case 0x04: return verifyPin(true,  corect);  // -0000100 Pin Encriptat
        }
        return true;
    }

    public void test() throws Exception{

        byte[] pin = {0x05, 0x00, 0x02, 0x05};

        boolean rezultat = this.selectApplet(true);
        if(!rezultat)   throw new Exception("Eroare selectare applet");

        short balans = this.getBalance(true);
        if(balans !=0 ) throw new Exception("Balans != 0");

        rezultat = this.verify(pin, true);
        if(!rezultat)   throw new Exception("Eroare verificare pin");

        rezultat = this.credit((short)10000, true);
        if(!rezultat)   throw new Exception("Eroare creditare 10000");

        balans = this.getBalance(true);
        if(balans != 10000) throw new Exception("Balans != 10000");

        rezultat = this.debit((short)10000, true);
        if(!rezultat) throw new Exception("Eroare debitare 10000");

        balans = this.getBalance(true);
        if(balans !=0 ) throw new Exception("Balans != 0");

        rezultat = this.credit((short)10000, true);
        if(!rezultat) throw new Exception("Eroare creditare 10000");

        balans = this.getBalance(true);
        if(balans != 10000) throw new Exception("Balans != 10000");

        rezultat = this.debit((short)50, true);
        if(!rezultat) throw new Exception("Eroare creditare de 50 fara pin");

        balans = this.getBalance(true);
        if(balans != 9950) throw new Exception("Balans != 9950");

        rezultat = this.verify(pin, true);
        if(!rezultat)   throw new Exception("Eroare verificare pin");

        rezultat = this.debit((short)9950, true);
        if(!rezultat) throw new Exception("Eroare creditare de 9950 cu pin");

        balans = this.getBalance(true);
        if(balans != 0) throw new Exception("Balans != 0");

        System.out.println("\n\n");
    }

    public boolean adauga( int valoare ) throws Exception {
        short value = (short) valoare;
        return this.credit(value, false);
    }

    public short balans() throws Exception {
        return this.getBalance(false);
    }

    public boolean retrage(int valoare, boolean corect) throws Exception {

        short value = (short) valoare;

        byte[] cvm = this.getCVM(false);
        short a =((short)(cvm[0] & 0xFF));
        short b =((short)(cvm[1] & 0xFF));
        short c =((short)(cvm[2] & 0xFF));
        short d =((short)(cvm[3] & 0xFF));

        short X = (short)((a<<8)|b);
        short Y = (short)((c<<8)|d);

        byte cvmCodes[] = new byte[10];
        byte cvmConditions[] = new byte[10];

        for(short index=0; index<(cvm.length-4)/2; index++){
            cvmCodes[index] = cvm[4+index*2];
            cvmConditions[index] = cvm[4+index*2+1];
        }

        for(short index=0; index<cvmCodes.length; index++){
            if(verifyCondition(value, X, Y, cvmConditions[index])){
                if(verifyMethod(cvmCodes[index], corect)){
                    return this.debit(value, false);
                }
            }
        }
        return false;
    }

    public void afisareCVM() throws Exception {
        byte[] cvm= this.getCVM(false);
        System.out.print("CVM: ");
        for(short index=0; index<cvm.length; index++){
            System.out.print(Integer.toHexString(cvm[index])+" ");
        }
        System.out.println("\n");
    }
}
