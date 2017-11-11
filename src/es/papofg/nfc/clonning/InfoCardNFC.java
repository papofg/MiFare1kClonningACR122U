package es.papofg.nfc.clonning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class InfoCardNFC {

	
	public String getStandard(String standard)
	{
		String cardName;
		
		switch(StringHexUtils.hexStringToByteArray(standard)[0])
		{
			case 0: cardName = "No card information"; break;
			case 1: cardName = "ISO 14443 A, Part1 Card Type"; break;
			case 2: cardName = "ISO 14443 A, Part2 Card Type"; break;
			case 3: cardName = "ISO 14443 A, Part3 Card Type"; break;
			case 5: cardName = "ISO 14443 B, Part1 Card Type"; break;
			case 6: cardName = "ISO 14443 B, Part2 Card Type"; break;
			case 7: cardName = "ISO 14443 B, Part3 Card Type"; break;
			case 9: cardName = "ISO 15693, Part1 Card Type"; break;
			case 10: cardName = "ISO 15693, Part2 Card Type"; break;
			case 11: cardName = "ISO 15693, Part3 Card Type"; break;
			case 12: cardName = "ISO 15693, Part4 Card Type"; break;
			case 13: cardName = "Contact Card (7816-10) IIC Card Type"; break;
			case 14: cardName = "Contact Card (7816-10) Extended IIC Card Type"; break;
			case 15: cardName = "Contact Card (7816-10) 2WBP Card Type"; break;
			case 16: cardName = "Contact Card (7816-10) 3WBP Card Type"; break;
			default: cardName = "Undefined card"; break;
		}
		
		
		return cardName;
	}
	
	public String getTypeCard(String typeCard)
	{
		String cardName="";
		byte[] typeCardBytes=StringHexUtils.hexStringToByteArray(typeCard);
		
		
		
		if(Integer.toHexString(((Byte)typeCardBytes[0]).intValue() & 0xFF).equals("0"))
		{
		
			switch(typeCardBytes[1])
			{
			
				case 0x01: cardName = cardName + "Mifare Standard 1K"; break;
                case 0x02: cardName = cardName + "Mifare Standard 4K"; break;
                case 0x03: cardName = cardName + "Mifare Ultra light"; break;
                case 0x04: cardName = cardName + "SLE55R_XXXX"; break;
                case 0x06: cardName = cardName + "SR176"; break;
                case 0x07: cardName = cardName + "SRI X4K"; break;
                case 0x08: cardName = cardName + "AT88RF020"; break;
                case 0x09: cardName = cardName + "AT88SC0204CRF"; break;
                case 0x0A: cardName = cardName + "AT88SC0808CRF"; break;
                case 0x0B: cardName = cardName + "AT88SC1616CRF"; break;
                case 0x0C: cardName = cardName + "AT88SC3216CRF"; break;
                case 0x0D: cardName = cardName + "AT88SC6416CRF"; break;
                case 0x0E: cardName = cardName + "SRF55V10P"; break;
                case 0x0F: cardName = cardName + "SRF55V02P"; break;
                case 0x10: cardName = cardName + "SRF55V10S"; break;
                case 0x11: cardName = cardName + "SRF55V02S"; break;
                case 0x12: cardName = cardName + "TAG IT"; break;
                case 0x13: cardName = cardName + "LRI512"; break;
                case 0x14: cardName = cardName + "ICODESLI"; break;
                case 0x15: cardName = cardName + "TEMPSENS"; break;
                case 0x16: cardName = cardName + "I.CODE1"; break;
                case 0x17: cardName = cardName + "PicoPass 2K"; break;
                case 0x18: cardName = cardName + "PicoPass 2KS"; break;
                case 0x19: cardName = cardName + "PicoPass 16K"; break;
                case 0x1A: cardName = cardName + "PicoPass 16KS"; break;
                case 0x1B: cardName = cardName + "PicoPass 16K(8x2)";break;
                case 0x1C: cardName = cardName + "PicoPass 16KS(8x2)";break;
                case 0x1D: cardName = cardName + "PicoPass 32KS(16+16)";break;
                case 0x1E: cardName = cardName + "PicoPass 32KS(16+8x2)";break;
                case 0x1F: cardName = cardName + "PicoPass 32KS(8x2+16)";break;
                case 0x20: cardName = cardName + "PicoPass 32KS(8x2+8x2)";break;
                case 0x21: cardName = cardName + "LRI64";break;
                case 0x22: cardName = cardName + "I.CODE UID";break;
                case 0x23: cardName = cardName + "I.CODE EPC";break;
                case 0x24: cardName = cardName + "LRI12";break;
                case 0x25: cardName = cardName + "LRI128";break;
                case 0x26: cardName = cardName + "Mifare Mini";break;
			
			}
			
		}
		else
		{
			
			if (typeCardBytes[0]==0xFF)
			{
				switch(typeCardBytes[1])
				{
				
					case 9: cardName = cardName + "Mifare Mini"; break;
				
				}
				
			}
			else if(typeCardBytes[0]==0xF0)
			{
				switch (typeCardBytes[1])
                {
                 	case 0x11:
	                 	cardName = cardName + "FeliCa 212K";
	                    break;
                 	case 0x12:
	                    cardName = cardName + "Felica 424K";
	                    break;
                 	case 0x04:
	                    cardName = cardName + "Topaz";
	                    break;

                 }	     
			}
			
		}

		return cardName;
	}
	
	private static HashMap<String, String> c_CardList;
	public static HashMap<String, String> getCardList() {
        if (c_CardList == null) {
            HashMap<String, String> list = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(InfoCardNFC.class.getResourceAsStream("smartcard_list.txt"),
                        "utf-8"))) {
                boolean done = false;
                do {
                    String line = reader.readLine();
                    if (line == null) {
                        done = true;
                    } else if (line.startsWith("#") || line.matches("")) {
                        continue;
                    } else {
                        String atr = line.replace(" ", "").trim();
                        StringBuffer desc = new StringBuffer();
                        boolean descDone = false;
                        while(!descDone) {
                            String descLine = reader.readLine();
                            if(descLine == null) {
                                done = true;
                                break;
                            }
                            if (descLine.startsWith("\t")) {
                               desc.append((desc.length() > 0) ? ", " : "").append(descLine.trim());
                            } else {
                                descDone = true;
                                list.put(atr, desc.toString());
                            }
                        }
                    }
                } while (!done);
            } catch (IOException e) {
                e.printStackTrace();
            }
            c_CardList = list;
        }
        return c_CardList;
    }
	
}
