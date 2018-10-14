/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nominav1.dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nominav1.NominaV1;
import nominav1.libs.DB;
import nominav1.libs.Utils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author dantecervantes
 */
public class UserDao extends RecursiveTreeObject<UserDao>{
    
       private Connection con = null;
       private Statement stmt = null;
       private ResultSet rs = null;
       
       public StringProperty firstName;
       public StringProperty lastName;
       public StringProperty email;
       public Integer id;
       public StringProperty profile;
       public BankDao banco;
       
       //variables para empleados
       public StringProperty paymentType;
       public StringProperty salary;
       public StringProperty jobPosition;
       public StringProperty country;
       public StringProperty state;
       
       public Button button;
       
       public ObjectProperty<Image> image;  
       
       public UserDao(String imagePath) {
        this(new Image(imagePath),"","","",0,"","","","","","",new Button());
       }
       
       //public ImageView image;
       
       public UserDao(
               Image _image,
               String fName, 
               String lName, 
               String email, 
               int id, 
               String profile,
               String paymentType,
               String salary,
               String jobPosition,
               String country,
               String state,
               Button ... btn) {
        this.image = new SimpleObjectProperty(_image);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.email = new SimpleStringProperty(email);
        this.id = id;
        this.profile = new SimpleStringProperty(profile);
        this.paymentType = new SimpleStringProperty(paymentType);
        this.salary = new SimpleStringProperty(salary);
        this.jobPosition = new SimpleStringProperty(jobPosition);
        this.country = new SimpleStringProperty(country);
        this.state = new SimpleStringProperty(state);
        this.button = btn[0];
       }
       
       public void setButton(Button btn){
           this.button = btn;
       }
       
       public Button getButton(){
           return button;
       }
       
       public void setImage(Image _image) {
           image.set(_image);
        }

        public Image getImage() {
            return image.get();
        }
       
       public BankDao getBanco(){
           return banco;
       }
       
       public void setBanco(BankDao banco){
           this.banco = banco;
       }
       
       public String getState(){
           return state.get();
       }
       
       public void setState(String s){
           state.set(s);
       }
       
       public String getCountry(){
           return country.get();
       }
       
       public void setCountry(String c){
           country.set(c);
       }
       
       public String getJobPosition(){
           return jobPosition.get();
       }
       
       public void setJobPosition(String jp){
           jobPosition.set(jp);
       }
              
       public String getSalary(){
           return salary.get();
       }
       
       public void setSalary(String s){
           salary.set(s);
       }
       
       public String getPaymentType(){
           return paymentType.get();
       }
       
       public void setPaymentType(String pType){
           paymentType.set(pType);
       }
       
