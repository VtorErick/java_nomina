/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.dashboard;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import nominav1.libs.Utils;
import nominav1.libs.AlertMaker;

/**
 * FXML Controller class
 *
 * @author dantecervantes
 */

//https://github.com/afsalashyana/Library-Assistant
public class DashboardController implements Initializable {
    
    @FXML private StackPane rootPane;
    @FXML private Pane mainPane;
    @FXML private ImageView usersImg = new ImageView();
    @FXML private ImageView logoutImg = new ImageView();
    @FXML private ImageView employeImg = new ImageView();
    @FXML private ImageView infoImg = new ImageView();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //users sections
        usersImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    Pane usersSection = FXMLLoader.load(getClass().getResource("/ui/users/Users.fxml"));
                    mainPane.getChildren().setAll(usersSection);
                    event.consume();
                } catch(IOException err){
                    System.out.print("No se pudo cargar el archivo => " + err.getMessage());
                }
            }
       });
        
        //info
        employeImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    Pane usersSection = FXMLLoader.load(getClass().getResource("/ui/employe/Employe.fxml"));
                    mainPane.getChildren().setAll(usersSection);
                    event.consume();
                } catch(IOException err){
                    System.out.print("No se pudo cargar el archivo => " + err.getMessage());
                }
            }
       });
        
        //employes
        infoImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Utils.loadWindow(getClass().getResource("/ui/info/InfoDialog.fxml"), "Información", null);
            }
       });
        
        //logout
        logoutImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
            JFXButton yesButton = new JFXButton("Si.");
            yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                 System.exit(0);
            });
            
            JFXButton noButton = new JFXButton("No,gracias.");
            noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            
            });
                
                AlertMaker.showMaterialDialog(rootPane, mainPane, Arrays.asList(yesButton, noButton), "Cerrar Sesión","¿Deseas cerrar sesión?");
            }
       });
    }
    
}
