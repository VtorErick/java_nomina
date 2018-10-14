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
import nominav1.libs.DB;

/**
 *
 * @author dantecervantes
 */
public class BankDao {
    
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    
    private int id;
    private String name;
    
    public BankDao(int id, String name){
        this.id = id;
        this.name = name;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public ArrayList<HashMap<String,Object>> getAll(){
            ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();            
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{                    
                    String sql = "SELECT * FROM nomina_banks ORDER BY bank_name ASC";
                    
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
