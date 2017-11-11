package es.papofg.nfc.clonning;

public class FactoryCommandsNFC {

	byte[] loadKeyIn0 = null;
    byte[] loadKeyIn1 = null;
    byte[] authenticateKey0 = null;
    byte[] authenticateKey1 = null;
    byte[] readBlock = null;
    byte[] writeBlock = null;
    
    public FactoryCommandsNFC() 
    {
    	//For the Keys A we will always load (loadKeyIn0 and authenticateKey0) it in the key 0 of the reader
    	//For the Keys B we will always load (loadKeyIn1 and authenticateKey1) it in the key 1 of the reader
    	
    	//LA KEY SON LAS ULTIMAS 6 POSICIONES, CAMBIAR EN EL MEGABUCLE
        loadKeyIn0 = new byte[] { (byte) 0xFF, (byte) 0x82, (byte) 0x00,(byte) 0x00,
        (byte) 0x06, (byte) 0x00,(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF };
        loadKeyIn1 = new byte[] { (byte) 0xFF, (byte) 0x82, (byte) 0x00,(byte) 0x01,
                (byte) 0x06, (byte) 0x00,(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    (byte) 0xFF, (byte) 0xFF };
        //el 3 por la cola es el nº de bloque(son 64) para validar,en el penultimo 60 para key A y 61 para key B
        //CAMBIAR DENTRO DE MEGABUCLE
        authenticateKey0 = new byte[] { (byte) 0xFF, (byte) 0x86, (byte) 0x00,(byte) 0x00,
        (byte) 0x05, (byte) 0x01,(byte) 0x00, (byte) 0x04, (byte) 0x60,
            (byte) 0x00 };
        authenticateKey1 = new byte[] { (byte) 0xFF, (byte) 0x86, (byte) 0x00,(byte) 0x00,
                (byte) 0x05, (byte) 0x01,(byte) 0x00, (byte) 0x04, (byte) 0x61,
                    (byte) 0x01 };
        //posicion 3 es el numero de bloque
        //posicion 4 indico 10 para leer 16 bytes
        readBlock = new byte[] { (byte) 0xFF, (byte) 0xB0, (byte) 0x00,(byte) 0x00,
                (byte) 0x10 };
        //los 16 ultimos son los datos
        //la posicion 3 es el num de bloque a escribir
        //la posicion 4 indico 10 para escribir 16 bytes
        writeBlock = new byte[] { (byte) 0xFF, (byte) 0xD6, (byte) 0x00,(byte) 0x00,
                (byte) 0x10, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00,(byte) 0x00, (byte) 0x00 };
    }
    
    public byte[] getLoadKeyA(String keyA)
    {
    	byte[] keyAProbar=StringHexUtils.hexStringToByteArray(keyA);
    	for(int i=0;i<6;i++) 
    	{
    		loadKeyIn0[5+i]=keyAProbar[i];
    	}
        return loadKeyIn0;
    }
    
    public byte[] getLoadKeyB(String keyB)
    {
    	byte[] keyBProbar=StringHexUtils.hexStringToByteArray(keyB);
    	for(int i=0;i<6;i++) 
    	{
    		loadKeyIn1[5+i]=keyBProbar[i];
    	}
        return loadKeyIn1;
    }
    
    public byte[] getReadBlock(int numBlock)
    {
    	//setting the block to read
    	readBlock[3]=(byte) numBlock;
    	return readBlock;
    }
    
    public byte[] getWriteBlock(int numBlock,String dataBlock)
    {
    	//setting the block to read
    	writeBlock[3]=(byte) numBlock;
    	byte[] dataWrite=StringHexUtils.hexStringToByteArray(dataBlock);
    	for(int i=0;i<16;i++) 
    	{
    		writeBlock[5+i]=dataWrite[i];
    	}
    	return writeBlock;
    }
    
    public byte[] getAuthenticateKeyA(int numBlock)
    {
    	//setting the block
    	authenticateKey0[7]=(byte) numBlock;
    	return authenticateKey0;
    }
    
    public byte[] getAuthenticateKeyB(int numBlock)
    {
    	//setting the block
    	authenticateKey1[7]=(byte) numBlock;
    	return authenticateKey1;
    }
    
    
}