       public String getFirstName() {
        return firstName.get();
        }
        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }
        public void setLastName(String fName) {
            lastName.set(fName);
        }

        public String getEmail() {
            return email.get();
        }
        public void setEmail(String fName) {
            email.set(fName);
        }
        
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        
        public String getProfile() {
            return profile.get();
        }
        public void setProfile(String pname) {
            profile.set(pname);
        }
        
        //optional parameters type... variablename
        public ArrayList<HashMap<String,Object>> findUsers(String search, boolean ... isEmploye){
            ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();            
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{
                    String _term = DB.likeQuote(con, search);
                    String sql = "";
                     
                    if(isEmploye[0]){
                        sql = "SELECT u.*, nud.* FROM nomina_users AS u"
                        + " INNER JOIN nomina_user_details AS nud ON nud.user_id = u.user_id"
                        + " WHERE (name LIKE " + _term + " OR "
                            + " lastname LIKE " + _term + " OR "
                            + " email LIKE " + _term + " OR "
                            + " username LIKE " + _term + ") AND u.profile_id = 3";
                        //sql += " AND u.profile_id = 3";
                    }else{
                        sql = "SELECT u.*, np.profile_name FROM nomina_users AS u "
                            + " INNER JOIN nomina_profiles AS np ON np.profile_id = u.profile_id "
                            + " WHERE (name LIKE " + _term + " OR "
                            + " lastname LIKE " + _term + " OR "
                            + " email LIKE " + _term + " OR "
                            + " username LIKE " + _term + ") AND u.profile_id != 3";
                    }
                    
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

                    //close statement
                    stmt.close();
                }catch(Exception ex){}
            }
            catch(SQLException es){}
            
            return rows;
        }
        
        public ArrayList<HashMap<String,Object>> getProfiles(){
            ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();            
            try{
                String sql = "SELECT * FROM nomina_profiles";
               
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
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

              //close statement
              stmt.close();
                
            }catch(SQLException ex){
            }
            return rows;
        }
        
        public boolean update(
                String username, 
                String name, 
                String lastname, 
                String email, 
                String password,
                int is_active,
                int profile_id,
                int userId){
            boolean success = false;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{
                    String _username = DB.quote(con, username);
                    String _name = DB.quote(con, name);
                    String _lastname = DB.quote(con, lastname);
                    String _email = DB.quote(con, email);   
                    String _password = "";
                    
                    if(!password.isEmpty()){
                        _password = DB.quote(con,DigestUtils.shaHex(password));
                    }
                    
                    String sql = "UPDATE nomina_users SET"
                        + " username = " + _username + ","
                        + " name = " + _name + ","
                        + " lastname = " + _lastname + ","
                        + " email = " + _email + ","
                        + " is_active = " + is_active + ","
                        + " profile_id = " + profile_id;
                    
                    if(!_password.isEmpty()){
                        sql += " ,password = " + _password;
                    }
                    
                    sql += " WHERE user_id = " + userId + "";
                    
                    int returningRows = stmt.executeUpdate(sql);
                    if (returningRows > 0)
                      success = true;
                    else
                      success = false;
                }catch(Exception e){}
                
            }catch(SQLException ex){}
            
            return success;
        }
        
        public boolean remove(int userId){
            boolean success = false;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                
                String sql = "DELETE FROM nomina_users "
                    + " WHERE user_id = " + userId + "";

                int returningRows = stmt.executeUpdate(sql);
                if (returningRows > 0)
                  success = true;
                else
                  success = false;
                                
            }catch(SQLException ex){}
            
            return success;
        }
        
        public boolean removeAditionalData(int userId){
            boolean success = false;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                
                String sql = "DELETE FROM nomina_employe_deductions "
                    + " WHERE user_id = " + userId + "";

                int returningRows = stmt.executeUpdate(sql);
                if (returningRows > 0){
                    String _sql = "DELETE FROM nomina_user_details "
                            + "WHERE user_id = " + userId + "";
                    int rows = stmt.executeUpdate(_sql);
                    if(rows > 0)
                        success = true;
                    else
                      success = false;
                }  
                                
            }catch(SQLException ex){}
            
            return success;
        }
        
        public boolean addNew(
                String username, 
                String name, 
                String lastname, 
                String email, 
                String password,
                int is_active,
                int profile_id){
            
            boolean success = false;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{
                    String _username = DB.quote(con, username);
                    String _name = DB.quote(con, name);
                    String _lastname = DB.quote(con, lastname);
                    String _email = DB.quote(con, email);   
                    String _password = DB.quote(con,DigestUtils.shaHex(password));
                    
                    String sql = "INSERT INTO nomina_users SET"
                        + " username = " + _username + ","
                        + " password = " + _password + ","
                        + " name = " + _name + ","
                        + " lastname = " + _lastname + ","
                        + " email = " + _email + ","
                        + " is_active = " + is_active + ","
                        + " profile_id = " + profile_id + "";
               
                    
                    int returningRows = stmt.executeUpdate(sql);
                    if (returningRows > 0)
                      success = true;
                    else
                      success = false;
                }catch(Exception e){}
                
            }catch(SQLException ex){}
            
            return success;
        }
        
        public int login(String username, String password){
            int user_id = 0;
            try{
                
               String sql = "SELECT * FROM nomina_users "
                       + "WHERE username = '" + username + "' "
                       + "AND password = '" + password + "' "
                       + "AND is_active = 1 "
                       + "LIMIT 1";
               
               con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
                boolean returningRows = stmt.execute(sql);
                if (returningRows)
                  rs = stmt.getResultSet();
                else
                  return user_id;
                
                
                while (rs.next()) {
                    user_id = rs.getInt("user_id");
                }
                
            } catch(SQLException exc){
                System.out.print(exc.getMessage());
            }
            
            return user_id;
        }
        
        public ArrayList<HashMap<String,Object>> getDeductions(int employe_id){
             ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();
            try{
                String sql = "SELECT ned.*, nd.deduction_name, nd.deduction_type, nd.deduction_price FROM nomina_employe_deductions AS ned "
                        + " INNER JOIN nomina_deductions AS nd ON nd.deduction_id = ned.deduction_id"
                        + " WHERE user_id = " + employe_id + ";";
                
                
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
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

              //close statement
              stmt.close();
                
                
            }catch(SQLException es){}
            return rows;
        }
        
        public Map getEmploye(int user_id){
            Map<String, String> user = new HashMap<String, String>();
            
            try{
                String sql = "SELECT u.*, nud.*"
                     + " FROM nomina_users u"
                     + " INNER JOIN nomina_user_details AS nud ON nud.user_id = u.user_id"
                     + " WHERE u.user_id = '"  + user_id +  "'"
                        + " AND u.profile_id = 3";

                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                boolean returningRows = stmt.execute(sql);
                if (returningRows)
                  rs = stmt.getResultSet();
                else
                  return null;

                

                while (rs.next()) {
                 
                    user.put("username",rs.getString("username"));
                    user.put("firstname",rs.getString("name"));
                    user.put("lastname",rs.getString("lastname"));
                    user.put("email",rs.getString("email"));
                    user.put("state",rs.getString("state"));
                    user.put("curp",rs.getString("curp"));
                    user.put("rfc",rs.getString("rfc"));
                    user.put("address",rs.getString("address"));
                    user.put("birthdate",Utils.formatDate(rs.getString("birthdate")));
                    user.put("cp",rs.getString("cp"));
                    user.put("clabe",rs.getString("bank_clabe"));
                    user.put("is_active",Integer.toString(rs.getInt("is_active")));
                    user.put("paymentType", Integer.toString(rs.getInt("payment_type")));
                    user.put("salary", Integer.toString(rs.getInt("payment_amount")));
                    user.put("country_id",Integer.toString(rs.getInt("country")));
                    user.put("job_position",rs.getString("job_position"));
                    user.put("bank_id",Integer.toString(rs.getInt("bank_id")));
                    user.put("image", rs.getString("user_image"));
                    
                }
                
                return user;

            }catch(SQLException ex){
                System.out.print(ex.getMessage());
            }

            return user;
        }
        
        public ArrayList<HashMap<String,Object>> getUsers(){
            ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();
            try{
                String sql = "SELECT u.*, np.profile_name FROM nomina_users AS u"
                        + " INNER JOIN nomina_profiles AS np ON np.profile_id = u.profile_id"
                        + " WHERE u.profile_id != 3";
                
                 
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
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

              //close statement
              stmt.close();

            } catch(SQLException exc){
                System.out.print("ERROR => " + exc.getMessage());
            }
           
            return rows;
       }
       
       public ArrayList<HashMap<String,Object>> getEmployes(){
            ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();
            try{
                String sql = "SELECT u.*, nud.* FROM nomina_users AS u"
                        + " INNER JOIN nomina_user_details AS nud ON nud.user_id = u.user_id"
                        + " WHERE u.profile_id = 3";
                                
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
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

              //close statement
              stmt.close();

            } catch(SQLException exc){
                System.out.print("ERROR => " + exc.getMessage());
            }
           
            return rows;
        }
       
        public void removeEmployeDeductions(int employe_id){
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
                //remover todas las deducciones
                String remove = "DELETE FROM nomina_employe_deductions WHERE user_id = " + employe_id;
                stmt.executeUpdate(remove);
            }
            catch(SQLException es){}
        }
       
        //employe
        public void addEmployeDeduction(int employe_id, int deduIndex){
            boolean success;
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;
                
                String _sql = "INSERT INTO nomina_employe_deductions SET"
                + " user_id = " + employe_id + ","
                + " deduction_id = " + deduIndex;
                
                int returningRows = stmt.executeUpdate(_sql);
                if (returningRows > 0)
                  success = true;
                else
                  success = false;
                
            } catch(SQLException ex){}
        }
        public int addNewEmploye(
                String name, 
                String lastname, 
                String email, 
                String password,
                int is_active
        ){
            
            int lastInsertedId = 0;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{
                    String _username = DB.quote(con, Utils.randomIdentifier());
                    String _name = DB.quote(con, name);
                    String _lastname = DB.quote(con, lastname);
                    String _email = DB.quote(con, email);   
                    String _password = DB.quote(con,password);
                    
                    String sql = "INSERT INTO nomina_users SET"
                        + " username = " + _username + ","
                        + " password = " + _password + ","
                        + " name = " + _name + ","
                        + " lastname = " + _lastname + ","
                        + " email = " + _email + ","
                        + " is_active = " + is_active + ","
                        + " profile_id = 3";
               
                    
                    int returningRows = stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
                    if (returningRows > 0){
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (rs.next()){
                            lastInsertedId = rs.getInt(1);
                        }
                        rs.close();

                        stmt.close();
                    }
                     
                }catch(Exception e){}
            }catch(SQLException ex){}
            
            return lastInsertedId;
        }
        
        public boolean addEmployeData(
                int user_id,
                String state,
                int country,
                int bank,
                String clabe,
                int paymentType,
                String paymentAmount,
                String curp,
                String rfc,
                String address,
                String birthdate,
                String cp,
                String jobPosition,
                String image
        ){
            boolean success = false;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{
                    String _curp = DB.quote(con, curp);
                    String _rfc = DB.quote(con, rfc);
                    String _state = DB.quote(con, state);
                    String _address = DB.quote(con, address);   
                    String _jobPosition = DB.quote(con, jobPosition);   
                    String _clabe = DB.quote(con, clabe);   
                    String _birthdate = DB.quote(con,birthdate);
                    
                    String sql = "INSERT INTO nomina_user_details SET"
                        + " user_id = " + user_id + ","
                        + " payment_type = " + paymentType + ","
                        + " payment_amount = " + paymentAmount + ","
                        + " curp = " + _curp + ","
                        + " birthdate = " + _birthdate + ","
                        + " rfc = " + _rfc + ","
                        + " country = " + country + ","
                        + " state = " + _state + ","
                        + " address = " + _address + ","
                        + " cp = " + cp + ","
                        + " job_position = " + _jobPosition + ","
                        + " bank_id = " + bank + ","
                        + " bank_clabe = " + _clabe + ","
                        + " user_image = '" + image + "'"; 
                                                            
                    int returningRows = stmt.executeUpdate(sql);
                    if (returningRows > 0)
                      success = true;
                    else
                      success = false;
                    
                }catch(Exception er){ System.out.print("ERROR => " + er.getMessage()); }
            }catch(SQLException ex){ System.out.print("ERROR => " + ex.getMessage()); }
            
            return success;
        }
        
        public boolean updateEmployeData(
                int user_id,
                String state,
                int country,
                int bank,
                String clabe,
                int paymentType,
                String paymentAmount,
                String curp,
                String rfc,
                String address,
                String birthdate,
                String cp,
                String jobPosition,
                String image
        ){
            boolean success = false;
            
            try{
                con = DB.getConnection();
                stmt = con.createStatement();
                rs = null;

                try{
                    String _curp = DB.quote(con, curp);
                    String _rfc = DB.quote(con, rfc);
                    String _state = DB.quote(con, state);
                    String _address = DB.quote(con, address);   
                    String _jobPosition = DB.quote(con, jobPosition);   
                    String _clabe = DB.quote(con, clabe);   
                    String _birthdate = DB.quote(con,birthdate);
                    
                    String sql = "UPDATE nomina_user_details SET"
                        + " payment_type = " + paymentType + ","
                        + " payment_amount = " + paymentAmount + ","
                        + " curp = " + _curp + ","
                        + " birthdate = " + _birthdate + ","
                        + " rfc = " + _rfc + ","
                        + " country = " + country + ","
                        + " state = " + _state + ","
                        + " address = " + _address + ","
                        + " cp = " + cp + ","
                        + " job_position = " + _jobPosition + ","
                        + " bank_id = " + bank + ","
                        + " bank_clabe = " + _clabe + ","
                        + " user_image = '" + image + "'"
                        + " WHERE user_id = "+ user_id +""; 
                                                            
                    int returningRows = stmt.executeUpdate(sql);
                    if (returningRows > 0)
                      success = true;
                    else
                      success = false;
                    
                }catch(Exception er){ System.out.print("ERROR => " + er.getMessage()); }
            }catch(SQLException ex){ System.out.print("ERROR => " + ex.getMessage()); }
            
            return success;
        }
    
}

