/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.employe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import nominav1.dao.UserDao;
import nominav1.dao.DeductionDao;
import nominav1.dao.LocalizationDao;
import nominav1.dao.PaymenTypeDao;
import nominav1.dao.BankDao;
import nominav1.libs.Utils;
import nominav1.libs.AlertMaker;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.controlsfx.control.CheckComboBox;
import sun.misc.BASE64Decoder;

/**
 * FXML Controller class
 *
 * @author dantecervantes
 */
public class EmployeDialogController implements Initializable {

    private boolean isUpdating = false;
    private int currentUser = 0;
    private String encodedBase64 = "";
    
    @FXML private StackPane mainPane;
    @FXML private AnchorPane mainContainer;
    @FXML private TextField firstNameTxt;
    @FXML private TextField lastNameTxt;
    @FXML private TextField emailTxt;
    @FXML private TextField stateTxt;
    @FXML private PasswordField passwordTxt;
    @FXML private TextField paymentTxt;
    @FXML private TextField curpTxt;
    @FXML private TextField rfcTxt;
    @FXML private TextField addressTxt;
    @FXML private TextField cpTxt;
    @FXML private DatePicker birthdateTxt;
    @FXML private TextField jobPositionTxt;
    @FXML private TextField clabeTxt;
    @FXML private ImageView employeImg = new ImageView();
    @FXML private ComboBox<BankDao> bankCmb = new ComboBox<>();
    @FXML private ComboBox<PaymenTypeDao> paymentType = new ComboBox();
    @FXML private ComboBox<LocalizationDao> countryCmb = new ComboBox();
    @FXML private ToggleButton isActive;
    @FXML private Label errorLbl;
    @FXML private CheckComboBox<DeductionDao> cmbDed = new CheckComboBox<>();
    final ObservableList<DeductionDao> strings = FXCollections.observableArrayList();
    final ObservableList<LocalizationDao> _countries = FXCollections.observableArrayList();
    final ObservableList<PaymenTypeDao> payments = FXCollections.observableArrayList();
    final ObservableList<BankDao> bankList = FXCollections.observableArrayList();
    private int selectedBank;
    int[] _selectedDeductions; // whaaaaaat? The length of an array is immutable in java.
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetFields();
        
        //bancos
        BankDao bdao = new BankDao(0,"");
        ArrayList banks = bdao.getAll();
        Iterator ibanks = banks.iterator();
        
        while(ibanks.hasNext()){
            Map map = (Map) ibanks.next();
            
            String bankName = map.get("bank_name").toString();
            int bankId = Integer.parseInt(map.get("bank_id").toString());
            
            bankList.add(new BankDao(bankId,bankName));
        }
        bankCmb.setItems(bankList);
        
        //int bankdid = bankCmb.getSelectionModel().getSelectedItem().getId();
        
