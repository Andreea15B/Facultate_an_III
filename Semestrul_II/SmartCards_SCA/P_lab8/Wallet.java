/** 
 * Copyright (c) 1998, 2019, Oracle and/or its affiliates. All rights reserved.
 * 
 */

/*
 */

/*
 * @(#)Wallet.java	1.11 06/01/03
 */

package com.oracle.jcclassic.samples.wallet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.OwnerPIN;
import javacard.framework.PINException;


public class Wallet extends Applet {

    /* constants declaration */

    // code of CLA byte in the command APDU header
    final static byte Wallet_CLA = (byte) 0x80;

    // codes of INS byte in the command APDU header
    final static byte VERIFY = (byte) 0x20;
    final static byte CREDIT = (byte) 0x30;
    final static byte DEBIT = (byte) 0x40;
    final static byte GET_BALANCE = (byte) 0x50;
    //aici am introdus codul INS pentru comanda de update
    final static byte UPDATE_PIN = (byte) 0x70;

    // maximum balance
    final static short MAX_BALANCE = 6000;
    //setez limita aferenta pentru fiecare caz
    //setez cantitatea maxima de litrii
    final static short MAX_LITERS=500;
    //setez cantitatea maxima de bani pentru combustibil
    final static short MAX_GAS=4000;
    //setez cantitatea maxima de bani pentru produse
    final static short MAX_PRODUCT=1000;
    // maximum transaction amount
    final static short MAX_TRANSACTION_AMOUNT = 600;

    // maximum number of incorrect tries before the
    // PIN is blocked
    final static byte PIN_TRY_LIMIT = (byte) 0x03;
    // maximum size PIN
    final static byte MAX_PIN_SIZE = (byte) 0x08;

    // signal that the PIN verification failed
    final static short SW_VERIFICATION_FAILED = 0x6300;
    // signal the the PIN validation is required
    // for a credit or a debit transaction
    final static short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    // signal invalid transaction amount
    // amount > MAX_TRANSACTION_AMOUNT or amount < 0
    final static short SW_INVALID_TRANSACTION_AMOUNT = 0x6A83;

    // signal that the balance exceed the maximum
    final static short SW_EXCEED_MAXIMUM_BALANCE = 0x6A84;
    // signal the the balance becomes negative
    final static short SW_NEGATIVE_BALANCE = 0x6A85;
    //am introdus un cod de returnare SW1 SW2 specific pentru cazul nostru 
    final static short SW_SECURITY_STATUS_NOT_SATISFIED =0x6982;

    /* instance variables declaration */
    OwnerPIN pin;
   
   
    short balance;
    //declar variabile pentru a retine valorile
    short litriicombustibil;
    short banicombustibil;
    short baniproduse;
  

    private Wallet(byte[] bArray, short bOffset, byte bLength) {

        // It is good programming practice to allocate
        // all the memory that an applet needs during
        // its lifetime inside the constructor
        pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);

        byte iLen = bArray[bOffset]; // aid length
        bOffset = (short) (bOffset + iLen + 1);
        byte cLen = bArray[bOffset]; // info length
        bOffset = (short) (bOffset + cLen + 1);
        byte aLen = bArray[bOffset]; // applet data length

