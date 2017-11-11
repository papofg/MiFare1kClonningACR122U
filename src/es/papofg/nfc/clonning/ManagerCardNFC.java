package es.papofg.nfc.clonning;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;



public class ManagerCardNFC 
{
	Properties propiedades = null;
	String[] keysA=new String[16];
    String[] keysB=new String[16];
    FactoryCommandsNFC cmds = null;
    
    int delay=0;
    int delayWrite=0;
    
    public ManagerCardNFC(String ficheroPropiedades, int delay, int delayWrite) throws Exception
    {
    	propiedades = new Properties();
    	cmds=new FactoryCommandsNFC();
    	this.delay=delay;
    	this.delayWrite=delayWrite;
    	propiedades.load(new FileInputStream(ficheroPropiedades));
    	loadKeysFromProperties(true);
    	loadKeysFromProperties(false);
    }
    
    
    public void loadKeysFromProperties(boolean isKeyA)
    {
    	String[] keys;
    	if(isKeyA)
    		keys=keysA;
    	else
    		keys=keysB;
    	
    	for (int i = 0; i < keys.length; i++) 
        {
            String clave = i+"";
            if(isKeyA)
            	clave+=".a";
            else
            	clave+=".b";
            String key = propiedades.getProperty(clave);
            
            if(key == null)
            {
                System.out.println("Missing KEY "+clave+" in properties file");
                System.exit(1);
            }
            else
            {
                keys[i]=key;
            }
            
        }
    }
    
    
    public int verifyKeysMiFare1k(CardChannel channel) throws Exception
    {
    	int numVerified = 0;
        for(int i=0 ; i < 16; i++)  
        {
        	numVerified+=verifyKeysSectorMiFare1k(i, channel);
        }
    	return numVerified;
    }
    
    
    //numSector: the card has 16 sectors so between 0 and 15
    public int verifyKeysSectorMiFare1k(int numSector, CardChannel channel) throws Exception
    {
    	int numVerfieds=0;
    	boolean isLoadedKeyA=false;
    	boolean isLoadedKeyB=false;

        String resCargaKey=sendCommand(cmds.getLoadKeyA(keysA[numSector]), channel);
        //if the key A is loaded successfully in the reader
        if(resCargaKey.substring(resCargaKey.length()-4).equals("9000"))
        	isLoadedKeyA=true;
        
        resCargaKey=sendCommand(cmds.getLoadKeyB(keysB[numSector]), channel);
        //if the key B is loaded successfully in the reader
        if(resCargaKey.substring(resCargaKey.length()-4).equals("9000"))
        	isLoadedKeyB=true;
        
        
    	if(isLoadedKeyA && isLoadedKeyB)
    	{
    		//Every sector has 4 blocks, checking key A and B in every one
        	numVerfieds+=verifyKeyBlock((numSector*4),true, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4),false, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4)+1,true, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4)+1,false, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4)+2,true, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4)+2,false, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4)+3,true, channel);
        	Thread.sleep(delay);
        	numVerfieds+=verifyKeyBlock((numSector*4)+3,false, channel);
        	Thread.sleep(delay);
    	}
    	else
    	{
    		if(!isLoadedKeyA)
    			System.out.println("The key A ("+keysA[numSector]+") of sector "+numSector+" can't be loaded in the Reader");
    		if(!isLoadedKeyB)
    			System.out.println("The key B ("+keysB[numSector]+") of sector "+numSector+" can't be loaded in the Reader");
    	}
    	
    	
    	return numVerfieds;
    }
    
    //numSector: the card has 16 sectors so between 0 and 15
    public byte[] readSectorMiFare1k(int numSector, CardChannel channel) throws Exception
    {
    	ByteArrayOutputStream  dataSector=new ByteArrayOutputStream ();
    	
        String resCargaKey=sendCommand(cmds.getLoadKeyA(keysA[numSector]), channel);
        //if the key A is loaded successfully in the reader
        if(resCargaKey.substring(resCargaKey.length()-4).equals("9000"))
        {
        	//Every sector has 4 blocks, reading every one
        	dataSector.write(readBlock((numSector*4), channel));
        	Thread.sleep(delay);
        	dataSector.write(readBlock((numSector*4)+1, channel));
        	Thread.sleep(delay);
        	dataSector.write(readBlock((numSector*4)+2, channel));
        	Thread.sleep(delay);
        	dataSector.write(StringHexUtils.hexStringToByteArray(keysA[numSector]));
        	String trailerBlock=StringHexUtils.toHexString(readBlock((numSector*4)+3, channel));
        	dataSector.write(StringHexUtils.hexStringToByteArray(trailerBlock.substring(12, 20)));
        	dataSector.write(StringHexUtils.hexStringToByteArray(keysB[numSector]));
        	Thread.sleep(delay);
        }
        else
        {
        	System.out.println("The key A ("+keysA[numSector]+") of sector "+numSector+" can't be loaded in the Reader");
        }
    	
        dataSector.flush();
        return dataSector.toByteArray();
    }
    
    
    public byte[] readBlock(int numBloque, CardChannel channel)
    {
    	byte[] dataBlock=new byte[0];
    	
    	String resAutenticaKey=sendCommand(cmds.getAuthenticateKeyA(numBloque), channel);
        //if the authentication of the key with the block is OK
        if(resAutenticaKey.substring(resAutenticaKey.length()-4).equals("9000"))
        {
        	String resReadBlock=sendCommand(cmds.getReadBlock(numBloque), channel);
        	System.out.println("Reading block command response("+resReadBlock.length()+")->"+resReadBlock);        
	        if(resReadBlock.substring(resReadBlock.length()-4).equals("9000"))
	        {
	        	System.out.println("Reading block "+numBloque+" OK");
	        	dataBlock=StringHexUtils.hexStringToByteArray(resReadBlock.substring(0, resReadBlock.length()-4));
	        }
	        else
	        {
	        	System.out.println("Reading block "+numBloque+" KO");
	        }
        	
        	
        }
        else
        {
        	System.out.println("The key A ("+keysA[numBloque/4]+") of sector "+(numBloque/4)+" can't be authenticated in the Reader");
        }


        return dataBlock;
    }
    
    //dataBlock will be a hexadecimal string(32) --> 16 bytes
    public int writeBlock(int numBloque, CardChannel channel, String dataBlock)
    {
    	String resAutenticaKey=sendCommand(cmds.getAuthenticateKeyB(numBloque), channel);
        //if the authentication of the key with the block is OK
        if(resAutenticaKey.substring(resAutenticaKey.length()-4).equals("9000"))
        {
        	String resWriteBlock=sendCommand(cmds.getWriteBlock(numBloque,dataBlock), channel);
        	if(resWriteBlock.substring(resWriteBlock.length()-4).equals("9000"))
	        {
	        	System.out.println("Writing block "+numBloque+" OK");

	        }
	        else
	        {
	        	System.out.println("Writing block "+numBloque+" KO");
	        }
        	
        }
        else
        {
        	System.out.println("The key B ("+keysB[numBloque/4]+") of sector "+(numBloque/4)+" can't be authenticated in the Reader");
        }

        return 0;
    }
    
    //numSector: the card has 16 sectors so between 0 and 15
    //dataSector: a hexadecimal string(128)
    public int writeSectorMiFare1k(int numSector, CardChannel channel, String dataSector) throws Exception
    {
    	
        String resCargaKey=sendCommand(cmds.getLoadKeyB(keysB[numSector]), channel);
        //if the key B is loaded successfully in the reader
        if(resCargaKey.substring(resCargaKey.length()-4).equals("9000"))
        {
        	//Every sector has 4 blocks, reading every one
        	writeBlock((numSector*4), channel, dataSector.substring(0, 32));
        	Thread.sleep(delayWrite);
        	writeBlock((numSector*4)+1, channel, dataSector.substring(32, 64));
        	Thread.sleep(delayWrite);
        	writeBlock((numSector*4)+2, channel, dataSector.substring(64, 96));
        	Thread.sleep(delayWrite);
        	writeBlock((numSector*4)+3, channel, dataSector.substring(96, 128));
        	Thread.sleep(delayWrite);
        }
        else
        {
        	System.out.println("The key B ("+keysB[numSector]+") of sector "+numSector+" can't be loaded in the Reader");
        }
    	
    	
    	return 0;
    }
    
    public int verifyKeyBlock(int numBloque,boolean isKeyA, CardChannel channel)
    {
    	String resultText="Block "+numBloque+" - ";
    	int isVerified=0;
    	byte[] command;
        
        if(isKeyA)
        {
            command=cmds.getAuthenticateKeyA(numBloque);
        	resultText+="Key A ("+keysA[numBloque/4]+"):";
        }
        else
        {
        	command=cmds.getAuthenticateKeyB(numBloque);
        	resultText+="Key B ("+keysB[numBloque/4]+"):";
        }
        
        String resAutenticaKey=sendCommand(command, channel);
        //if the authentication of the key with the block is OK
        if(resAutenticaKey.substring(resAutenticaKey.length()-4).equals("9000"))
        {
        	System.out.println(resultText+" OK");
        	isVerified=1;
        }
        else
        {
        	System.out.println(resultText+" KO");
        }
  
        
        return isVerified;
    }
    
    
    public String sendCommand(byte[] cmd, CardChannel channel) {
        String res = "";
        byte[] baResp = new byte[258];
        ByteBuffer bufCmd = ByteBuffer.wrap(cmd);
        ByteBuffer bufResp = ByteBuffer.wrap(baResp);
        // output = The length of the received response APDU
        int output = 0;
        try {
            output = channel.transmit(bufCmd, bufResp);
        } catch (CardException ex) {
            ex.printStackTrace();
        }
        for (int i = 0; i < output; i++) {
            res += String.format("%02X", baResp[i]);
            // The result is formatted as a hexadecimal 
        }
        return res;
    }
    
    public void verifyKeysMiFare1K(CardChannel channel) throws Exception
    {
    	
    	int numVerified = this.verifyKeysMiFare1k(channel);
        if(numVerified!=128)
        {
        	System.out.println("Some key/s are incorrect!! the clone is aborted!!");
        	System.exit(1);
        }
        else
        	System.out.println("All keys are checked!! Let's to clone!!");
    }
    
    
    public void createDumpBackupMiFare1K(CardChannel channel, String nameBackupFile) throws Exception
    {
    	ByteArrayOutputStream  dataCard=new ByteArrayOutputStream ();
        for(int i=0 ; i < 16; i++)
        {
        	dataCard.write(this.readSectorMiFare1k(i, channel));
        }
        OutputStream outputStream = new FileOutputStream(nameBackupFile+"_"+System.currentTimeMillis()+".mfd");
   		dataCard.writeTo(outputStream);
   		outputStream.close();
   		System.out.println("Generated backup file of the card with name: "+nameBackupFile+"_"+System.currentTimeMillis()+".mfd");
    }
    
    //fileDump2Clone: name of dump file 1k
    public void writeDumpInMiFare1K(CardChannel channel, String fileDump2Clone) throws Exception
    {
    	Path path = Paths.get(fileDump2Clone);
        byte[] dataDump = Files.readAllBytes(path);
        String hexDataDump = StringHexUtils.toHexString(dataDump);
    	
    	for(int i=0 ; i < 16; i++)
        {
        	this.writeSectorMiFare1k(i, channel, hexDataDump.substring(i*128,(i+1)*128));
        }
    	
    }

}
