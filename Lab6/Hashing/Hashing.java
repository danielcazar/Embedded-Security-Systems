package com.example.mypackage;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;
import javacard.security.MessageDigest;

import javacard.security.CryptoException;

public class Hashing extends Applet {

	private static final byte HW_CLA = (byte) 0x80;						
	private static final byte HW_INS_MD5 = (byte) 0x01;
	private static final byte HW_INS_RIPEMD160 = (byte) 0x02;
	private static final byte HW_INS_SHA = (byte) 0x03;
	private static final byte HW_INS_SHA_224 = (byte) 0x04;
	private static final byte HW_INS_SHA_256 = (byte) 0x05;
	private static final byte HW_INS_SHA_384 = (byte) 0x06;
	private static final byte HW_INS_SHA_512 = (byte) 0x07;
	private static final byte HW_INS_SHA3_224 = (byte) 0x08;
	private static final byte HW_INS_SHA3_256 = (byte) 0x09;
	private static final byte HW_INS_SHA3_384 = (byte) 0xa;
	private static final byte HW_INS_SHA3_512 = (byte) 0xb;
		
	private byte [] receivedData;
	private byte [] hashedData;
	private MessageDigest mesDig;

    private Hashing(byte[] bArray, short bOffset, byte bLength) {
        register();
    } // end of the constructor

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        // create a Wallet applet instance
        new Hashing(bArray, bOffset, bLength);
    } // end of install method

    @Override
    public boolean select() {
        return true;
    }// end of select method

    @Override
    public void process(APDU apdu) {
        if (selectingApplet()) {
          return;
        }

     byte[] buffer = apdu.getBuffer();

     byte CLA = (byte) (buffer[ISO7816.OFFSET_CLA] & 0xFF);

     byte INS = (byte) (buffer[ISO7816.OFFSET_INS] & 0xFF);

     if (CLA != HW_CLA) {

       ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);

     }

     switch (INS) {
      
     case HW_INS_MD5:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_MD5, false);

    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_MD5);
    		mesDig.reset();
    		break;
    	case HW_INS_RIPEMD160:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_RIPEMD160, false);

    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_RIPEMD160);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);

    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA_224:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA_224, false);

    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA_224);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA_256:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA_256);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA_384:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA_384, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA_384);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA_512:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA_512, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA_512);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA3_224:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA3_224, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA3_224);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA3_256:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA3_256, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA3_256);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA3_384:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA3_384, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA3_384);
    		mesDig.reset();
    		break;
    	case HW_INS_SHA3_512:
    		try {
    			mesDig = MessageDigest.getInstance(MessageDigest.ALG_SHA3_512, false);
    		}catch(CryptoException e){
    			ISOException.throwIt(e.getReason());
    			//NO_SUCH_ALGORITHM = SW2:0x03 
    		}
    		resendHash(apdu, MessageDigest.LENGTH_SHA3_512);
    		mesDig.reset();
    		break;
     	     		
     	default:
     		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
     }
     
   }
        
    void resendHash(APDU apdu, byte hashLength){
    	byte[] buffer = apdu.getBuffer();
    	receiveData(apdu);
    	short recivedDataLength = (short) receivedData.length;
    	hashedData = new byte[(short)hashLength];
    	
    	try {
    		mesDig.doFinal(receivedData, (short)0, recivedDataLength, hashedData,
    			(short)0);
    	}catch(CryptoException e){
 			ISOException.throwIt(e.getReason());
 		}
    	
    	try {
        Util.arrayCopyNonAtomic(hashedData, (short) 0, buffer, (short) 0,
        		hashLength);
    	}catch(ArrayIndexOutOfBoundsException e){
    		ISOException.throwIt((short)0xAEAE);
    	}
        apdu.setOutgoingAndSend((short) 0, hashLength);
    	
    }
    
    void receiveData(APDU apdu) {

        byte[] buffer = apdu.getBuffer();
        short recvLen = apdu.setIncomingAndReceive();        
        receivedData = new byte [recvLen];
        short receivedIdx = 0;
        short dataOffset = apdu.getOffsetCdata();        
        
        while (recvLen > 0) {
        	receivedData[receivedIdx]=buffer[dataOffset];
            recvLen = apdu.receiveBytes(dataOffset);
            receivedIdx++;
            dataOffset++;

        } 
    }   
}