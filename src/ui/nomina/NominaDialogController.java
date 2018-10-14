/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.nomina;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import nominav1.dao.NominaDao;
import nominav1.dao.UserDao;
import nominav1.libs.AlertMaker;
import nominav1.libs.Utils;

/**
 * FXML Controller class
 *
 * @author dantecervantes
 */
public class NominaDialogController implements Initializable {
    
    @FXML StackPane mainPane;
    @FXML AnchorPane anchorPane;
    @FXML TextField txtNameEmploye = new TextField();
    @FXML TextField retardosTxt = new TextField();
    @FXML TextField inasistenciasTxt = new TextField();
    @FXML ScrollPane scrollPane = new ScrollPane();
    private UserDao _employe;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        retardosTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    retardosTxt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        inasistenciasTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    inasistenciasTxt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
    }    
    
    public void setEmployeData(UserDao employe){
        if(employe.getId() > 0){
            
            _employe = employe;
            
            txtNameEmploye.setText(employe.getFirstName() + " " + employe.getLastName());
            txtNameEmploye.setDisable(true);
            
        }else{
            AlertMaker.showMaterialDialog(mainPane, anchorPane, new ArrayList<>(), "Error", "No se pudo acceder al empleado.");
        }
    }
    
    @FXML
    public void handleGetNomina(ActionEvent event){
                
        String retardos = retardosTxt.getText();
        String inasistencias = inasistenciasTxt.getText();
        
        if(retardos.isEmpty()){
            retardos = "0";
        }
        
        if(inasistencias.isEmpty()){
            inasistencias = "0";
        }
        
        NominaDao nomi = new NominaDao();
        nomi.setValues(retardos, inasistencias);
        
        FlowPane _pane = new FlowPane();
        _pane.setPrefSize(222.0, Double.MAX_VALUE);
        
        ArrayList deductions = _employe.getDeductions(_employe.getId());
        Iterator dedi = deductions.iterator();

        String salary = _employe.getSalary().replace(",", "").replace("$", "");
        double employeSalary = Double.parseDouble(salary);
        float employeFloatSalary = (float)employeSalary;
        
        double totalSalary = employeSalary;
        
        //salario
        Label _salary = new Label();
        _salary.setText("SALARIO : " + Utils.getCurrencyFormatFloat(employeFloatSalary));
        _salary.setMaxWidth(222.0);

        _pane.getChildren().add(_salary);
        
        //br
        Region _p = new Region();
        _p.setPrefSize(222.0, 20.0);

        _pane.getChildren().add(_p);
        
         //salario
        Label _deductionsLbl = new Label();
        _deductionsLbl.setText("DEDUCCIONES");
        _deductionsLbl.setMaxWidth(222.0);

        _pane.getChildren().add(_deductionsLbl);
        
        //br
        Region _p_ = new Region();
        _p_.setPrefSize(222.0, 20.0);

        _pane.getChildren().add(_p_);
        
        

        while(dedi.hasNext()){
            Map data = (Map) dedi.next();

            String dedName = data.get("deduction_name").toString().toUpperCase();
            //1 = %, 0 = $
            int dedType = Integer.parseInt(data.get("deduction_type").toString());
            double dedAmount = Double.parseDouble(data.get("deduction_price").toString());

            double dedValue = 0.0;

            if(dedType == 1){
                dedValue = employeSalary * dedAmount;
            }else{
                dedValue = employeSalary - dedAmount;
            }
            
            totalSalary = totalSalary - dedValue;

            //br
            Region p = new Region();
            p.setPrefSize(222.0, 0.0);

            _pane.getChildren().add(p);

            Label dedlbl = new Label();
            dedlbl.setText(dedName + " : $ " + dedValue);
            dedlbl.setMaxWidth(222.0);
            //System.out.print(dedName + " : " + dedValue);
            //scrollPane.getChildrenUnmodifiable().add(dedlbl);

            _pane.getChildren().add(dedlbl);
        }
        
        //br
        Region pa = new Region();
        pa.setPrefSize(222.0, 30.0);
        
        _pane.getChildren().add(pa);
        
        //total de salario
        //dias trabajados - faltas
        double faltas = 10 - nomi.getFaltas();
        float total = (float)((totalSalary / 10) * faltas);
        
        Label totallbl = new Label();
        totallbl.setText("SALARIO TOTAL : " + Utils.getCurrencyFormatFloat(total));
        totallbl.setMaxWidth(222.0);
        
        _pane.getChildren().add(totallbl);

        scrollPane.setContent(_pane);
    } 
    
}
