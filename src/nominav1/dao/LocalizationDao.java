/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nominav1.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import nominav1.libs.DB;
import nominav1.libs.Utils;

/**
 *
 * @author dantecervantes
 */
public class LocalizationDao {
    
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    
    private int id;
    private String name;
    
    public LocalizationDao(int id, String name){
        this.id = id;
        this.name = name;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public Map getCountry(int country_id){
        Map<String, String> country = new HashMap<String, String>();
        
        try{
                String sql = "SELECT * FROM nomina_countries WHERE country_id = '" + country_id + "' LIMIT 1;";
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                boolean returningRows = stmt.execute(sql);
                if (returningRows)
                  rs = stmt.getResultSet();
                else
                  return null;

                while (rs.next()) {
                    country.put("country_id",Integer.toString(rs.getInt("country_id")));
                    country.put("country_name",rs.getString("country_name"));
                }
            }catch(SQLException ex){

            }
        
        return country;
    }
    
    public ArrayList<HashMap<String,Object>> getCountries(){
            ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();            
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{                    
                    String sql = "SELECT * FROM nomina_countries ORDER BY country_name ASC";
                    
                    boolean returningRows = stmt.execute(sql);
                    if (returningRows)
                      rs = stmt.getResultSet();
                    else 
                       return new ArrayList<HashMap<String,Object>>();

                    ResultSetMetaData meta = null;
                    meta = rs.getMetaData();

                    //get column names
                    int colCount = meta.getColumnCount();
                    ArrayList<String> cols = new ArrayList<String>();
                    for (int index=1; index <= colCount; index++)
                      cols.add(meta.getColumnName(index));


                    while (rs.next()) {
                        HashMap<String,Object> row = new HashMap<String,Object>();
                        for (String colName:cols) {
                          Object val = rs.getObject(colName);
                          row.put(colName,val);
                        }
                        rows.add(row);
                    }

                    stmt.close();
                }catch(Exception ex){}
            }
            catch(SQLException es){}
            
            return rows;
    }
}
