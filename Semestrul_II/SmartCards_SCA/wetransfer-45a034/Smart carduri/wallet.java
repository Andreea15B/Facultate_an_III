package com.sun.jcclassic.samples.wallet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.OwnerPIN;
import javacardx.crypto.Cipher;

public class Wallet extends Applet {

    //Constante	
	byte[] privateModulo = {
			
	};
	
	byte[] privateExponent = {
	
	};
	
    //CLA
    final static byte Wallet_CLA = (byte) 0x80;

    //Instructiuni
    final static byte VERIFY = (byte) 0x20;
    final static byte CREDIT = (byte) 0x30;
    final static byte DEBIT = (byte) 0x40;
    final static byte GET_BALANCE = (byte) 0x50;
    final static byte VERIFY_CRYPTO = (byte) 0x21;
    final static byte GET_CARDHOLDER_METHODS = (byte) 0x22;
    
    //CVM Valori
	byte CVM_X_1 = (byte) 0x00;
	byte CVM_X_2 = (byte) 0x32;
	
	byte CVM_Y_1 = (byte) 0x00;
	byte CVM_Y_2 = (byte) 0x64;
    
    //Constante
    final static short MAX_BALANCE_MONEY = 30000;
    final static short MAX_MONEY_TRANSACTION_AMOUNT = 10000;
    
    final static byte PINLESS_LIMIT = (byte) 0x04;
    final static byte PIN_TRY_LIMIT = (byte) 0x03;
    final static byte MAX_PIN_SIZE = (byte) 0x08;
    
    final static short SW_VERIFICATION_FAILED = 0x6300;
    final static short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    final static short SW_INVALID_TRANSACTION_AMOUNT = 0x6A83;

    final static short SW_EXCEED_MAXIMUM_BALANCE = 0x84;
    final static short SW_NEGATIVE_BALANCE = 0x6A85;

    OwnerPIN pin;
    short pinlessCount;
    short balanceMoney;

    private Wallet(byte[] bArray, short bOffset, byte bLength) {

        pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);

        byte iLen = bArray[bOffset]; // aid length
        bOffset = (short) (bOffset + iLen + 1);
        byte cLen = bArray[bOffset]; // info length
        bOffset = (short) (bOffset + cLen + 1);
        byte aLen = bArray[bOffset]; // applet data length

        pin.update(bArray, (short) (bOffset + 1), aLen);
        register();

    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Wallet(bArray, bOffset, bLength);
    }

    @Override
    public boolean select() {
        if (pin.getTriesRemaining() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void deselect() {
        pin.reset();
    }

    @Override
    public void process(APDU apdu) {

        byte[] buffer = apdu.getBuffer();

        if (apdu.isISOInterindustryCLA()) {
            if (buffer[ISO7816.OFFSET_INS] == (byte) (0xA4)) {
                return;
            }
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        if (buffer[ISO7816.OFFSET_CLA] != Wallet_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case GET_BALANCE:
            	getBalance(apdu);
                return;
            case DEBIT:
                debit(apdu);
                return;
            case CREDIT:
            	credit(apdu);
                return;
            case VERIFY:
                verify(apdu);
                return;
            case VERIFY_CRYPTO:
            	verifyCrypto(apdu);
            	return;
            case GET_CARDHOLDER_METHODS:
            	cvm(apdu);
            	return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }

    }
    
    private void credit(APDU apdu) {
                
        //Calculam suma creditata formata din cei doi bytes P1 si P2
        byte[] buffer = apdu.getBuffer();
        short a = (short)(buffer[ISO7816.OFFSET_P1] & 0xFF);
        short b = (short)(buffer[ISO7816.OFFSET_P2] & 0xFF);
        short creditAmount = (short)( (a << 8) | b );
        
        //Verificam tranzactia
        if ((creditAmount > MAX_MONEY_TRANSACTION_AMOUNT) || (creditAmount < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }
        if ((short) (balanceMoney + creditAmount) > MAX_BALANCE_MONEY) {
            ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
        }
        
        //Crestem balanta banilor
        balanceMoney = (short) (balanceMoney + creditAmount);
    }
    
    private void debit(APDU apdu) {
        
        byte[] buffer = apdu.getBuffer();
        
        short a;
        short b;
        short moneyAmount;
        short pinlessMaxAmount;
        
       //Calculam suma creditata formata din cei doi bytes de date
        a = (short) (buffer[ISO7816.OFFSET_P1] & 0xFF);
        b = (short) (buffer[ISO7816.OFFSET_P2] & 0xFF);
        moneyAmount = (short)((a << 8)|b);
        
        a = (short) (CVM_X_1 & 0xFF);
        b = (short) (CVM_X_2 & 0xFF);
        pinlessMaxAmount = (short)((a<<8)|b);
        
        
        //Verificam tranzactia
        if( moneyAmount > pinlessMaxAmount ){
            if (!pin.isValidated()) {
                ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
            }
	        if( (moneyAmount < 0) || (moneyAmount > MAX_MONEY_TRANSACTION_AMOUNT) ){
	        	ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
	        }
        }
        
        if( (short)(balanceMoney - moneyAmount) < 0 ){
        	ISOException.throwIt(SW_NEGATIVE_BALANCE);
        }
        pin.reset();
        
        //Modificam balanta
        balanceMoney  = (short)(balanceMoney  - moneyAmount);
    }
    
    private void getBalance(APDU apdu) {
    	
        byte[] buffer = apdu.getBuffer();
        short le = apdu.setOutgoing();
        
    	//Verificam numarul de bytes asteptati
        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        apdu.setOutgoingLength((byte) 2);
        buffer[0] = (byte) (balanceMoney >> 8);
        buffer[1] = (byte) (balanceMoney & 0xFF);
        apdu.sendBytes((short) 0, (short) 2);
    }
    

    private void verify(APDU apdu) {

        byte[] buffer = apdu.getBuffer();
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if (pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false) {
            ISOException.throwIt(SW_VERIFICATION_FAILED);
        }
    }
    
    private void cvm(APDU apdu){
    	
    	byte[] CVM_Codes = new byte[3];
    	CVM_Codes[0] = (byte) 0x1F;
    	CVM_Codes[1] = (byte) 0x01;
    	CVM_Codes[2] = (byte) 0x04;
    	
    	byte[] CVM_Conditions = new byte[3];
    	CVM_Conditions[0] = (byte)0x06;
    	CVM_Conditions[1] = (byte)0x08;
    	CVM_Conditions[2] = (byte)0x09;
    	
    	byte[] buffer = apdu.getBuffer();
    	short le = apdu.setOutgoing();
    	if(le<10){
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
    	}
    	apdu.setOutgoingLength((byte)10);
    	buffer[0] = CVM_X_1;
    	buffer[1] = CVM_X_2;
    	buffer[2] = CVM_Y_1;
    	buffer[3] = CVM_Y_2;
    	buffer[4] = CVM_Codes[0];
    	buffer[5] = CVM_Conditions[0];
    	buffer[6] = CVM_Codes[1];
    	buffer[7] = CVM_Conditions[1];
    	buffer[8] = CVM_Codes[2];
    	buffer[9] = CVM_Conditions[2];
    	
        apdu.sendBytes((short) 0, (short) 10);
    }
    
    private void verifyCrypto(APDU apdu){

            ISOException.throwIt(SW_VERIFICATION_FAILED);
    }
}