        bankCmb.setConverter(new StringConverter<BankDao>() {

            @Override
            public String toString(BankDao object) {
                return object.getName();
            }

            @Override
            public BankDao fromString(String string) {
                return bankCmb.getItems().stream().filter(ap -> 
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
        
       
        bankCmb.valueProperty().addListener((ob,obViejo,obNuevo) -> {
            selectedBank = obNuevo.getId();
        });
       
        //paises
        LocalizationDao ldao = new LocalizationDao(0,"");
        ArrayList countries = ldao.getCountries();
        Iterator icountries = countries.iterator();
        
        while(icountries.hasNext()){
            Map map = (Map) icountries.next();
            
            int country_id = Integer.parseInt(map.get("country_id").toString());
            String countryName = map.get("country_name").toString();
            _countries.add(new LocalizationDao(country_id,countryName));
        }
        
        countryCmb.setItems(_countries);
        
        countryCmb.setConverter(new StringConverter<LocalizationDao>() {

            @Override
            public String toString(LocalizationDao object) {
                return object.getName();
            }

            @Override
            public LocalizationDao fromString(String string) {
                return countryCmb.getItems().stream().filter(ap -> 
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
        
        
        //tipo de pago
        payments.addAll(
            new PaymenTypeDao(1,"Semanal"),
            new PaymenTypeDao(2,"Catorcenal"),
            new PaymenTypeDao(3,"Quincenal"),
            new PaymenTypeDao(4,"Mensual"),
            new PaymenTypeDao(5,"Por horas"),
            new PaymenTypeDao(6,"Diario"),
            new PaymenTypeDao(7,"Otro")
        );
        
        paymentType.setItems(payments);
        
        
        paymentType.setConverter(new StringConverter<PaymenTypeDao>() {

            @Override
            public String toString(PaymenTypeDao object) {
                return object.getName();
            }

            @Override
            public PaymenTypeDao fromString(String string) {
                return paymentType.getItems().stream().filter(ap -> 
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
        
        
        DeductionDao dedDao = new DeductionDao(0,"");
        ArrayList deducciones = dedDao.getAll();
        Iterator i = deducciones.iterator();
        
        //https://bitbucket.org/controlsfx/controlsfx/issues/883/how-to-add-selected-items-from-database
        while(i.hasNext()){
            Map map = (Map) i.next();
            
            int deductionId = Integer.parseInt(map.get("deduction_id").toString());
            String deductionName = map.get("deduction_name").toString();
            strings.add(new DeductionDao(deductionId, deductionName));
        }

        cmbDed.getItems().addAll(strings);
        
        
        cmbDed.setConverter(new StringConverter<DeductionDao>() {
            @Override
            public String toString(DeductionDao object) {
                return object.getName();
            }

            @Override
            public DeductionDao fromString(String string) {
                return cmbDed.getItems().stream().filter(ap -> 
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
        
        cmbDed.getCheckModel().getCheckedItems().addListener(new ListChangeListener<DeductionDao>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DeductionDao> c) {         
                ObservableList<DeductionDao> items = cmbDed.getCheckModel().getCheckedItems();
                //maximo numero de deducciones que puede tener un usuario? es depende del numero 
                //de registros que hay en el sistema
                //System.out.print("Seleccionaste => " + );
                //DeductionDao ddao = new DeductionDao(0,"");
                //int maximum = ddao.getCount();
                int i = 0;
                _selectedDeductions = new int[items.size()];
                for(DeductionDao deduction : items) {
                    //System.out.print(deduction.getId());
                    _selectedDeductions[i] = deduction.getId();
                    i++;
                }
                //System.out.print(selectedDeductions);
            }
        });
        
        //inputs de tipo numerico
        cpTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    cpTxt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        clabeTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    clabeTxt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        paymentTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    paymentTxt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        //employe image selector
        employeImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                encodedBase64 = "";
                FileChooser fileChooser = new FileChooser();
                //obtener el window por los componentes
                Window wind = mainContainer.getScene().getWindow();
                File file = fileChooser.showOpenDialog(wind);
                if (file != null) {
                    //open(file.toURI().toString());
                    //obtener especificaciones de la base de datos? :o
                    Image image = new Image(file.toURI().toString(),150, 150, false, false);
                    employeImg.setImage(image);
                    
                    File originalFile = new File(file.getAbsoluteFile().toString());
                    try {
                        FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
                        byte[] bytes = new byte[(int)originalFile.length()];
                        fileInputStreamReader.read(bytes);
                        
                        encodedBase64 = new String(Base64.encodeBase64String(bytes));
                        
                        //mostrar imagen en base64 desde la bd
                        //https://stackoverflow.com/questions/11537434/javafx-embedding-encoded-image-in-fxml-file
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   
                }
            }
                
        });
    }
    
    public void setEmployeData(int employe_id){
        UserDao employe = new UserDao(new Image("images/icons8-camera.png"),"","","",0,"","","","","","",new Button());
        Map<String,String> employeData = employe.getEmploye(employe_id);
        
        isUpdating = true;
        currentUser = employe_id;
                        
        firstNameTxt.setText(employeData.get("firstname"));
        lastNameTxt.setText(employeData.get("lastname"));
        emailTxt.setText(employeData.get("email"));
        stateTxt.setText(employeData.get("state"));
        
        //user image
        if(!employeData.get("image").isEmpty()){
            encodedBase64 = employeData.get("image");
            try{
                String rocketImgStr = employeData.get("image");
                BASE64Decoder base64Decoder = new BASE64Decoder();
                ByteArrayInputStream rocketInputStream = new ByteArrayInputStream(base64Decoder.decodeBuffer(rocketImgStr));
                
                Image rocketImg = new Image(rocketInputStream,150,150,false,false);
                employeImg.setImage(rocketImg);
                //
            }catch(IOException es){}
        }
        
        int country_id = Integer.parseInt(employeData.get("country_id"));
        
        Predicate<LocalizationDao> cmatcher = data1 -> (data1.getId() == country_id ? true : false );
        Optional<LocalizationDao> copt = countryCmb.getItems().stream().filter(cmatcher).findAny();
        countryCmb.setValue(copt.get());
        
        paymentTxt.setText(employeData.get("salary"));
        
        int _paymentType = Integer.parseInt(employeData.get("paymentType"));
        
        Predicate<PaymenTypeDao> ptmatcher = data1 -> (data1.getId() == _paymentType ? true : false );
        Optional<PaymenTypeDao> ptopt = paymentType.getItems().stream().filter(ptmatcher).findAny();
        paymentType.setValue(ptopt.get());
        
        curpTxt.setText(employeData.get("curp"));
        rfcTxt.setText(employeData.get("rfc"));
        addressTxt.setText(employeData.get("address"));
        birthdateTxt.getEditor().setText(employeData.get("birthdate"));
        cpTxt.setText(employeData.get("cp"));
        jobPositionTxt.setText(employeData.get("job_position"));
        
        clabeTxt.setText(employeData.get("clabe"));
        int bank_id = Integer.parseInt(employeData.get("bank_id"));
        
        //basado en el id
        Predicate<BankDao> bankmatcher = data1 -> (data1.getId() == bank_id ? true : false );
        Optional<BankDao> bankopt = bankCmb.getItems().stream().filter(bankmatcher).findAny();
        bankCmb.setValue(bankopt.get());
       
        
        int is_active = Integer.parseInt(employeData.get("is_active"));
        if(is_active == 1){
            isActive.setSelected(true);
        }else{
            isActive.setSelected(false);
        }
        
        ArrayList deductions = employe.getDeductions(employe_id);
        Iterator i = deductions.iterator();
        
        while(i.hasNext()){
            Map data = (Map) i.next();
            
            ObservableList<DeductionDao> items = cmbDed.getItems();
            for(DeductionDao item : items){
                int selected_deduction = Integer.parseInt(data.get("deduction_id").toString());
                
                if(selected_deduction == item.getId()){
                    cmbDed.getCheckModel().check(item);
                }
            }
        }
                
    }
    
    @FXML
    private void handleSaveEmploye(ActionEvent event) throws IOException {
        boolean canGo = true;
        String firstName = firstNameTxt.getText();
        String lastName = lastNameTxt.getText();
        String email = emailTxt.getText();
        String state = stateTxt.getText();
        int selectedCountry = countryCmb.getSelectionModel().getSelectedIndex() + 1;
        String paymentAmount = paymentTxt.getText();
        int selectedPaymentType = paymentType.getSelectionModel().getSelectedIndex() + 1;
        String password = passwordTxt.getText();
        String curp = curpTxt.getText();
        String rfc = rfcTxt.getText();
        String address = addressTxt.getText();
        String birthdate = birthdateTxt.getEditor().getText();
        String currentDate = "";
        String cp = cpTxt.getText();
        String jobPosition = jobPositionTxt.getText();
        String clabe = clabeTxt.getText();
        boolean _isActive = isActive.isSelected();
        
        ObservableList selectedDeductions = cmbDed.getCheckModel().getCheckedItems();
        
        int userActive = _isActive ? 1 : 0;
        
        //validaciones
        if(firstName == null || firstName.isEmpty()){
            errorLbl.setText("El nombre del empleado es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(lastName == null || lastName.isEmpty()){
            errorLbl.setText("El apellido del empleado es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(email == null || email.isEmpty()){
            errorLbl.setText("El email del empleado es obligatorio.");
            canGo = false;
        }else{
            if(!Utils.validateEmailAddress(email)){
                errorLbl.setText("El email no es correcto.");
                errorLbl.setTextFill(Color.web("red")); 
                canGo = false;
            }
        }
        
        if(state == null || state.isEmpty()){
            errorLbl.setText("El estado es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(cp == null || cp.isEmpty()){
            errorLbl.setText("El código postal es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(curp == null || curp.isEmpty()){
            errorLbl.setText("El CURP es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }else if (!Utils.validarCurp(curp)){
            errorLbl.setText("El CURP no es válido.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(rfc == null || rfc.isEmpty()){
            errorLbl.setText("El RFC es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }else if(!Utils.validarRFC(rfc)){
            errorLbl.setText("El RFC no es válido.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(address == null || address.isEmpty()){
            errorLbl.setText("La dirección es obligatoria.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        } else if (address.length() <= 32){
            errorLbl.setText("La dirección debe contener al menos 32 caracteres.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(birthdate == null || birthdate.isEmpty()){
            errorLbl.setText("La fecha de nacimiento es obligatoria.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }else{
            String[] dateParts = birthdate.split("/");

            currentDate = dateParts[2];
            currentDate += "-" + dateParts[1];
            currentDate += "-" + dateParts[0];
        }
        
        if(clabe == null || clabe.isEmpty()){
            errorLbl.setText("La CLABE del banco es obligatoria.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(jobPosition == null || jobPosition.isEmpty()){
            errorLbl.setText("El puesto es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(selectedCountry == -1){
            errorLbl.setText("El país es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(selectedBank == -1){
            errorLbl.setText("El banco es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(selectedPaymentType == -1){
            errorLbl.setText("El método de pago es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(paymentAmount == null || paymentAmount.isEmpty()){
            errorLbl.setText("El salario es obligatorio.");
            errorLbl.setTextFill(Color.web("red")); 
            canGo = false;
        }
        
        if(!isUpdating){
            if(password == null || password.isEmpty()){
                errorLbl.setText("La contraseña es obligatoria.");
                canGo = false;
            }
        }
        
        if(canGo){
            UserDao employe = new UserDao(new Image("images/icons8-camera.png"),"","","",0,"","","","","","",new Button());

            if(!isUpdating){
                //insertar empleado
                int inserted_user_id = employe.addNewEmploye(
                        firstName,
                        lastName,
                        email,
                        password,
                        userActive
                );

                if(inserted_user_id > 0){
                    //se insertó correctamente, insertar la información relacional

                    //

                    boolean insertedData = employe.addEmployeData(
                        inserted_user_id,
                        state,
                        selectedCountry,
                        selectedBank,
                        clabe,
                        selectedPaymentType,
                        paymentAmount,
                        curp,
                        rfc,
                        address,
                        currentDate,
                        cp,
                        jobPosition,
                        encodedBase64
                    );

                    //eliminar las deducciones del empleado
                    employe.removeEmployeDeductions(inserted_user_id);

                    for(int i = 0; i < _selectedDeductions.length; i++){
                        //System.out.print("Seleccionado " + inserted_user_id + " => " + _selectedDeductions[i]);
                        employe.addEmployeDeduction(inserted_user_id,_selectedDeductions[i]);
                    }

                    AlertMaker.showMaterialDialog(mainPane, mainContainer, new ArrayList<>() , "Wow!", "El empleado se creó con éxito.");

                    resetFields();
                }   
            
            }else{
                //actualizar info del usuario
                boolean wasUpdated = employe.update(
                    "",
                    firstName,
                    lastName,
                    email,
                    password,
                    userActive,
                    3, //siempre es empleado
                    currentUser
                );
                
                if(wasUpdated){
                    boolean wasUpdatedEmployeData = employe.updateEmployeData(
                        currentUser,
                        state,
                        selectedCountry,
                        selectedBank,
                        clabe,
                        selectedPaymentType,
                        paymentAmount,
                        curp,
                        rfc,
                        address,
                        currentDate,
                        cp,
                        jobPosition,
                        encodedBase64
                    );
                    
                    if(wasUpdatedEmployeData){
                        //eliminar las deducciones del empleado
                        employe.removeEmployeDeductions(currentUser);

                        for(int i = 0; i < _selectedDeductions.length; i++){
                            //System.out.print("Seleccionado " + inserted_user_id + " => " + _selectedDeductions[i]);
                            employe.addEmployeDeduction(currentUser,_selectedDeductions[i]);
                        }

                        /*errorLbl.setText("El empleado se creó correctamente.");
                        errorLbl.setTextFill(Color.web("green"));*/
                        AlertMaker.showMaterialDialog(mainPane, mainContainer, new ArrayList<>() , "Wow!", "El empleado se actualizó con éxito.");
                    }
                }
            }
        }
    }
    
    public void resetFields(){
        encodedBase64 = "";
        Image image = new Image("images/icons8-camera.png");
        employeImg.setImage(image);
        firstNameTxt.setText("");
        lastNameTxt.setText("");
        emailTxt.setText("");
        stateTxt.setText("");
        countryCmb.getSelectionModel().clearSelection();
        paymentTxt.setText("");
        paymentType.getSelectionModel().clearSelection();
        passwordTxt.setText("");
        curpTxt.setText("");
        rfcTxt.setText("");
        addressTxt.setText("");
        birthdateTxt.getEditor().setText("");
        cpTxt.setText("");
        bankCmb.getSelectionModel().clearSelection();
        jobPositionTxt.setText("");
        clabeTxt.setText("");
        isActive.setSelected(false);
        
        cmbDed.getCheckModel().clearChecks();
        
        DeductionDao d = new DeductionDao(0,"");
        _selectedDeductions = new int[d.getCount()];
    }
    
}
