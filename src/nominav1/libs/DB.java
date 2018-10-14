/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nominav1.libs;

/**
 *
 * @author dantecervantes
 */
import java.sql.*;  

public class DB{  
    
    private static String urlstring = "jdbc:mysql://localhost:3306/nomina";    
    private static String driverName = "com.mysql.jdbc.Driver";   
    private static String username = "root";   
    private static String password = "root";
    private static Connection con;

    public static Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(urlstring, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection."); 
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found."); 
        }
        return con;
    }
    
    public static String mysql_real_escape_string(java.sql.Connection link, String str) 
           throws Exception
        {
        
         if (str == null) {
             return null;
         }

         if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
             return str;
         }

         String clean_string = str;
         clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
         clean_string = clean_string.replaceAll("\\n","\\\\n");
         clean_string = clean_string.replaceAll("\\r", "\\\\r");
         clean_string = clean_string.replaceAll("\\t", "\\\\t");
         clean_string = clean_string.replaceAll("\\00", "\\\\0");
         clean_string = clean_string.replaceAll("'", "\\\\'");
         clean_string = clean_string.replaceAll("\\\"", "\\\\\"");

         if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]"
           ,"").length() < 1) 
         {
             return clean_string;
         }

         java.sql.Statement stmt = link.createStatement();
         String qry = "SELECT QUOTE('"+clean_string+"')";

         stmt.executeQuery(qry);
         java.sql.ResultSet resultSet = stmt.getResultSet();
         resultSet.first();
         String r = resultSet.getString(1);
         return r.substring(1,r.length() - 1); 
        
     }

     /**
      * Escape data to protected against SQL Injection
      *
      * @param link
      * @param str
      * @return
      * @throws Exception 
      */

     public static String quote(java.sql.Connection link, String str)
           throws Exception
     {
         if (str == null) {
             return "NULL";
         }
         return "'"+mysql_real_escape_string(link,str)+"'";
     }

     /**
      * Escape identifier to protected against SQL Injection
      *
      * @param link
      * @param str
      * @return
      * @throws Exception 
      */

     public static String nameQuote(java.sql.Connection link, String str)
           throws Exception
     {
         if (str == null) {
             return "NULL";
         }
         return "`"+mysql_real_escape_string(link,str)+"`";
     }
     
     public static String likeQuote(java.sql.Connection link, String str)
           throws Exception
     {
         if (str == null) {
             return "NULL";
         }
         return "'%"+mysql_real_escape_string(link,str)+"%'";
     }
    
}  