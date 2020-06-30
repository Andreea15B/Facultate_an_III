package com.oracle.jcclassic.samples.wallet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.OwnerPIN;
import javacard.framework.Util;

public class Wallet extends Applet {

    /* constants declaration */

    // code of CLA byte in the command APDU header
    final static byte Wallet_CLA = (byte) 0x80;

    // codes of INS byte in the command APDU header
    final static byte VERIFY = (byte) 0x20;
    final static byte CREDIT = (byte) 0x30;
    final static byte DEBIT = (byte) 0x40;
    final static byte GET_BALANCE = (byte) 0x50;
    
    // Am adaugat comanda de cumparare bilete
    final static byte BUY_TICKETS = (byte) 0x80;
    
    // Am adaugat valoarea pentru INS
    final static byte UPDATE_PIN = (byte) 0x70;

    // maximum balance
    final static short MAX_BALANCE = 0x7FFF;
    // maximum transaction amount
    final static short MAX_TRANSACTION_AMOUNT = 650;
    
    
    // Am adaugat numaru maxim de bilete / tranzactie
    final static short MAX_TICKET_AMOUNT = 20;

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

    /* instance variables declaration */
    OwnerPIN pin;
    short balance;

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
            case VERIFY:
                verify(apdu);
                return;
            // Am adaugat comanda pentru a putea fi procesata
            case UPDATE_PIN:
                updatePin(apdu);
                return;
            case BUY_TICKETS:
                buyTickets(apdu);
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
        if ((numBytes != 1) || (byteRead != 1)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // convert 2 byte of creatBytes to a single short value.
        byte creditAmount = buffer[ISO7816.OFFSET_CDATA];

        // check the credit amount
        if ((creditAmount > MAX_TRANSACTION_AMOUNT) || (creditAmount < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }

        // check the new balance
        if ((short) (balance + creditAmount) > MAX_BALANCE) {
            ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
        }

        // credit the amount
        balance = (short) (balance + creditAmount);

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
        
        // get the creditBytes
        byte[] debitBytes = new byte[2];
        debitBytes[0]=buffer[ISO7816.OFFSET_CDATA];
        debitBytes[1]=buffer[ISO7816.OFFSET_CDATA+1];

        // get debit amount
        short debitAmount = Util.getShort(debitBytes,(short)0);;

        // check debit amount
        if ((debitAmount > MAX_TRANSACTION_AMOUNT) || (debitAmount < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }

        // check the new balance
        if ((short) (balance - debitAmount) < (short) 0) {
            ISOException.throwIt(SW_NEGATIVE_BALANCE);
        }

        balance = (short) (balance - debitAmount);

    } // end of debit method

    private void getBalance(APDU apdu) {

        byte[] buffer = apdu.getBuffer();

        // inform system that the applet has finished
        // processing the command and the system should
        // now prepare to construct a response APDU
        // which contains data field
        short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // informs the CAD the actual number of bytes
        // returned
        apdu.setOutgoingLength((byte) 2);

        // move the balance data into the APDU buffer
        // starting at the offset 0
        buffer[0] = (byte) (balance >> 8);
        buffer[1] = (byte) (balance & 0xFF);

        // send the 2-byte balance at the offset
        // 0 in the apdu buffer
        apdu.sendBytes((short) 0, (short) 2);

    } // end of getBalance method

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
    	// Campul de date al APDU-ului va fi de forma [pinSize][pin][newPinSize][newPin]
    
    	// am adaugat verificarea numarului de trials
        if (pin.getTriesRemaining() == 0) {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        
        byte[] buffer = apdu.getBuffer();
        
        // Citim lungimea PIN-ului curent
    	byte pinSize = buffer[ISO7816.OFFSET_LC + 1];
        
    	// Verificam corectitudinea PIN-ului
    	if(!pin.check(buffer, (short)(ISO7816.OFFSET_LC + 2), pinSize)) {
    		ISOException.throwIt(SW_VERIFICATION_FAILED);
    	}
    	
    	// Citim lungimea noului PIN
    	byte newPinSize = (byte)(buffer[(short)(ISO7816.OFFSET_LC + pinSize + 2)]);
    	
    	// Actualizam valoarea PIN-ului
    	pin.update(buffer, (short)(ISO7816.OFFSET_LC + pinSize + 3), newPinSize);
    }
    
    private void buyTickets(APDU apdu) {

        // Verific corectitudinea pin-ului
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        // Citesc comanda APDU
        // Campul de date APDU va fi de forma [Identificatorul mijlocului de transport == pretul unei calatorii][Numarul de bilete spre cmparare]
        byte[] buffer = apdu.getBuffer();
        byte numBytes = (buffer[ISO7816.OFFSET_LC]);
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if ((numBytes != 2) || (byteRead != 2)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        // Identificatorul mijlocului de transport
        byte transportType = buffer[ISO7816.OFFSET_CDATA];
        
        // Numarul de bilete spre cmparare
        byte numberOfTickets = buffer[ISO7816.OFFSET_CDATA + 1];
        
        // Pretul total fara discount
        short transactionAmount = (short)((short) transportType * numberOfTickets);

        // Verificam corectitudinea numarului de bilete
        if ((numberOfTickets > MAX_TICKET_AMOUNT) || (numberOfTickets < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }
        
        // Aplicam discount de grup, daca este cazul
        if (numberOfTickets > 10) {
        	transactionAmount = (short) ((short) transactionAmount * 65 / 100);
        }
        
        // Verificam daca avem suficienti bani in cont
        if ((short) (balance - transactionAmount) < (short) 0) {
            ISOException.throwIt(SW_NEGATIVE_BALANCE);
        }

        balance = (short) (balance - transactionAmount);

    }
} // end of class Wallet