package es.papofg.nfc.clonning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class ClonningNFC 
{
	static CardTerminal terminal = null;
	static Card card = null;
    static CardChannel channel = null;
	/**
     * @param args the command line arguments 
     * 
     */
    public static void main(String[] args) 
    {
        try 
        {
        	/*
        	 * ONLY FOR MIFARE1K CARDS AND USB NFC READER ACR122U
        	 * 
        	 * https://www.acs.com.hk/download-manual/419/API-ACR122U-2.04.pdf
        	 * 
        	 */
        	
        	//Command-line parameters initialization in development
        	String fileDump2Clone="C:\\Users\\xx\\Desktop\\NFC\\pruebaClon.mfd";
        	String fileKeys="C:\\Users\\xx\\eclipse-workspace\\ClonningNFC\\src\\es\\papofg\\nfc\\clonning\\keys.properties";
        	String cardName4Backup="GRABADA"; //it will be a part of the backup filename so better no spaces, strange characters bla bla bla
        	int delayRead=50;  //in milliseconds
        	int delayWrite=2000;  //in milliseconds
        	
        	
            //Checking the parameters and set the variables in execution by command-line, example:
//            System.out.println("Numbers of Parameters: "+args.length);
//            
//            if(args.length != 1)
//            {
//                System.out.println("the parameter with the name of the properties file with the keys is required");
//                System.exit(1);
//            }
//            
//            

        	ManagerCardNFC cardManager=new ManagerCardNFC(fileKeys, delayRead,delayWrite);
        	//Initialize the reader and check the card
            initReader();
            //Checking the keys A and B in every block 
            cardManager.verifyKeysMiFare1K(channel);
            //Writing a backup dump file with the card data
            cardManager.createDumpBackupMiFare1K(channel,cardName4Backup);
            
            System.out.println("Do you want to record the file in the card? (Type (Y/N) and <INTRO>): ");

            BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
            if("Y".equalsIgnoreCase(br.readLine()))
            {
            	//Writing the dump file in the card
    			cardManager.writeDumpInMiFare1K(channel,fileDump2Clone);
            }
            
            
          
//        	comparaFicheros();
            
            
            card.disconnect(true);
            System.out.println("End......");
            
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(ClonningNFC.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
    
    
    public static void initReader() throws Exception
    {
        terminal = null;
        InfoCardNFC infoCard=new InfoCardNFC();
        // show the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        String readerName = "";
        for (int i = 0; i < terminals.size(); i++) {

            readerName = terminals.get(i).toString()
                    .substring(terminals.get(i).toString().length() - 2);
            //terminal = terminals.get(i);

            if (readerName.equalsIgnoreCase(" 0")) {
                terminal = terminals.get(i);
            }
        }
        // Establish a connection with the card
        System.out.println("Put the card over the reader..");
        if(terminal==null)
            return;
        terminal.waitForCardPresent(0);
        card = terminal.connect("T=1");
        channel = card.getBasicChannel();
        
        String atrCard=StringHexUtils.toHexString(card.getATR().getBytes());
        System.out.println("NFC Card detected, ATR-->"+atrCard);
        
        String informationCard=InfoCardNFC.getCardList().get(atrCard);
        String[] informationArray=informationCard.split(", ");
        System.out.println("NFC Card Information:");
        System.out.println("**********************************************************************************");
        for(int i=0;i<informationArray.length;i++)
        	System.out.println(informationArray[i]);
        System.out.println("**********************************************************************************");
        String standard = null, typeCard = null;
        
        if(atrCard.length()>25)
        {
        	standard=atrCard.substring(24,26);
            System.out.println("Standard detected, standard-->"+infoCard.getStandard(standard));
        }
        
        if(atrCard.length()>29)
        {
        	typeCard=atrCard.substring(26,30);
            System.out.println("Type Card detected, Type-->"+infoCard.getTypeCard(typeCard));
        }
        
        if(!"0001".equals(typeCard))
        {
        	System.out.println("ERROR: Type Card detected must be Mifare Standard 1K");
        	System.exit(1);
        }

    	
    }
    
    
//    public static void comparaFicheros() throws Exception
//    {
//    	
//    	String fileDump2Clone="C:\\Users\\xx\\Desktop\\NFC\\pruebaClon.mfd";
//    	String fileDump2Clone2="C:\\Users\\xx\\eclipse-workspace\\ClonningNFC\\GRABADA_1510431058867.mfd";
//
//    	Path path = Paths.get(fileDump2Clone);
//        byte[] dataDump = Files.readAllBytes(path);
//        String hexDataDump = StringHexUtils.toHexString(dataDump);
//        
//        Path path2 = Paths.get(fileDump2Clone2);
//        byte[] dataDump2 = Files.readAllBytes(path2);
//        String hexDataDump2 = StringHexUtils.toHexString(dataDump2);
//        
//        System.out.println("*******************************************************************************");
//        System.out.println("FICHERO 1");
//        for(int i=0;i<64;i++)
//        {
//        	System.out.println(i+" - "+hexDataDump.substring(i*32, (i+1)*32));
//        }
//        
//        System.out.println("*******************************************************************************");
//        System.out.println("FICHERO 2");
//        for(int i=0;i<64;i++)
//        {
//        	System.out.println(i+" - "+hexDataDump2.substring(i*32, (i+1)*32));
//        }
//        System.out.println("*******************************************************************************");
//    }
    
    
    
    

}
