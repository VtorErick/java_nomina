/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.employe;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import nominav1.dao.LocalizationDao;
import nominav1.dao.UserDao;
import nominav1.libs.AlertMaker;
import nominav1.libs.Utils;
import ui.nomina.NominaDialogController;

/**
 * FXML Controller class
 *
 * @author dantecervantes
 */
public class EmployeController implements Initializable {
    
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
                          "/ui/employe/EmployeDialog.fxml"
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
                        System.out.print("ERROR AL CARGAR EL ARCHIVO => " + ex.getMessage());
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
                    
                    //eliminar los datos adicionales
                    boolean wasRemovedAllInfo = user.removeAditionalData(user_id);
                    if(wasDeleted && wasRemovedAllInfo){
                        
                        refreshTable(false,"");
                        btnDel.setStyle("-fx-opacity: 0.3;-fx-cursor:pointer;");
                    }
                });

                JFXButton noButton = new JFXButton("No,gracias.");
                noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                    refreshTable(false,"");
                    btnDel.setStyle("-fx-opacity: 0.3;-fx-cursor:pointer;");
                });

                AlertMaker.showMaterialDialog(mainPane, usersTbl, Arrays.asList(yesButton, noButton), "Advertencia", "¿Desea eliminar este empleado permanentemente?");
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
            users = user.findUsers(search,true);
        }else{
            users = user.getEmployes();
        }
        
        Iterator i = users.iterator();
        
        while(i.hasNext()){
            Map map = (Map) i.next();
            
            //Integer user_id = new Integer(((Number) map.get("user_id")).intValue());
            String name = map.get("name").toString();
            String lastname = map.get("lastname").toString();
            String email = map.get("email").toString();
            Integer id = Integer.parseInt(map.get("user_id").toString());
            Integer paymentType = Integer.parseInt(map.get("payment_type").toString());
            String salary = Utils.getCurrencyFormatFloat(Float.parseFloat(map.get("payment_amount").toString()));
            Integer country_id = Integer.parseInt(map.get("country").toString());
            
            LocalizationDao cd = new LocalizationDao(0,"");
            Map<String, String> localization = cd.getCountry(country_id);
            
            String state = map.get("state").toString();
            String jobPosition = map.get("job_position").toString();
            String paymentName = Utils.getPaymentTypes(paymentType);
            
            String encodedBase64 = map.get("user_image").toString();
            //ImageView avatar_wrap = new ImageView("images/icons8-camera.png");
            Image avatar = new Image("images/icons8-camera.png");
            
            if(!encodedBase64.isEmpty()){
                //try{
                    String rocketImgStr = encodedBase64;
                    //BASE64Decoder base64Decoder = new BASE64Decoder();
                    ByteArrayInputStream rocketInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(rocketImgStr));

                    //avatar_wrap = new ImageView(rocketInputStream.toString());
                    avatar = new Image(rocketInputStream);
                    //avatar_wrap.setImage(avatar);
                    //avatar = new Image(rocketInputStream);
                    //avatar_wrap.setImage(avatar);
                    //
                /*}catch(IOException es){
                    es.getMessage();
                    es.printStackTrace();
                }*/
            }
                        
            Button btn = new Button("Generar Nómina");
                        
            tbldata.add(
                new UserDao(avatar,name,lastname,email,id,"",paymentName,salary,jobPosition,localization.get("country_name"),state,btn)
            );
        }
    }
    
    private void getUsersTable(){
        
        JFXTreeTableColumn<UserDao,String> getNomina = new JFXTreeTableColumn<>("Generar Nómina");
        JFXTreeTableColumn<UserDao,Image> imageCol = new JFXTreeTableColumn<>("Avatar");
        JFXTreeTableColumn<UserDao,String> firstNameCol = new JFXTreeTableColumn<>("Nombre");
        JFXTreeTableColumn<UserDao,String> lastNameCol = new JFXTreeTableColumn<>("Apellido");
        JFXTreeTableColumn<UserDao,String> emailCol = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<UserDao,String> paymentTypeCol = new JFXTreeTableColumn<>("Tipo de pago");
        JFXTreeTableColumn<UserDao,String> salaryCol = new JFXTreeTableColumn<>("Salario");
        JFXTreeTableColumn<UserDao,String> countryCol = new JFXTreeTableColumn<>("País");
        JFXTreeTableColumn<UserDao,String> stateCol = new JFXTreeTableColumn<>("Estado");
        JFXTreeTableColumn<UserDao,String> jobPositionCol = new JFXTreeTableColumn<>("Puesto");
        
        /*getNomina.setCellFactory(column -> new JFXTreeTableCell<UserDao,Button>(){
            @Override
            protected void onClick(){}
        });*/
        //getNomina.setCellValueFactory(new PropertyValueFactory<>("button"));
        
       
        //getNomina.setPrefWidth(100);
        Callback<TreeTableColumn<UserDao, String>, TreeTableCell<UserDao, String>> cellFactory
                = //
                new Callback<TreeTableColumn<UserDao, String>, TreeTableCell<UserDao, String>>() {
                    @Override
                    public TreeTableCell call(final TreeTableColumn<UserDao, String> param) {
                        final TreeTableCell<UserDao, String> cell = new TreeTableCell<UserDao, String>() {

                            final JFXButton btn = new JFXButton("Generar Nómina");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setButtonType(JFXButton.ButtonType.RAISED);
                                    btn.setStyle("-fx-background-color:black;-fx-text-fill: white;");
                                    
                                    
                                    
                                    btn.applyCss();
                                    btn.setOnAction(event -> {
                                        //mostrar la nomina en una ventana modal
                                        //enviar la nomina por correo
                                        
                                        
                                        UserDao employe = getTreeTableRow().getItem();
                                        
                                        try{
                                            FXMLLoader loader = new FXMLLoader(
                                                getClass().getResource(
                                                  "/ui/nomina/NominaDialog.fxml"
                                                )
                                              );

                                              Stage _stage = new Stage(StageStyle.DECORATED);
                                              _stage.setScene(
                                                new Scene(
                                                  (Pane) loader.load()
                                                )
                                              );

                                              _stage.setOnHidden(_event -> refreshTable(false,""));

                                              NominaDialogController _controller = 
                                                loader.<NominaDialogController>getController();
                                              _controller.setEmployeData(employe);

                                              _stage.show();
                                            } catch(IOException ex){
                                            }

                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        getNomina.setCellFactory(cellFactory);
      
        imageCol.setCellFactory(column -> new JFXTreeTableCell<UserDao, Image>(){
            private final ImageView imageView;
            {
                imageView = new ImageView();
                imageView.setFitWidth(56);
                imageView.setFitHeight(56);
                setGraphic(imageView);
            }
            
            @Override
            protected void updateItem(Image item, boolean empty) {
                if(!empty){
                    super.updateItem(item, empty);
                    
                    Rectangle clip = new Rectangle(
                        imageView.getFitWidth(), imageView.getFitHeight()
                    );
                    clip.setArcWidth(100);
                    clip.setArcHeight(100);
                    imageView.setClip(clip);
                    
                    imageView.setEffect(new DropShadow(2, Color.BLACK));
                    imageView.setImage(item);
                }
            }

        });
        imageCol.setCellValueFactory(cellData -> cellData.getValue().getValue().image);
        firstNameCol.setCellValueFactory(param -> param.getValue().getValue().firstName);      
        lastNameCol.setCellValueFactory(param -> param.getValue().getValue().lastName);
        emailCol.setCellValueFactory(param -> param.getValue().getValue().email);
        paymentTypeCol.setCellValueFactory(param -> param.getValue().getValue().paymentType);
        salaryCol.setCellValueFactory(param -> param.getValue().getValue().salary);
        countryCol.setCellValueFactory(param -> param.getValue().getValue().country);
        stateCol.setCellValueFactory(param -> param.getValue().getValue().state);
        jobPositionCol.setCellValueFactory(param -> param.getValue().getValue().jobPosition);

        final TreeItem<UserDao> root = new RecursiveTreeItem<UserDao>(tbldata, RecursiveTreeObject::getChildren);
        
        usersTbl.getColumns().addAll(getNomina, imageCol, firstNameCol, lastNameCol, emailCol, paymentTypeCol, salaryCol,countryCol,stateCol,jobPositionCol);
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
                          "/ui/employe/EmployeDialog.fxml"
                        )
                      );

                      Stage stage = new Stage(StageStyle.DECORATED);
                      stage.setScene(
                        new Scene(
                          (Pane) loader.load()
                        )
                      );
                      
                      stage.setOnHidden(event -> refreshTable(false,""));
                      
                      EmployeDialogController controller = 
                        loader.<EmployeDialogController>getController();
                      controller.setEmployeData(user_id);

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
