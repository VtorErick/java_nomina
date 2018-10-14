/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.login;

//estoy usando java 10, pero configure la pc para que utilice java 8 y sea compatible con jfoenix
//https://stackoverflow.com/questions/46513639/how-to-downgrade-java-from-9-to-8-on-a-macos-eclipse-is-not-running-with-java-9

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import org.apache.commons.codec.digest.DigestUtils;

import nominav1.dao.UserDao;
import nominav1.libs.Utils;

/**
 *
 * @author dantecervantes
 */
public class LoginController implements Initializable {
    
    
    @FXML private JFXTextField usernameTxt = new JFXTextField();
    @FXML private JFXPasswordField passwordTxt = new JFXPasswordField();
    @FXML private Button btnLogin;
    @FXML private Label lblError = new Label("");
    
    @FXML
    private void handleButtonLogin(ActionEvent event) throws IOException {
        String username = usernameTxt.getText();
        String password = DigestUtils.shaHex(passwordTxt.getText());
                
        if(username.isEmpty() || password.isEmpty()){
            //usuario vacio
            lblError.setText("El usuario / contraseña es requerido");
            lblError.setTextFill(Color.web("red"));
        }else{
            //todo ok
            lblError.setText("");
            
            UserDao user = new UserDao(new Image("images/icons8-camera.png"),"","","",0,"","","","","","",new Button());
            int canLogin = user.login(username, password);
            
            if(canLogin > 0){
                closeStage();
                Utils.loadWindow(getClass().getResource("/ui/dashboard/Dashboard.fxml"), "Dashboard", null);
            }else{
                lblError.setText("Usuario no encontrado");
                lblError.setTextFill(Color.web("red"));
            }
            
        }
    } 
    
    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage) usernameTxt.getScene().getWindow()).close();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

