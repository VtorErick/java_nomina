/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.users;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import nominav1.dao.UserDao;
import nominav1.libs.DB;
import nominav1.libs.Utils;

/**
 * FXML Controller class
 *
 * @author dantecervantes
 */
public class UserDialogController implements Initializable {
    
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private boolean isUpdating = false;
    private int currentUser = 0;
    
    @FXML private TextField firstNameTxt;
    @FXML private TextField lastNameTxt;
    @FXML private TextField emailTxt;
    @FXML private TextField usernameTxt;
    @FXML private PasswordField passwordTxt;
    @FXML private ComboBox profileId = new ComboBox();
    @FXML private ToggleButton isActive;
    @FXML private Label errorLbl;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        resetFields();
        getProfiles();
    }    
    
    @FXML 
    private void handleSaveUser(ActionEvent event) throws Exception{
        String _firstName = firstNameTxt.getText();
        String _lastName = lastNameTxt.getText();
        String _username = usernameTxt.getText();
        String _email = emailTxt.getText();
        String _password = passwordTxt.getText();
        int _profileId = profileId.getSelectionModel().getSelectedIndex() + 1;
        boolean toggleUserActive = isActive.isSelected();
        
        int _isActive = 0;
        if(toggleUserActive){
            _isActive = 1;
        }
        
        boolean canGo = true;
        
        if(_firstName.isEmpty() || _username.isEmpty()){
            errorLbl.setText("El nombre del usuario es obligatorio");
            errorLbl.setTextFill(Color.web("red"));            
            canGo = false;
        }
        
        if(_lastName.isEmpty()){
            errorLbl.setText("El apellido del usuario es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(!isUpdating && _password.isEmpty()){
            errorLbl.setText("La contraseña es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(_email.isEmpty()){
            errorLbl.setText("El email es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }else {
            if(!Utils.validateEmailAddress(_email)){
                errorLbl.setText("El email no es correcto.");
                errorLbl.setTextFill(Color.web("red")); 
                canGo = false;
            }
        }
        
        if(_profileId == -1){
            errorLbl.setText("El perfil del usuario es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(canGo){
            UserDao user = new UserDao(new Image("images/icons8-camera.png"),"","","",0,"","","","","","",new Button());
            
            String word = "";
            String aword = "";
            boolean createdUser = false;
            if(!isUpdating){
                createdUser = user.addNew(
                    _username,
                    _firstName,
                    _lastName,
                    _email,
                    _password,
                    _isActive,
                    _profileId
                );
                word = "crear";
                aword = "creó";
            }else{
                createdUser = user.update(
                    _username,
                    _firstName,
                    _lastName,
                    _email,
                    _password,
                    _isActive,
                    _profileId,
                    currentUser
                );
                word = "actualizar";
                aword = "actualizó";
            }
           
            
            if(!createdUser){
                errorLbl.setText("El usuario no se pudo " + word);
                errorLbl.setTextFill(Color.web("red")); 
            }else{
                errorLbl.setText("El usuario se " + aword + " correctamente");
                errorLbl.setTextFill(Color.web("green")); 
                resetFields();
            }
        }
    }
    
    public void resetFields(){
        usernameTxt.setEditable(true);
        firstNameTxt.setText("");
        lastNameTxt.setText("");
        usernameTxt.setText("");
        profileId.setValue("");
        passwordTxt.setText("");
        emailTxt.setText("");
        isActive.setSelected(false);
    }
    
    /*void initData(UserDao customer) {
        firstNameTxt.setText(customer.getFirstName());
        lastNameTxt.setText(customer.getLastName());
    }*/
    
    public void getProfiles(){
        UserDao user = new UserDao(new Image("images/icons8-camera.png"),"","","",0,"","","","","","",new Button());
        ArrayList profiles = user.getProfiles();
        Iterator i = profiles.iterator();

        while(i.hasNext()){
            Map map = (Map) i.next();

            profileId.getItems().add(map.get("profile_name").toString());
        }
    }
    
    public void getUser(int user_id){
        //Map<String, String> user = new HashMap<String, String>();
        isUpdating = true;
        currentUser = user_id;
        try{
            String sql = "SELECT u.username,u.name, u.lastname, u.email, u.profile_id, u.is_active"
                 + " FROM nomina_users u"
                 + " WHERE user_id = '"  + user_id +  "'";

            con = DB.getConnection();
            stmt = con.createStatement();
            rs = null;

            boolean returningRows = stmt.execute(sql);
            if (returningRows)
              rs = stmt.getResultSet();
            
              //return null;
              
            int profile_id = 0;

            while (rs.next()) {
                /*user.put("username",rs.getString("username"));
                user.put("name",rs.getString("name"));
                user.put("lastname",rs.getString("lastname"));
                user.put("email",rs.getString("email"));
                user.put("is_active",Integer.toString(rs.getInt("is_active")));
                user.put("profile_id",Integer.toString(rs.getInt("profile_id")));*/
                firstNameTxt.setText(rs.getString("name"));
                lastNameTxt.setText(rs.getString("lastname"));
                emailTxt.setText(rs.getString("email"));
                usernameTxt.setText(rs.getString("username"));
                
                int is_user_active = rs.getInt("is_active");
                boolean isToggleSelected = (boolean)(is_user_active == 1 ? true : false);
                isActive.setSelected(isToggleSelected);
                
                profile_id = rs.getInt("profile_id");
            }
                        
            profileId.getSelectionModel().select(profile_id - 1); //no es un buen funcionamiento
            usernameTxt.setEditable(false);

        }catch(SQLException ex){

        }

        //return user;
    }
    
}
