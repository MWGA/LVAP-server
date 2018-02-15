
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;




public class LVAPserver {


    //LVAP structure
    //declared as nested static class :
    // When you declare a nested class static, it automatically becomes a stand alone class which can be instantiated without having to instantiate the outer class it belongs to.

    static class LVAP_struct {
        Integer IP_address;
        byte[] MAC_address;
        byte[]  BSSID;
        String SSID;
        Integer WTP;

        public LVAP_struct(Integer ipaddr, byte[] macaddr, byte[] bssidaddr, String netssid) {
            IP_address = ipaddr;
            MAC_address = macaddr;
            BSSID = bssidaddr;
            SSID = netssid;
        }

        public LVAP_struct() {

        }

    }

    //creating new Database
    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/sqlite/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //creating new table for VAP context
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/timak.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS LVAP (\n"
                + "	ip integer PRIMARY KEY,\n"
                + "	mac blob,\n"
                + "	bssid blob,\n"  //blob is "any" data .. unspecified data ... in our case byte aray
                + "	ssid text\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //connection to database, used later
    public static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/timak.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //inserting row into database
    public static void insert(LVAP_struct lvap) {
        String sql = "INSERT INTO LVAP(ip,mac,bssid,ssid) VALUES(?,?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lvap.IP_address);
            pstmt.setBytes(2, lvap.MAC_address);
            pstmt.setBytes(3, lvap.BSSID);
            pstmt.setString(4, lvap.SSID);
            pstmt.executeUpdate();
            System.out.println("success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //selecting rows from database, for debug purposes
    public static void selectAll(){
        String sql = "SELECT ip,mac,bssid,ssid FROM LVAP";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(
                        rs.getInt("ip") + "\t" + rs.getBinaryStream("mac") + "\t" + rs.getBinaryStream("bssid") + "\t" +
                        rs.getString("ssid")
                        );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void update(int ip, String ssid) {
        String sql = "UPDATE LVAP SET ssid = ?  "
                + "WHERE ip = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, ssid);
            pstmt.setInt(2, ip);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    //creating LVAP object --- not used yet
    LVAP_struct create_lvap() {
        LVAP_struct lvap = new LVAP_struct();

        return lvap;
    }




    public static void main(String[] args) {

        //createNewDatabase("timak.db");
        //createNewTable();

        //first test entry
        String ssid1 = "sulecko";
        Integer ip1 = 147100256;
        String mac01 = "00:0a:95:9d:68:16";
        String bssid01 = "ee:ee:95:9d:68:e7";
        byte[] mac1 = mac01.getBytes();
        byte[] bssid1 = bssid01.getBytes();
        LVAP_struct sulec = new LVAP_struct(ip1,mac1,bssid1,ssid1);

        //second test entry
        String ssid2 = "HePiHo WiFiNa";
        Integer ip2 = 192168452;
        String mac02 = "08:7a:55:9d:6b:1a";
        String bssid02 = "ee:ee:a5:91:78:e7";
        byte[] mac2 = mac02.getBytes();
        byte[] bssid2 = bssid02.getBytes();
        LVAP_struct hepi = new LVAP_struct(ip2,mac2,bssid2,ssid2);


        //third test entry
        String ssid3 = "Opicka.EXE";
        Integer ip3 = 1010158;
        String mac03 = "aa:0a:bb:9d:d8:16";
        String bssid03 = "ee:ee:55:9f:d5:b7";
        byte[] mac3 = mac03.getBytes();
        byte[] bssid3 = bssid03.getBytes();
        LVAP_struct opicka = new LVAP_struct(ip3,mac3,bssid3,ssid3);

        //fourth test entry
        String ssid4 = "TrubickiFy";
        Integer ip4 = 125120561;
        String mac04 = "00:0a:c5:13:ab:cd";
        String bssid04 = "ee:ee:c4:f7:9a:5b";
        byte[] mac4 = mac04.getBytes();
        byte[] bssid4 = bssid04.getBytes();
        LVAP_struct trubicka = new LVAP_struct(ip4,mac4,bssid4,ssid4);

        //insert(sulec);
        //insert(hepi);
        //insert(opicka);
        //insert(trubicka);
        //update(192168452, "hepiho wifina");
        selectAll();
    }

}

