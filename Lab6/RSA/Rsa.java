package com.example.myrsa;

import javacard.security.*;
import javacard.framework.*;
import javacardx.crypto.*;

public class Rsa extends Applet {

	private static final byte HW_CLA = (byte) 0x80;						
	private static final byte HW_INS_GenKeys = (byte) 0x00;
	private static final byte HW_INS_PubKey = (byte) 0x11;
	private static final byte HW_INS_PrivKey = (byte) 0x22;
	private static final byte HW_INS_Verify = (byte) 0x33;	
	
	private static final short SW_REFERENCE_DATA_NOT_FOUND = (short)0x6A88;
	
	private byte[] rsaPubKey;
    private short rsaPubKeyLen;
    private byte[] rsaPriKey;
    private short rsaPriKeyLen;
    private Cipher rsaCipher;
    private Cipher rsaDeCipher;
	
    private Rsa(byte[] bArray, short bOffset, byte bLength) {
        rsaPubKey = new byte[(short)   256];
        rsaPriKey = new byte[(short)   256];
        rsaPubKeyLen = 0;
        rsaPriKeyLen = 0;
        
    	register();
    } // end of the constructor

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        // create a Wallet applet instance
        new Rsa(bArray, bOffset, bLength);
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
      
     case HW_INS_GenKeys:
		 //GEN_RSA_KEYPAIR
		 genRsaKeyPair();
		 break;
    
     case HW_INS_PubKey:
    	 //GET_RSA_PUBKEY
         getRsaPubKey(apdu);
         break;
		 
     case HW_INS_PrivKey:
    	 //GET_RSA_PRIKEY
         getRsaPriKey(apdu);
         break;     	     		

     case HW_INS_Verify:
    	 //VERIFY
    	 rsaVerify(apdu);
         break;
         
     default:
     		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
     }
     
   }
    //Generate Key Pair
    private void genRsaKeyPair()
    {    	
    	    	
        short keyLen = (short)512;
        byte alg = KeyPair.ALG_RSA;
        KeyPair keyPair = new KeyPair(alg, keyLen);

        //(Re)Initializes the key objects encapsulated in this KeyPair instance with new key values.
        keyPair.genKeyPair();
        

        rsaPubKeyLen = 0;
        rsaPriKeyLen = 0;

        //Get a reference to the public key component of this 'keyPair' object.
        RSAPublicKey pubKey = (RSAPublicKey)keyPair.getPublic();
        short pubKeyLen = 0;
        //Store the RSA public key value in the global variable 'rsaPubKey', the public key contains modulo N and Exponent E
        pubKeyLen += pubKey.getModulus(rsaPubKey, pubKeyLen);
        pubKeyLen += pubKey.getExponent(rsaPubKey, pubKeyLen);

        short priKeyLen = 0;
          
       //Returns a reference to the private key component of this KeyPair object.
        RSAPrivateKey priKey = (RSAPrivateKey)keyPair.getPrivate();
        //RSA Algorithm,  the Private Key contains N and D, and store these parameters value in global variable 'rsaPriKey'.
        priKeyLen += priKey.getModulus(rsaPriKey, priKeyLen);
        priKeyLen += priKey.getExponent(rsaPriKey, priKeyLen);

        rsaPubKeyLen = pubKeyLen;
        rsaPriKeyLen = priKeyLen;
    	
    }
    
    
    //Get the value of RSA Public Key from the global variable 'rsaPubKey' 
    private void getRsaPubKey(APDU apdu)
    {
        byte[] buffer = apdu.getBuffer();
        if (rsaPubKeyLen == 0)
        {
            ISOException.throwIt(SW_REFERENCE_DATA_NOT_FOUND);
        }

        
        if (buffer[ISO7816.OFFSET_P1]==0)
        {
        
            Util.arrayCopyNonAtomic(rsaPubKey,(short)0,buffer,(short)0,rsaPubKeyLen);
            //Util.arrayCopyNonAtomic(rsaPubKey,(short)0,buffer,ISO7816.OFFSET_EXT_CDATA,rsaPubKeyLen);
            apdu.setOutgoingAndSend((short)0,rsaPubKeyLen);
         
        }
        else
        {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
            //SW1=6A SW2=86	Incorrect P1 or P2 parameter
         
        }

    }

    //Get the value of RSA Private Key
    private void getRsaPriKey(APDU apdu)
    {
        byte[] buffer = apdu.getBuffer();
        if (rsaPriKeyLen == 0)
        {
            ISOException.throwIt(SW_REFERENCE_DATA_NOT_FOUND);
        }

        
        if (buffer[ISO7816.OFFSET_P1]==0)
        {
        
            Util.arrayCopyNonAtomic(rsaPriKey,(short)0,buffer,(short)0,rsaPriKeyLen);
            apdu.setOutgoingAndSend((short)0,rsaPriKeyLen);
         
        }
        else
        {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
            //SW1=6A SW2=86	Incorrect P1 or P2 parameter
         
        }
    }
    

	
    //RSA algorithm encrypt and decrypt
    private void rsaVerify(APDU apdu)
     {
    	//byte[] hello = { (byte) 'H', (byte) 'e', (byte) 'l', (byte) 'l', (byte) 'o', (byte) '1', (byte) '2', (byte) '3', (byte) '4'};
    	//byte []encryptedData = new byte[64];
    	byte[] buffer = apdu.getBuffer();
    	short Lc = apdu.getIncomingLength();

    	
    	//Create a RSA_PKCS1 object instance
        rsaCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        rsaDeCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        
        short keyLen = (short) 512;
        byte alg = KeyPair.ALG_RSA;
        KeyPair keyPair = new KeyPair(alg, keyLen);

        //(Re)Initializes the key objects encapsulated in this KeyPair instance with new key values.
        keyPair.genKeyPair();

        //Get a reference to the public key component.
        RSAPublicKey pubKey = (RSAPublicKey)keyPair.getPublic();
        
        //Get a reference to the private key component.
        RSAPrivateKey priKey = (RSAPrivateKey)keyPair.getPrivate();
		
    	try {

        //In multiple-part encryption/decryption operations, only the first APDU command will be used.
        rsaCipher.init(pubKey, Cipher.MODE_ENCRYPT);
        
		//Generates encrypted output from all input data.
        //rsaCipher.doFinal(buffer, (short)0, len, buffer, (short)0);
        rsaCipher.doFinal(buffer, (short) ((short) 1 + ISO7816.OFFSET_LC), Lc, buffer, (short) 0);
        //ISO7816.OFFSET_CDATA
                
        //In multiple-part encryption/decryption operations, only the first APDU command will be used.
        rsaDeCipher.init(priKey, Cipher.MODE_DECRYPT);
        
        //Generates encrypted output from all input data.
        rsaDeCipher.doFinal(buffer, (short)0, (short) 64, buffer, (short)0);
        apdu.setOutgoingAndSend((short)0, Lc);

        
    	}catch(CryptoException e){
			ISOException.throwIt(e.getReason());

		}
    	
     }    
}