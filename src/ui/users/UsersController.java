/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.users;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import nominav1.dao.UserDao;
import nominav1.libs.AlertMaker;
import nominav1.libs.Utils;

/**
 * FXML Controller class
 *
 * @author dantecervantes
 */
public class UsersController implements Initializable {
    
    @FXML private StackPane mainPane;
    @FXML private JFXTreeTableView<UserDao> usersTbl;
    @FXML private ImageView btnAdd = new ImageView();
    @FXML private ImageView btnDel = new ImageView();
    @FXML private ImageView btnSearch = new ImageView();
    @FXML private ImageView btnRefresh = new ImageView();
    @FXML private TextField txtSearch = new TextField();
    
    ObservableList<UserDao> tbldata = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        refreshTable(false,"");
        getUsersTable();
        
        txtSearch.setStyle("-fx-text-inner-color: white;");
        
        //refresh table
        btnRefresh.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshTable(false,"");
            }
        });
        
        //input de busqueda
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            Integer newValueCounter = newValue.length();
            if(newValueCounter > 3){
                btnSearch.setStyle("-fx-opacity: 1;-fx-cursor:pointer;");
                
                //habilitar clic del botón
                btnSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //realizar busqueda
                        refreshTable(true,newValue);
                    }
               });
            }else{
                btnSearch.setStyle("-fx-opacity: 0.3;");
            }
        });
        
        //add more
        btnAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                          "/ui/users/UserDialog.fxml"
                        )
                      );

                      Stage stage = new Stage(StageStyle.DECORATED);
                      stage.setScene(
                        new Scene(
                          (Pane) loader.load()
                        )
                      );
                      
                      stage.setOnHidden(e -> {
                          refreshTable(false,"");
                       });

                      stage.show();
                    } catch(IOException ex){
                    }
            }
        });
        
        //habilitar clic del botón
        btnDel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int index = usersTbl.getSelectionModel().getFocusedIndex();
                UserDao user = tbldata.get(index);

                int user_id = user.getId();


                JFXButton yesButton = new JFXButton("Si.");
                yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                    boolean wasDeleted = false;
                    wasDeleted = user.remove(user_id);

                    if(wasDeleted){
                        refreshTable(false,"");
                        btnDel.setStyle("-fx-opacity: 0.3;-fx-cursor:pointer;");
                    }
                });

                JFXButton noButton = new JFXButton("No,gracias.");
                noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                    refreshTable(false,"");
                    btnDel.setStyle("-fx-opacity: 0.3;-fx-cursor:pointer;");
                });

                AlertMaker.showMaterialDialog(mainPane, usersTbl, Arrays.asList(yesButton, noButton), "Advertencia", "¿Desea eliminar este usuario permanentemente?");
            }
       });
        
    }
    
    public void refreshTable(boolean isSearching, String search){
        btnDel.setStyle("-fx-opacity: 0.3;-fx-cursor:pointer;");
        txtSearch.setText("");
        tbldata.clear();
        UserDao user = new UserDao(new Image("images/icons8-camera.png"),"","","",0,"","","","","","",new Button());
        
        ArrayList users = null;
        
        if(isSearching){
            users = user.findUsers(search,false);
        }else{
            users = user.getUsers();
        }
        
        Iterator i = users.iterator();
        
        while(i.hasNext()){
            Map map = (Map) i.next();
            
            //Integer user_id = new Integer(((Number) map.get("user_id")).intValue());
            String name = map.get("name").toString();
            String lastname = map.get("lastname").toString();
            String email = map.get("email").toString();
            Integer id = Integer.parseInt(map.get("user_id").toString());
            String profile = map.get("profile_name").toString();
            
            tbldata.add(
                new UserDao(new Image("images/icons8-camera.png"),name,lastname,email,id,profile,"","","","","",new Button())
            );
        }
    }
    
    private void getUsersTable(){
        
        JFXTreeTableColumn<UserDao,String> firstNameCol = new JFXTreeTableColumn<>("Nombre");
        JFXTreeTableColumn<UserDao,String> lastNameCol = new JFXTreeTableColumn<>("Apellido");
        JFXTreeTableColumn<UserDao,String> emailCol = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<UserDao,String> profileCol = new JFXTreeTableColumn<>("Perfíl");
      
        firstNameCol.setCellValueFactory(param -> param.getValue().getValue().firstName);      
        lastNameCol.setCellValueFactory(param -> param.getValue().getValue().lastName);
        emailCol.setCellValueFactory(param -> param.getValue().getValue().email);
        profileCol.setCellValueFactory(param -> param.getValue().getValue().profile);

        final TreeItem<UserDao> root = new RecursiveTreeItem<UserDao>(tbldata, RecursiveTreeObject::getChildren);
        
        usersTbl.getColumns().addAll(firstNameCol, lastNameCol, emailCol, profileCol);
        usersTbl.setRoot(root);
        usersTbl.setShowRoot(false);
        
        //doble clic en elemento de la tabla
        usersTbl.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            { 
                
                if(mouseEvent.getClickCount() == 1){
                    removeUser();
                }
                
                if(mouseEvent.getClickCount() == 2)
                {
                    int index = usersTbl.getSelectionModel().getFocusedIndex();
                    UserDao user = tbldata.get(index);
                    
                    int user_id = user.getId();
                   
                    try{
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                          "/ui/users/UserDialog.fxml"
                        )
                      );

                      Stage stage = new Stage(StageStyle.DECORATED);
                      stage.setScene(
                        new Scene(
                          (Pane) loader.load()
                        )
                      );
                      
                      stage.setOnHidden(event -> refreshTable(false,""));
                      
                      UserDialogController controller = 
                        loader.<UserDialogController>getController();
                      controller.getUser(user_id);

                      stage.show();
                    } catch(IOException ex){
                    }
                   
                }
            }
        });
    }
    
    public void removeUser(){
        btnDel.setStyle("-fx-opacity: 1;-fx-cursor:pointer;");
    }
    
}
