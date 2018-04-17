/*
 * Author Katarina Bedejova
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import static java.sql.Types.NULL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LVAPserver20 {
    
    //LVAP structure
    //declared as nested static class :
    //When you declare a nested class static, it automatically becomes a stand alone class which can be instantiated without having to instantiate the outer class it belongs to.
    public static class LVAP_struct {

        String IP_address;
        String MAC_address;
        String  BSSID;
        String SSID;
        int WTP;

        public LVAP_struct(String ipaddr, String macaddr, String bssidaddr, String netssid, int wtpnum) {
            IP_address = ipaddr;
            MAC_address = macaddr;
            BSSID = bssidaddr;
            SSID = netssid;
            WTP = wtpnum;
        }      
    }

    private static String host = "jdbc:derby://localhost:1527/lvap_server";
    private static String tableName = "STRUCTURES";
    // jdbc Connection
    private static Connection con = null;
    private static Statement stmt = null;    
    static String uName = " ";
    static String uPass= " ";
    static ResultSet rs;
    
    //calculate BSSID
    private static String getBSSID(String adresa) {
        String result = null;
        
        BSSID_generator gen = new BSSID_generator();
        result = gen.getUniqueBSSID(adresa);
        
        return result;
    }
    
    //establish a connection with the database
    private static void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            con = DriverManager.getConnection(host); 
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
    
    //shutdown connection with the database
    private static void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (con != null)
            {
                DriverManager.getConnection(host + ";shutdown=true");
                con.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            
        }

    }
    
    //returns empty (null iniatilized) LVAP structure
    static LVAP_struct createLVAP() {    
        return (new LVAP_struct(null,null,null,null,0));
    }
    
    //delete LVAP struct when no longer necessary
    static void disposeOfLVAP(LVAP_struct deleteMe) {        
        deleteMe = null;
    }
    
    //process client request (LVAP server)
    public static LVAP_struct processRequest(String req) throws SQLException {
        System.out.println("Processing client request ...... ");
        List<String> list = new ArrayList<String>(Arrays.asList(req.split(",")));
              
        LVAP_struct tmp = new LVAP_struct(list.get(0),list.get(1),list.get(2),list.get(3),0);
        
        //update or insert?
        //This solution expects that MAC address is ALWAYS specified in a request
        if(checkForMac(tmp.MAC_address)) {
            System.out.println("MAC known. Updating ......");
            return updateLVAP(tmp);
        }
        else {
            System.out.println("MAC unknown. Creating new LVAP structure......");
            return insertLVAPstruct(tmp);
        }
            
     }
     
    //update LVAP record in the database
    public static LVAP_struct updateLVAP(LVAP_struct structure) throws SQLException {
       
        System.out.println("Update started ......");
       //This solution expects that MAC address is ALWAYS specified in a request
  
       if(!structure.IP_address.equals("-")) {
           System.out.println("Adding IP address ......");
           System.out.println("Trying to add IP address ...." + structure.IP_address);
           System.out.println("Corresponding MAC ..... " + structure.MAC_address);
           String query = "UPDATE STRUCTURES SET IP=? WHERE MAC=?";
           PreparedStatement ps = con.prepareStatement(query);
           ps.setString(1, structure.IP_address);
           ps.setString(2, structure.MAC_address);
           ps.executeUpdate();          
       }
       
       if(!structure.SSID.equals("-")) {
           System.out.println("Adding SSID......");
           String query = "UPDATE STRUCTURES SET SSID=? WHERE MAC=?";
           PreparedStatement ps = con.prepareStatement(query);
           ps.setString(1, structure.SSID);
           ps.setString(2, structure.MAC_address);
           ps.executeUpdate();
           
       }
              
       System.out.println("Updated ......");
       return structure;
    }
    
    //method name self-explanatory
    public static LVAP_struct retrieveStructByIP(String value) throws SQLException{
        
      LVAP_struct structure = createLVAP();

      String query = "SELECT * FROM STRUCTURES WHERE IP = ?";

      // create the java statement
      PreparedStatement ps = con.prepareStatement(query);
      ps.setString(1, value);
      
      // execute the query, and get a java resultset
      ResultSet rs = ps.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String IP = rs.getString("IP");
        String MAC = rs.getString("MAC");
        String BSSID = rs.getString("BSSID");
        String SSID = rs.getString("SSID");
        int WTP = rs.getInt("WTP");
        
        //update the structure
        structure.IP_address = IP;
        structure.MAC_address = MAC;
        structure.BSSID = BSSID;
        structure.SSID = SSID;
        structure.WTP = WTP;

      }
      return structure;
      
    }
    
    //method name self-explanatory
    public static LVAP_struct retrieveStructByMAC(String value) throws SQLException{
        
      LVAP_struct structure = createLVAP();
        
      String query = "SELECT * FROM STRUCTURES WHERE MAC = ?";

      // create the java statement
      PreparedStatement ps = con.prepareStatement(query);
      ps.setString(1, value);
      
      // execute the query, and get a java resultset
      ResultSet rs = ps.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String IP = rs.getString("IP");
        String MAC = rs.getString("MAC");
        String BSSID = rs.getString("BSSID");
        String SSID = rs.getString("SSID");
        int WTP = rs.getInt("WTP");
        
        //update the structure
        structure.IP_address = IP;
        structure.MAC_address = MAC;
        structure.BSSID = BSSID;
        structure.SSID = SSID;
        structure.WTP = WTP;
      }
        
        return structure;
    
     }
    
    //method name self-explanatory 
    public static LVAP_struct retrieveStructByBSSID(String value) throws SQLException{
        
        LVAP_struct structure = createLVAP();
        
        String query = "SELECT * FROM STRUCTURES WHERE BSSID = ?";

      // create the java statement
      PreparedStatement ps = con.prepareStatement(query);
      ps.setString(1, value);
      
      // execute the query, and get a java resultset
      ResultSet rs = ps.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String IP = rs.getString("IP");
        String MAC = rs.getString("MAC");
        String BSSID = rs.getString("BSSID");
        String SSID = rs.getString("SSID");
        int WTP = rs.getInt("WTP");
        
        //update the structure
        structure.IP_address = IP;
        structure.MAC_address = MAC;
        structure.BSSID = BSSID;
        structure.SSID = SSID;
        structure.WTP = WTP;
        
        }
      
      return structure;
    }
    
    //method name self-explanatory
    public static LVAP_struct retrieveStructBySSID(String value) throws SQLException{
        
        LVAP_struct structure = createLVAP();
        
        String query = "SELECT * FROM STRUCTURES WHERE SSID = ?";

      // create the java statement
      PreparedStatement ps = con.prepareStatement(query);
      ps.setString(1, value);
      
      // execute the query, and get a java resultset
      ResultSet rs = ps.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String IP = rs.getString("IP");
        String MAC = rs.getString("MAC");
        String BSSID = rs.getString("BSSID");
        String SSID = rs.getString("SSID");
        int WTP = rs.getInt("WTP");
        
        //update the structure
        structure.IP_address = IP;
        structure.MAC_address = MAC;
        structure.BSSID = BSSID;
        structure.SSID = SSID;
        structure.WTP = WTP;
      }
        
        return structure;
    }
    
    //method name self-explanatory
    public static LVAP_struct retrieveStructByWTP(int value) throws SQLException{
        
        LVAP_struct structure = createLVAP();
        
        String query = "SELECT * FROM STRUCTURES WHERE WTP = ?";

      // create the java statement
      PreparedStatement ps = con.prepareStatement(query);
      ps.setInt(1, value);
      
      // execute the query, and get a java resultset
      ResultSet rs = ps.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String IP = rs.getString("IP");
        String MAC = rs.getString("MAC");
        String BSSID = rs.getString("BSSID");
        String SSID = rs.getString("SSID");
        int WTP = rs.getInt("WTP");
        
        //update the structure
        structure.IP_address = IP;
        structure.MAC_address = MAC;
        structure.BSSID = BSSID;
        structure.SSID = SSID;
        structure.WTP = WTP;
      }
        
        return structure;
    }
    
    //checks if a record with given MAC address exists in database
    public static boolean checkForMac(String value) throws SQLException { 
        
        
        System.out.println("Checking for mac ...... : " + value);
        String sql = "Select 1 from STRUCTURES where MAC = ?"; 
        System.out.println(" sql nastavene");
        PreparedStatement prst = con.prepareStatement(sql);
        System.out.println("prepared statement pripraveny");
        prst.setString(1, value);
        ResultSet rs = prst.executeQuery();
        System.out.println("query executed");
                
        return rs.next();      
    }
    
    //checks if a record with given IP address exists in database
    public static boolean checkForIP(String value) throws SQLException { 
        
        String sql = "Select 1 from STRUCTURES where IP = ?";  
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, value);
        ResultSet rs = ps.executeQuery();
        
        return rs.next();
    }
    
    //checks if a record with given BSSID exists in database
    public static boolean checkForBSSID(String value) throws SQLException { 
        
        String sql = "Select 1 from STRUCTURES where BSSID = ?";  
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, value);
        ResultSet rs = ps.executeQuery();
        
        return rs.next();
    }
    
    //inserts LVAP structure into database
    private static LVAP_struct insertLVAPstruct(LVAP_struct structure)
    {
        System.out.println("Insert started ......");
        try
        {
            stmt = con.createStatement();
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
        
        System.out.println("Inserted ......");
        
        return structure;
    }
    
    //debug function - list all DB entries into console
    private static void selectLVAPstruct()
    {
        try
        {
            stmt = con.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int i=1; i<=numberCols; i++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i)+"\t\t\t");  
            }

            System.out.println("\n-----------------------------------------------------------------------------------------------");

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
       
    //server thread
    protected static void run() throws IOException, SQLException {
         
    ServerSocket s = new ServerSocket(80);
    LVAP_struct responseObj; //reference to the LVAP struct that will be later sent to a client as JSON object
    
    System.out.println("Cakam vas na porte 80 :) ");
    System.out.println("Pripojte sa niekto");
    System.out.println("Server waiting for request ..... ");
    
    while(true) {
        
        // wait for a connection
        Socket client = s.accept(); 
        System.out.println("Client connection accepted ......");
               
        // Get input and output streams to talk to the client
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());
        
        String request = in.readLine();

        responseObj = processRequest(request);
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        // JSON data structure
        JsonElement jsonElement = gson.toJsonTree(responseObj);
        JsonObject jsonObject = (JsonObject) jsonElement;
        // property removal
        //jsonObject.remove("property");
        // serialization to String
        String javaObjectString = jsonObject.toString();
        System.out.println(javaObjectString);
        //send to the client
        //out.print(javaObjectString);
        
        out.flush(); //splachnut po sebe            
        out.close(); 
        in.close();        
        client.close();
        
    }        
}
        
    /**
     * MAIN FUNCTION
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        
        createConnection();
        
        LVAPserver20 server = new LVAPserver20();
        server.run();
        
        System.out.println(checkForMac("ab:cd:ef:01:23:45"));
        
        //insertLVAPstruct(sulec);
        //insertLVAPstruct(hepi);
        //insertLVAPstruct(trubicka);
        //insertLVAPstruct(opicka);
        
        //Pomocne vypisiky
        //boolean odpoved = checkIP("45:08:8b:8d:d8:e6");
        //System.out.println(odpoved);
        
        //selectLVAPstruct();
                
        shutdown();
               
    }
    
}

