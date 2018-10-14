/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nominav1.libs;

import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author dantecervantes
 */
public class Utils {
    
    public static final String ICON_IMAGE_LOC = "/resources/icon.png";
    final static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    public final static String REGEX_CURP = 
        "[A-Z][A,E,I,O,U,X][A-Z]{2}[0-9]{2}[0-1][0-9][0-3][0-9][M,H][A-Z]{2}[B,C,D,F,G,H,J,K,L,M,N,Ñ,P,Q,R,S,T,V,W,X,Y,Z]{3}[0-9,A-Z][0-9]";
    public final static String REGEX_RFC = "^([A-ZÑ\\x26]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])([A-Z]|[0-9]){2}([A]|[0-9]){1})?$";
    
    final static java.util.Random rand = new java.util.Random();

    // consider using a Map<String,Boolean> to say whether the identifier is being used or not 
    final static Set<String> identifiers = new HashSet<String>();

    
    public static Object loadWindow(URL loc, String title, Stage parentStage) {
        Object controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage = null;
            if (parentStage != null) {
                stage = parentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            //setStageIcon(stage);
        } catch (IOException ex) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return controller;
    }
    
    public static boolean validateEmailAddress(String emailID) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(emailID).matches();
    }
    
    //se hace estático por ahora
    public static String getPaymentTypes(int payment_id){
        String paymentName = "";
        
        switch(payment_id){
            case 1:
                paymentName = "Semanal";
            break;
            
            case 2:
                paymentName = "Catorcernal";
            break;
            
            case 3:
                paymentName = "Quincenal";
            break;
            
            case 4:
                paymentName = "Mensual";
            break;
            
            case 5:
                paymentName = "Por horas";
            break;
        }
        
        return paymentName;
    }
    
    public static String getCurrencyFormat(int v){
        /*String toReturn = "";
        String s =  String.valueOf(v);
        int length = s.length();
        for(int i = length; i >0 ; --i){
            toReturn += s.charAt(i - 1);
            if((i - length - 1) % 3 == 0 && i != 1) toReturn += ',';
        }
        return "$" + new StringBuilder(toReturn).reverse().toString();*/
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(v);
        return moneyString;
    }
    
    public static String getCurrencyFormatFloat(float v){        
         NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(v);
        return moneyString;
    }
    
    public static String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+5;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
    
    public static boolean validarRFC(String rfc){
        boolean rfcValid = false;

        Pattern pattern = Pattern.compile(REGEX_RFC);
        Matcher matcher = pattern.matcher(rfc);
        rfcValid = matcher.find();

        matcher = null;
        pattern = null;

        return rfcValid;
    }
    
    public static boolean validarCurp(String textoCurp){
        boolean curpValido = false;

        Pattern pattern = Pattern.compile(REGEX_CURP);
        Matcher matcher = pattern.matcher(textoCurp);
        curpValido = matcher.find();

        matcher = null;
        pattern = null;

        return curpValido;
    }
    
    public static String formatDate(String date){
        String[] separate = date.split("-");
        
        String year = separate[0];
        String month = separate[1];
        String day = separate[2];
        
        return day + "/" + month + "/" + year;
    }
}
