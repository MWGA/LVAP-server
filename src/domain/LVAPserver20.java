/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
   http://www.java2s.com/Code/Java/Network-Protocol/ASimpleWebServer.htm
   https://www.codeproject.com/Tips/1040097/Create-simple-http-server-in-Java
   https://github.com/iamprem/HTTPclient-server/blob/master/HTTPServer/src/Server.java
   http://www.java2s.com/Code/Java/Network-Protocol/AverysimpleWebserverWhenitreceivesaHTTPrequestitsendstherequestbackasthereply.htm  --- robit to podla tohto
 */
package domain;
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


public class LVAPserver20 {
    
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

    private static String host = "jdbc:derby://localhost:1527/lvap_server";
    private static String tableName = "STRUCTURES";
    // jdbc Connection
    private static Connection con = null;
    private static Statement stmt = null;
    
    static String uName = " ";
    static String uPass= " ";
    static ResultSet rs;
    
    private static String getBSSID(String adresa) {
        String result = null;
        
        BSSID_generator gen = new BSSID_generator();
        result = gen.getUniqueBSSID(adresa);
        
        return result;
    }

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
    
    public static boolean checkIP(String ip) throws SQLException {
        
    byte[] macadrr = ip.getBytes();

    
    String sql = "Select 1 from STRUCTURES where MAC = ?";  

    PreparedStatement ps = con.prepareStatement(sql);
    //ps.setString(1, macaddr);
    ps.setBytes(1, macadrr);
    ResultSet rs = ps.executeQuery();

    
    return rs.next();
}

    private static void insertLVAPstruct(LVAP_struct structure)
    {
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
    }
    
    
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
        
        
        
    
    protected static boolean process(String MAC_ADRESA) throws SQLException {
          
        boolean check = checkIP(MAC_ADRESA);
        return check;
    }
    

    protected static void run() throws IOException, SQLException {
      
    
    
    ServerSocket s = new ServerSocket(80);
    
    System.out.println("Cakam vas na porte 8080");
    System.out.println("Pripojte sa niekto ");
    System.out.println("Poslite mi JSON ");

    
    
    while(true) {
        
        // wait for a connection
        Socket client = s.accept(); 
        System.out.println("niekto sa pripojil");
        
        
        // Get input and output streams to talk to the client
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());
        
        //Get client request - MAC address
        String client_request = ".";
        while (!client_request.equals(""))
            client_request = in.readLine();
        
        // Start sending our reply, using the HTTP 1.1 protocol
        out.print("HTTP/1.1 200 \r\n"); // Version & status code
        out.print("Content-Type: text/plain\r\n"); // The type of data
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n"); // End of headers
                
        
        if (process(client_request)){
            out.print("N");
        }
        else {
            out.print("Y");
            //LVAP_struct tmp = new LVAP_struct();
            //insertLVAPstruct(tmp);
            //zatial nemam dalsie parametre k dispozicii......
         }
        
        
        out.flush(); //splachnut po sebe
            
        out.close(); 
        in.close();
        
        client.close();
        
    }
    
    
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        
        LVAPserver20 server = new LVAPserver20();
        server.run();
        
                //first test entry
        String ssid1 = "eduroam";
        String ip1 = "187.150.55.2";
        String mac01 = "9f:0a:35:ef:68:16";
        String bssid01 = "ee:ee:cc:cd:18:e5";
        byte[] mac1 = mac01.getBytes();
        byte[] bssid1 = bssid01.getBytes();
        LVAP_struct sulec = new LVAP_struct(ip1,mac1,bssid1,ssid1,0);

        //second test entry
        String ssid2 = "Guest";
        String ip2 = "152.158.55.5";
        String mac02 = "55:5a:a5:5a:aa:8a";
        String bssid02 = "ee:ee:aa:91:aa:aa";
        byte[] mac2 = mac02.getBytes();
        byte[] bssid2 = bssid02.getBytes();
        LVAP_struct hepi = new LVAP_struct(ip2,mac2,bssid2,ssid2,1);


        //third test entry
        String ssid3 = "vraH";
        String ip3 = "210.210.215.28";
        String mac03 = "45:08:8b:8d:d8:e6";
        String bssid03 = "ee:ee:ff:ff:ee:ee";
        byte[] mac3 = mac03.getBytes();
        byte[] bssid3 = bssid03.getBytes();
        LVAP_struct opicka = new LVAP_struct(ip3,mac3,bssid3,ssid3,1);

        //fourth test entry
        String ssid4 = "Sulec 5G";
        String ip4 = "12.20.6.155";
        String mac04 = "ab:ca:55:f3:fb:c7";
        String bssid04 = "ee:ee:c4:da:da:57";
        byte[] mac4 = mac04.getBytes();
        byte[] bssid4 = bssid04.getBytes();
        LVAP_struct trubicka = new LVAP_struct(ip4,mac4,bssid4,ssid4,1);

        createConnection();
        
        //insertLVAPstruct(sulec);
        //insertLVAPstruct(hepi);
        //insertLVAPstruct(trubicka);
        //insertLVAPstruct(opicka);
        
        //Pomocne vypisiky
        //boolean odpoved = checkIP("45:08:8b:8d:d8:e6");
        //System.out.println(odpoved);
        
        selectLVAPstruct();
        
        shutdown();
               
    }
    
}

