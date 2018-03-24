/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Katelyn 
 */
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import javax.activation.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;


public class LVAPserver {

    private static String dbURL = "jdbc:derby://localhost:1527/lvap_server";
    private static String tableName = "STRUCTURES";
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;

    //LVAP structure
    //declared as nested static class :
    // When you declare a nested class static, it automatically becomes a stand alone class which can be instantiated without having to instantiate the outer class it belongs to.

    static class LVAP_struct {

        String IP_address;
        byte[] MAC_address;
        byte[]  BSSID;
        String SSID;
        int WTP;

        public LVAP_struct(String ipaddr, byte[] macaddr, byte[] bssidaddr, String netssid, int wtpnum) {
            IP_address = ipaddr;
            MAC_address = macaddr;
            BSSID = bssidaddr;
            SSID = netssid;
            WTP = wtpnum;
        }      
    }

     private static void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(dbURL); 
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
     
    private static void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                conn.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            
        }

    }
    


    private static void insertLVAPstruct(LVAP_struct structure)
    {
        try
        {
            stmt = conn.createStatement();
            stmt.execute("insert into " + tableName + " values ('" +
                         structure.IP_address + "','" + 
                         structure.MAC_address.toString() + "','" + 
                         structure.BSSID.toString() + "','" + 
                         structure.SSID + "'," + 
                         structure.WTP +
                         ")");
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
    
    private static void selectLVAPstruct()
    {
        try
        {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int i=1; i<=numberCols; i++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
            }

            System.out.println("\n--------------------------------------------------------------------");

            while(results.next())
            {
                String ip_addr = results.getString(1);
                String mac_addr = results.getString(2);
                String bssid_addr = results.getString(3);
                String ssid_name = results.getString(4);
                int wtp_num = results.getInt(5);
                System.out.println(ip_addr + "\t\t" + mac_addr + "\t\t" + bssid_addr + "\t\t" + ssid_name + "\t\t" + wtp_num);
            }
            results.close();
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
 

    public static void main(String[] args) throws SQLException, NamingException {

        //createNewDatabase("timak.db");
        //createNewTable();

        //first test entry
        String ssid1 = "sulecnet";
        String ip1 = "147.100.25.6";
        String mac01 = "00:0a:95:9d:68:16";
        String bssid01 = "ee:ee:95:9d:68:e7";
        byte[] mac1 = mac01.getBytes();
        byte[] bssid1 = bssid01.getBytes();
        LVAP_struct sulec = new LVAP_struct(ip1,mac1,bssid1,ssid1,0);

        //second test entry
        String ssid2 = "hepiho wifina";
        String ip2 = "192.168.45.2";
        String mac02 = "08:7a:55:9d:6b:1a";
        String bssid02 = "ee:ee:a5:91:78:e7";
        byte[] mac2 = mac02.getBytes();
        byte[] bssid2 = bssid02.getBytes();
        LVAP_struct hepi = new LVAP_struct(ip2,mac2,bssid2,ssid2,1);


        //third test entry
        String ssid3 = "Opicka.EXE";
        String ip3 = "10.10.15.8";
        String mac03 = "aa:0a:bb:9d:d8:16";
        String bssid03 = "ee:ee:55:9f:d5:b7";
        byte[] mac3 = mac03.getBytes();
        byte[] bssid3 = bssid03.getBytes();
        LVAP_struct opicka = new LVAP_struct(ip3,mac3,bssid3,ssid3,1);

        //fourth test entry
        String ssid4 = "TrubickiFy";
        String ip4 = "125.120.56.1";
        String mac04 = "00:0a:c5:13:ab:cd";
        String bssid04 = "ee:ee:c4:f7:9a:5b";
        byte[] mac4 = mac04.getBytes();
        byte[] bssid4 = bssid04.getBytes();
        LVAP_struct trubicka = new LVAP_struct(ip4,mac4,bssid4,ssid4,1);

        createConnection();
        
        insertLVAPstruct(sulec);
        insertLVAPstruct(hepi);
        insertLVAPstruct(trubicka);
        insertLVAPstruct(opicka);
        
        selectLVAPstruct();
        
        shutdown();
        
        

    }

}
