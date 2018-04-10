/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 * Created by katelynx on 11. 11. 2017.
 *
 *
 */

//import org.projectfloodlight.openflow.types.MacAddress;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import org.projectfloodlight.openflow.util.HexString;

public class BSSID_generator {
    
  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}

    public static String getUniqueBSSID(String m) {

        //will be needed later for byte overwrite
        String tmp_mac;
        String e_mac = "ee:ee:ee:ee:ee:ee";
        tmp_mac = e_mac; // ee:ee:ee:ee:ee:ee MAC
        byte[] tmp_byte = tmp_mac.getBytes(); //6 bytes

        //dumb init
        String BSSID = null;

        //dat dvojbodky z MAC adresy do prec
        m= m.replace(":", "");

        //convert from MacAddress to bytes
        byte[] buffer = m.getBytes();

        //create SHA1 fingerprint & convert to byte array & back to MacAddress
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(buffer);
            byte[] digest = Arrays.copyOfRange(md.digest(),0,6); //take the first 6 bytes from the fingerprint

            //overwrite specific bytes in byte array with byte value of 'e' ...hork's wish
            digest[0] = tmp_byte[0];
            digest[1] = tmp_byte[1];

            //BSSID = digest.toString();
            BSSID = bytesToHex(digest);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //control print
        //System.out.println("Povodna MAC je : "+ toStr(m));
        //System.out.println("BSSID MAC je : "+ toStr(BSSID));

        return BSSID;

    }

    public static void main(String[] args) {
        String mac_addr = null;
        mac_addr = "ab:ca:55:f3:fb:c7";
        String converted_mac = getUniqueBSSID(mac_addr);
        
        System.out.println(converted_mac);
    }

}
