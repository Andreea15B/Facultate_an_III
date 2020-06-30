package src;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadClientInterface;
import com.sun.javacard.apduio.CadDevice;
import com.sun.javacard.apduio.CadTransportException;

public class Terminal {

    public static String installPath = "D:\\Facultate_FII_UAIC\\Facultate_an_III\\Semestrul_II\\SmartCards_SCA\\eclipse-workspace\\CardHolderVerification\\apdu_scripts\\cap-ro.uaic.info.sca.cardholderapp.script";
    public static String initPath = "D:\\Facultate_FII_UAIC\\Facultate_an_III\\Semestrul_II\\SmartCards_SCA\\eclipse-workspace\\CardHolderVerification\\apdu_scripts\\init.script";
    private static int lowerLimit;
    private static int upperLimit;

    public static List<Short> CVMs = new ArrayList<>();
    public static byte[] CVM_CODES_LIST = {0x1F, 0x01};


    public static void runScripts(CadClientInterface cad, String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream
                .filter(s -> {
                    if (s.isEmpty())
                        return false;
                    if (Character.isLetter(s.charAt(0)))
                        return false;
                    return !s.startsWith("/");
                })
                .map(line -> Arrays.stream(line.replace(";", "").split(" ")).map(x -> {
                    char first = x.charAt(2);
                    char second = x.charAt(3);

                    return (byte) (((Integer.parseInt(String.valueOf(first), 16)) * 16) + Integer.parseInt(String.valueOf(second), 16));
                })
                        .collect(Collectors.toList()))
                .forEach(bytes -> {
                    Apdu apdu = new Apdu();
                    apdu.command = new byte[]{bytes.get(0), bytes.get(1), bytes.get(2), bytes.get(3)};
                    byte[] data = new byte[bytes.size() - 6];
                    for (int i = 5; i < bytes.size() - 1; i++) {
                        data[i - 5] = bytes.get(i);
                    }
                    apdu.setDataIn(data, bytes.get(4));
                    apdu.setLe(bytes.get(bytes.size() - 1));
                    System.out.println("command runScripts: " + apdu);

                    try {
                        cad.exchangeApdu(apdu);
                    } catch (IOException | CadTransportException e) {
                        e.printStackTrace();
                    }

                    System.out.println("response runScripts: " + apdu);
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] params) {
        try {
            CadClientInterface cad;
            Socket sock;

            sock = new Socket("localhost", 9025);
            InputStream is = sock.getInputStream();
            OutputStream os = sock.getOutputStream();

            cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);
            System.out.println("Before power up");
            byte[] atr = cad.powerUp();
            for (int i = 0; i < atr.length; i++) System.out.println(atr[i]);
            System.out.println("After power up");

            runScripts(cad, installPath);
            runScripts(cad, initPath);
            getCVMList(cad);

            label_1:
            while (true) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    
                    System.out.println("Insert method (balance/credit/debit/hardcoded/close): ");
                    String method = br.readLine().toLowerCase();

                    switch (method) {
                        case "balance":
                            getBalance(cad);
                            break;

                        case "credit":
                            credit(cad, getAmount(br));
                            break;

                        case "debit":
                            debit(br, cad, getAmount(br));
                            break;

                        case "hardcoded":
                            credit(cad, (short) 100);
                            getBalance(cad);
                            debit(br, cad, (short) 30);
                            getBalance(cad);
                            debit(br, cad, (short) 60);
                            getBalance(cad);
                            break;

                        case "close":
                            cad.powerDown();
                            sock.close();
                            break label_1;

                        default:
                            System.out.println("Invalid command: " + method);
                            continue;
                    }


                } catch (IOException | IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void credit(CadClientInterface cad, short amount) throws IOException {

        byte firstByte = (byte) (amount & 0xff);
        byte secondByte = (byte) ((amount >>> 8) & 0xff);

        Apdu apdu = new Apdu();
        apdu.command = new byte[]{(byte) 0x80, 0x30, 0x00, 0x00};
        apdu.setDataIn(new byte[]{secondByte, firstByte}, 0x02);
        apdu.setLe(0x7f);

        System.out.println("command credit: " + apdu);
        try {
            cad.exchangeApdu(apdu);
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }

        System.out.println("response credit: " + apdu);
    }
    
    private static void debit(BufferedReader br, CadClientInterface cad, short amount) throws IOException, GeneralSecurityException {

        byte firstByte = (byte) (amount & 0xff);
        byte secondByte = (byte) ((amount >>> 8) & 0xff);

        byte currentCase;
        if (amount < lowerLimit) currentCase = 0x06; // no CVM required
        else currentCase = 0x08; // CVM required

        byte CVMCode = 0x00;
        for (short CVR : CVMs) {
            if ((CVR & 0xFF) == currentCase) { // the current element in list
                CVMCode = (byte) (CVR >> 8);
            }
        }

        String PIN;
        switch (CVMCode) {
            case 0x1F: // no CVM required
                verifyNone(cad);
                break;
            case 0x01: // CVM required
            	System.out.println("Transaction requires pin: ");
                PIN = br.readLine(); 
                verifyPIN(PIN, cad);
                break;
            default:
                System.out.println("Unrecognized CVM code: " + CVMCode);
        }

        Apdu apdu = new Apdu();
        apdu.command = new byte[]{(byte) 0x80, 0x40, 0x00, 0x00};
        apdu.setDataIn(new byte[]{secondByte, firstByte}, 2);
        apdu.setLe(0x7f);

        System.out.println("command debit: " + apdu);
        try {
            cad.exchangeApdu(apdu);
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }

        System.out.println("response debit: " + apdu);
    }

    private static short getAmount(BufferedReader br) throws IOException {
        System.out.println("Insert amount: ");
        short amount = 0;
        try {
            amount = Short.parseShort(br.readLine());
        } catch (NumberFormatException e) {
            throw new IOException("Amount is invalid");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(amount);
        return amount;
    }

    private static void verifyNone(CadClientInterface cad) {
        Apdu apdu = new Apdu();
        apdu.command = new byte[]{(byte) 0x80, 0x20, 0x00, 0x00};
        apdu.setDataIn(null, 0);
        apdu.setLe(0x7f);

        System.out.println("command verifyNone: " + apdu);
        try {
            cad.exchangeApdu(apdu);
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }

        System.out.println("response verifyNone: " + apdu);
    }

    private static void verifyPIN(String pin, CadClientInterface cad) {
        byte[] PINBytes = new byte[pin.length()];
        for (int i = 0; i < pin.length(); ++i) {
            PINBytes[i] = (byte) (pin.charAt(i) - '0');
        }

        Apdu apdu = new Apdu();
        apdu.command = new byte[]{(byte) 0x80, 0x21, 0x00, 0x00};
        apdu.setDataIn(PINBytes, PINBytes.length);
        apdu.setLe(0x7f);

        System.out.println("command verifyPIN: " + apdu);
        try {
            cad.exchangeApdu(apdu);
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }

        System.out.println("response verifyPIN: " + apdu);
    }
    
    private static void getBalance(CadClientInterface cad) {
        Apdu apdu = new Apdu();
        apdu.command = new byte[]{(byte) 0x80, 0x50, 0x00, 0x00};
        apdu.setDataIn(null, 0);
        apdu.setLe(0x02);

        System.out.println("command getBalance: " + apdu);
        try {
            cad.exchangeApdu(apdu);
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }

        System.out.println("response getBalance: " + apdu);

    }

    private static void getCVMList(CadClientInterface cad) throws Exception {
        Apdu apdu = new Apdu();
        apdu.command = new byte[]{(byte) 0x80, 0x70, 0x00, 0x00};
        apdu.setDataIn(null, 0);
        apdu.setLe(0x7f);

        System.out.println("command getCVMList: " + apdu);
        try {
            cad.exchangeApdu(apdu);
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }

        System.out.println("response getCVMList: " + apdu);

        byte[] response = apdu.getDataOut();

        byte[] lowerLimitBytes = Arrays.copyOfRange(response, 0, 4);
        lowerLimit = bytesToInt(lowerLimitBytes);
        
        byte[] upperLimitBytes = Arrays.copyOfRange(response, 4, 8);
        upperLimit = bytesToInt(upperLimitBytes);

        for (int i = 8; i < response.length-2; i += 2) {
            short newCVR = (short) (response[i] << 8 | response[i + 1] & 0xFF);
            CVMs.add(newCVR);
        }
        for (Short cvm : CVMs) {
            boolean ok = false;
            for (byte terminalCVMRule : CVM_CODES_LIST) {
                if ((byte)((cvm >> 8) & 0xff) == terminalCVMRule) {
                    ok = true;
                }
            }

            if (!ok) {
                throw new Exception("Rule not supported " + cvm);
            }
        }
    }

    public static int bytesToInt(byte[] x) {
        return (x[0] << 24) & 0xff000000 | (x[1] << 16) & 0x00ff0000 |
                (x[2] << 8) & 0x0000ff00 | (x[3] << 0) & 0x000000ff;
    }

}