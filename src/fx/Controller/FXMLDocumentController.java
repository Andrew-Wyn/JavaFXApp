package fx.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.collections.*;
import javafx.scene.control.cell.*;
import javafx.stage.*;
import javafx.util.*;
import javax.swing.WindowConstants;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Pane sx_anc;
    @FXML
    private JFXHamburger hamb;
    @FXML
    private JFXButton btn_add_key;
    @FXML
    private TextField txt_key;
    @FXML
    private JFXTabPane tab;
    @FXML
    private AnchorPane dx_anc;
    @FXML
    private JFXButton btn_fileChooser;
    @FXML
    private Label txt_path;
    @FXML
    private Label txt_comptime;
    @FXML
    private TextArea txt_out = new TextArea("Nessun File Selezionato ...");
    @FXML
    private JFXSpinner loader;
    @FXML
    private JFXNodesList drop; 
    @FXML
    private MenuItem selectedKey;
    private Window stage;
    
    //variabili d'ambiente
    boolean opened = true;
    HamburgerSlideCloseTransition burgerTask;
    TableView tableKeys = new TableView();
    //apro gli stram di input  ** file ----> programma **
    FileReader q = null;
    BufferedReader fIN = null;
    //apro gli stream di output ** programma ----> file ** 
    FileWriter f = null;
    PrintWriter fOUT = null;
    private ObservableList<KeyRow> keycollection = FXCollections.observableArrayList();
    File file = null;
    File fileTxt = null;
    FileChooser fileChooser = new FileChooser();
    //!Importante!
    String key = "";
    byte[] data = null;
    ThComp th = new ThComp(); //thread per la computazione dell'algoritmo
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //prova node list 
        /*JFXButton btn_node1 = new JFXButton("Drop Me !");
        btn_node1.setButtonType(JFXButton.ButtonType.RAISED);
        JFXButton btn_node2 = new JFXButton("File");
        btn_node2.setButtonType(JFXButton.ButtonType.RAISED);
        JFXButton btn_node3 = new JFXButton("Selected Key");
        btn_node3.setButtonType(JFXButton.ButtonType.RAISED);
        drop.addAnimatedNode(btn_node1);
        drop.addAnimatedNode(btn_node2);
        drop.addAnimatedNode(btn_node3);*/
        //loader.setVisible(false);
        txt_out.setWrapText(true);
        txt_out.setText("Nessun File Selezionato ...");
        initTabView();
        startkeys();
        /*set event listener on row for double click*/
        tableKeys.setRowFactory( tv -> {
            TableRow<KeyRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    KeyRow rowData = row.getItem();
                    key = rowData.getK();
                    selectedKey.setText("Key Userd: " + key);
                    //alert per indicare la modifica della chiave
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Modifica Chiave");
                    alert.setHeaderText("Chiave Modificata con Successo!");
                    alert.setContentText("La chiave è stata cambiata in: '" + key + "'.");

                    alert.showAndWait();
                    

                    System.out.println(rowData.getK());
                }
            });
            return row;
        });
        burgerTask = new HamburgerSlideCloseTransition(hamb);
        burgerTask.setRate(-1);
    }    
    
    @FXML
    public void hamb_ev() {
        burgerTask.setRate(burgerTask.getRate() * -1);
        burgerTask.play();
        
        if(opened == true){
            closeSx();
        } else {
            openSx();
        }       
    }
    
    @FXML
    public void getPath(){
        file = fileChooser.showOpenDialog(stage);
        System.out.println(file.getPath().toString());
        txt_path.setText(file.getPath().toString());
        
        reloadPath();
        
        txt_out.setText(Arrays.toString(data));
    }
    
    @FXML
    public void setKey(){
        String pathK = "keys.txt";
        if(fileTxt != null){
            pathK = fileTxt.getPath().toString();
        }
        try{
            f = new FileWriter(pathK, true);
            fOUT = new PrintWriter(f);
        }catch(IOException e){}
        
        key = txt_key.getText();
        selectedKey.setText("Key Userd: " + key);
        txt_key.setText("");
        
        alertKey();       
               
        fOUT.println(key);
        fOUT.flush();
        try{
           f.close(); 
        }
        catch(IOException e){

        }
        
        keycollection.add(new KeyRow(key));
    }
    
    @FXML
    public void go(){
        
        loader.setVisible(true);
        reloadPath();

        
        //inizio computazione
        long tStart = System.currentTimeMillis();
        th.start();
        //GO
        Vector v = th.go(data, key);
        //provo a fare un join sul thread ma non va!
        try {
            th.join(2);
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;

        loader.setVisible(false);

        System.out.println("computazione terminata");
        txt_comptime.setText("computazione terminata in: " + elapsedSeconds + " sec.");

        String outtt = "";
        for(int out=0; out<v.size(); out++){
            outtt += v.elementAt(out);
        }
        System.out.println(outtt);
        byte[] bval = new BigInteger(outtt, 2).toByteArray();

        txt_out.setText(Arrays.toString(bval));

        try (FileOutputStream fos = new FileOutputStream(file.getPath())) {
            fos.write(bval);
            fos.close();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        v.removeAllElements();
    }
    
    private void closeSx(){
        TranslateTransition closePsx = new TranslateTransition(Duration.millis(500), sx_anc);
        closePsx.setFromX(-200);
        closePsx.setToX(0);
        closePsx.setCycleCount(1);
        closePsx.setAutoReverse(true);
        closePsx.play();
        
        TranslateTransition closePdx = new TranslateTransition(Duration.millis(500), dx_anc);
        closePdx.setFromX(0);
        closePdx.setToX(200);
        closePdx.setCycleCount(1);
        closePdx.setAutoReverse(true);
        closePdx.play();
        
        opened = false;
        
    }
    
    private void openSx(){
        TranslateTransition openPsx = new TranslateTransition(Duration.millis(500), sx_anc);
        openPsx.setFromX(0);
        openPsx.setToX(-200);
        openPsx.setCycleCount(1);
        openPsx.setAutoReverse(true);
        
        openPsx.play();
        
        TranslateTransition openPdx = new TranslateTransition(Duration.millis(500), dx_anc);
        openPdx.setFromX(200);
        openPdx.setToX(0);
        openPdx.setCycleCount(1);
        openPdx.setAutoReverse(true);
        openPdx.play();
        
        opened = true;
    }
    
    private void initTabView(){
        Tab tbbytes = new Tab();
        tbbytes.setText("Bytes");
        tbbytes.setContent(txt_out);
        
        Tab tbkeys = new Tab();
        tbkeys.setText("Keys");
        tbkeys.setContent(tableKeys);
        
        tab.getTabs().add(tbbytes);
        tab.getTabs().add(tbkeys);
    }
    
    public void startkeys(){
        TableColumn keyCol = new TableColumn("Keys");
        
        keyCol.setCellValueFactory(
                new PropertyValueFactory<>("k")
        );
        
        tableKeys.getColumns().addAll(keyCol);
        
        String pathK = "keys.txt";
        if(fileTxt != null){
            pathK = fileTxt.getPath().toString();
        }
        
        try{
            q = new FileReader(pathK);
            fIN = new BufferedReader(q);
        }catch(IOException e){}
                        
        String sw = null;
        try{
            sw = fIN.readLine();
            while(sw!=null){
                keycollection.add(new KeyRow(sw));
                sw=fIN.readLine();
                
            }
        } catch(IOException e){
            
        }
        try{
           q.close(); 
        }
        catch(IOException e){}
        
        keycollection.sorted();
        
        tableKeys.setItems(keycollection);
    }
    
    private void reloadPath(){
        try{
            Path path = Paths.get(file.getPath());
            data = Files.readAllBytes(path);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    private void alertKey(){
        if(key.length() <= 32){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inserimento Chiave");
            alert.setHeaderText("Chiave Inserita con Successo!");
            alert.setContentText("La chiave è stata aggiunta e cambiata in: '" + key + "'.");

            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Inserimento Chiave");
            alert.setHeaderText("Errore Inserimento Chiave");
            alert.setContentText("La lunghezza massima della chiave è di 32 caratteri");

            alert.showAndWait();
        }
    }
}
