package es.papofg.nfc.clonning;

public class StringHexUtils {

	public static byte[] hexStringToByteArray(String s)
    {
    	return hexStringToByteArray(s, false);
    }
    
    public static byte[] hexStringToByteArray(String s, boolean withReverse) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        if(withReverse)
        {
        	byte[] dataReverse = new byte[data.length];
            for (int i = 1; i <= dataReverse.length; i ++) {
            	dataReverse[i-1]=data[data.length-i];
            }
            return dataReverse;
        }
        return data;
    }
    
    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString().toUpperCase();
    }
}