        // The installation parameters contain the PIN
        // initialization value
        pin.update(bArray, (short) (bOffset + 1), aLen);
        register();

    } // end of the constructor

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        // create a Wallet applet instance
        new Wallet(bArray, bOffset, bLength);
    } // end of install method

    @Override
    public boolean select() {

        // The applet declines to be selected
        // if the pin is blocked.
        if (pin.getTriesRemaining() == 0) {
            return false;
        }

        return true;

    }// end of select method

    @Override
    public void deselect() {

        // reset the pin value
        pin.reset();

    }

    @Override
    public void process(APDU apdu) {

        // APDU object carries a byte array (buffer) to
        // transfer incoming and outgoing APDU header
        // and data bytes between card and CAD

        // At this point, only the first header bytes
        // [CLA, INS, P1, P2, P3] are available in
        // the APDU buffer.
        // The interface javacard.framework.ISO7816
        // declares constants to denote the offset of
        // these bytes in the APDU buffer

        byte[] buffer = apdu.getBuffer();
        // check SELECT APDU command

        if (apdu.isISOInterindustryCLA()) {
            if (buffer[ISO7816.OFFSET_INS] == (byte) (0xA4)) {
                return;
            }
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        // verify the reset of commands have the
        // correct CLA byte, which specifies the
        // command structure
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
            //am legat cazul INS de functia din applet
            case UPDATE_PIN:
            	updatePin(apdu);
            	return;
            case VERIFY:
                verify(apdu);
                return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }

    } // end of process method

    private void credit(APDU apdu) {

        // access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        byte[] buffer = apdu.getBuffer();

        // Lc byte denotes the number of bytes in the
        // data field of the command APDU
        byte numBytes = buffer[ISO7816.OFFSET_LC];

        // indicate that this APDU has incoming data
        // and receive data starting from the offset
        // ISO7816.OFFSET_CDATA following the 5 header
        // bytes.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // it is an error if the number of data bytes
        // read does not match the number in Lc byte
        if ((numBytes != 3) || (byteRead != 3)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // avand LC=1 o sa primesc in CDATA cazul specific de creditare
        byte command = buffer[ISO7816.OFFSET_CDATA];
        //in creditAmount o sa primesc cantitatea specifica cazului ales la comanda
        byte creditAmount1 = buffer[ISO7816.OFFSET_CDATA+1];
        byte creditAmount2 = buffer[ISO7816.OFFSET_CDATA +2];
       
        short creditAmount = (short)((creditAmount1 << 8) | (creditAmount2 & 0xFF));


        //in functie de comanda aleasa cantitatea se va duce pe una din cele 3 cuantificari
        if(command==0)
        {
        	 if ((short) (banicombustibil + creditAmount) > MAX_GAS) {
                 ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
             }

        	banicombustibil=(short)(banicombustibil+creditAmount);
        }
        else if(command==1)
        {
        	 if ((short) (baniproduse + creditAmount) > MAX_PRODUCT) {
                 ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
             }

        	baniproduse=(short)(baniproduse+creditAmount);
        }
        else if(command==2)
        {
        	 if ((short) (litriicombustibil + creditAmount) > MAX_LITERS) {
                 ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
             }

        	litriicombustibil=(short)(litriicombustibil+creditAmount);
        }


    } // end of deposit method

    private void debit(APDU apdu) {

        // access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        byte[] buffer = apdu.getBuffer();

        byte numBytes = (buffer[ISO7816.OFFSET_LC]);

        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if ((numBytes != 2) || (byteRead != 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // get debit amount
        byte debitAmount1 = buffer[ISO7816.OFFSET_CDATA];
        byte debitAmount2 = buffer[ISO7816.OFFSET_CDATA +1];
       
        short debitAmount = (short)((debitAmount1 << 8) | (debitAmount2 & 0xFF));

        // check debit amount
        if ((debitAmount > MAX_TRANSACTION_AMOUNT) || (debitAmount < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }

        // check the new balance
        if ((short) (balance - debitAmount) < (short) 0) {
            ISOException.throwIt(SW_NEGATIVE_BALANCE);
        }


    } // end of debit method

    private void getBalance(APDU apdu) {

        byte[] buffer = apdu.getBuffer();

        // inform system that the applet has finished
        // processing the command and the system should
        // now prepare to construct a response APDU
        // which contains data field
        short le = apdu.setOutgoing();
        
        //o sa am LC=1 iar in CDATA una din cele 7 combinatii posibile in care pot sa afisez cele 3 balante
        //LE va avea valoarea in functie de cata informatie doresc sa afisez pe un anumit caz
        byte command=buffer[ISO7816.OFFSET_CDATA];
        //afisez bani combustibil
        if(command==0) 
        {
        	if (le < 2) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 2);

            buffer[0] = (byte) (banicombustibil >> 8);
            buffer[1] = (byte) (banicombustibil & 0xFF);
                        
            apdu.sendBytes((short) 0, (short) 2);

        }
        //afisez bani produse
        else if(command==1)
        {
        	if (le < 2) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 2);

            buffer[0] = (byte) (baniproduse >> 8);
            buffer[1] = (byte) (baniproduse & 0xFF);
                        
            apdu.sendBytes((short) 0, (short) 2);

        }
        //afisez litrii combustibil
        else if(command==2)
        {
        	if (le < 2) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 2);

            buffer[0] = (byte) (litriicombustibil >> 8);
            buffer[1] = (byte) (litriicombustibil & 0xFF);
                        
            apdu.sendBytes((short) 0, (short) 2);

        }
        // afisez toate balantele
        else if(command==3)
        {
        	if (le < 6) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 6);
            buffer[0] = (byte) (banicombustibil >> 8);
            buffer[1] = (byte) (banicombustibil & 0xFF);
            buffer[2] = (byte) (baniproduse >> 8);
            buffer[3] = (byte) (baniproduse & 0xFF);
            buffer[4] = (byte) (litriicombustibil >> 8);
            buffer[5] = (byte) (litriicombustibil & 0xFF);
                        
            apdu.sendBytes((short) 0, (short) 6);
        }
        //afisez bani combustibil + bani produse
        else if(command==4)
        {
        	if (le < 4) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 4);
            buffer[0] = (byte) (banicombustibil >> 8);
            buffer[1] = (byte) (banicombustibil & 0xFF);
            buffer[2] = (byte) (baniproduse >> 8);
            buffer[3] = (byte) (baniproduse & 0xFF);
                                   
            apdu.sendBytes((short) 0, (short) 4);
        }
        //afisez bani produse + litrii combustibil
        else if(command==5)
        {
        	if (le < 4) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 4);
            
            buffer[0] = (byte) (baniproduse >> 8);
            buffer[1] = (byte) (baniproduse & 0xFF);
            buffer[2] = (byte) (litriicombustibil >> 8);
            buffer[3] = (byte) (litriicombustibil & 0xFF);
                        
            apdu.sendBytes((short) 0, (short) 4);
        }
        //afisez bani combustibil + litrii combustibil
        else if(command==6)
        {
        	if (le < 4) {
                ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            }

            apdu.setOutgoingLength((byte) 4);
            buffer[0] = (byte) (banicombustibil >> 8);
            buffer[1] = (byte) (banicombustibil & 0xFF);
            buffer[2] = (byte) (litriicombustibil >> 8);
            buffer[3] = (byte) (litriicombustibil & 0xFF);
                        
            apdu.sendBytes((short) 0, (short) 4);
        }

       
    } 

    private void verify(APDU apdu) {

        byte[] buffer = apdu.getBuffer();
        // retrieve the PIN data for validation.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // check pin
        // the PIN data is read into the APDU buffer
        // at the offset ISO7816.OFFSET_CDATA
        // the PIN data length = byteRead
        if (pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false) {
            ISOException.throwIt(SW_VERIFICATION_FAILED);
        }

    } // end of validate method
    
    private void updatePin(APDU apdu) {
    	
    	
    	//dupa toate cele PIN_TRY_LIMIT au fost epuizate, Ownerpinul initial selectat a fost deselectat
    	if(!select()) 
    	{
    		ISOException.throwIt(SW_SECURITY_STATUS_NOT_SATISFIED);
    	}
    	//primesc buffer din apdu
    	byte[] buffer = apdu.getBuffer();
    	
    	byte numBytes = buffer[ISO7816.OFFSET_LC];
        //formatul CDATA este in felul urmator
    	//numar cifre pin vechi + pin vechi + numar cifre pin nou + pin nou
    	
    	//citim numarul de cifre din pinul vechi
    	byte numberofdigits1= buffer[ISO7816.OFFSET_CDATA];
    	//cream un byte array gol de dimensiunea pinului vechi
        byte[] oldpin=new byte[(short)numberofdigits1];
        short i=0;
        //retin pinul vechi
        for(i=0; i< (short)numberofdigits1; i++)
        	oldpin[i]= buffer[(short)(ISO7816.OFFSET_CDATA +1+i)];
        
        //citim numarul de cifre din pinul nou
        byte numberofdigits2= buffer[(short)(ISO7816.OFFSET_CDATA +i+1)];
        //cream un byte array gol de dimensiunea pinului nou
        byte[] newpin=new byte[(short)numberofdigits2];
        short j=0;
        //retin pinul nou
        for(j=0; j< (short)numberofdigits2; j++)
        	newpin[j]= buffer[(short)(ISO7816.OFFSET_CDATA +i+2+j)];
        
       
        //verificam pinul existent din Ownerpin cu pinul vechi dat ca paramentru in functie
        if (pin.check(oldpin,(short) 0, numberofdigits1) == false) {
        	
        	//pentru primele PIN_TRY_LIMIT incercari esuate va fi afisat SW1 SW2 63 00 
            ISOException.throwIt(SW_VERIFICATION_FAILED);
            
        }
        else 
        {
        	try {
        	//ISOException.throwIt((short)newpin[0]);
        	//daca totul a decurs bine facem update cu noul pin	
        	pin.update(newpin, (short) 0,(byte) ((short)numberofdigits2));
        	//ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
        	
        	}
        	catch(PINException c) {
        		short reason=c.getReason();
        		ISOException.throwIt(reason);
        	}
        	 
        }

    	
    }
} // end of class Wallet

