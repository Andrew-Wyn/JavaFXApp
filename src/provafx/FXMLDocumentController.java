package provafx;

import java.awt.event.MouseEvent;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;

public class FXMLDocumentController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    private Window stage;
    
    @FXML
    private Button btn_fileChooser;
    
    @FXML
    private Button btn_key;
    
    @FXML
    private TextField txt_key;
    
    @FXML
    private Label txt_path;
    
    @FXML
    private Label txt_comptime;
    
    @FXML
    private TextArea txt_out;
        
    @FXML
    private TextArea txt_keys;
    
    private ObservableList<KeyRow> keycollection = FXCollections.observableArrayList();
    
    @FXML
    private TableView<KeyRow> tab_keys;
    
    @FXML
    private MenuItem txt_keyused;
    
    @FXML
    public static ProgressIndicator progres_comp;
    
    @FXML
    private MenuItem open_file;
    
    FileChooser fileChooser = new FileChooser();
    
    String key;
    byte[] data = null;
    File file = null;
    File fileTxt = null;
    //apro gli stram di input  ** file ----> programma **
    FileReader q = null;
    BufferedReader fIN = null;
    //apro gli stream di output ** programma ----> file ** 
    FileWriter f = null;
    PrintWriter fOUT = null;
    String keys = "";    
    
    
    @FXML
    void initialize() {
        assert btn_fileChooser != null : "fx:id=\"btn_fileChooser\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        key="";
        txt_out.setWrapText(true);
        txt_out.setText("Nessun File Selezionato ...");
        TableColumn keyCol = new TableColumn("Keys");
        
        keyCol.setCellValueFactory(
                new PropertyValueFactory<>("k")
        );
        
        tab_keys.getColumns().addAll(keyCol);
        startkeys();
        /*set event listener on row for double click*/
        tab_keys.setRowFactory( tv -> {
            TableRow<KeyRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    KeyRow rowData = row.getItem();
                    key = rowData.getK();
                    txt_keyused.setText("La chiave in utilizzo è: '" + key + "'");
                    
                    //alert per indicare la modifica della chiave
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Modifica Chiave");
                    alert.setHeaderText("Chiave Modificata con Successo!");
                    alert.setContentText("La chiave è stata cambiata in: '" + key + "'.");

                    alert.showAndWait();
                    

                    System.out.println(rowData.getK());
                }
            });
            return row;
        });
        
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
        txt_key.setText("");
        
        alertKey();       
        
        keys += "\n" + key;
        
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
            reloadPath();
                    
            //prendere bytes da file esterno
            StringBuilder binary = new StringBuilder();
            for (byte b : data)
            {
               int val = b;
               for (int i = 0; i < 8; i++)
               {
                  binary.append((val & 128) == 0 ? 0 : 1);
                  val <<= 1;
               }
            }
                        
            Vector v = new Vector();
            
            
            byte[] bytesk = key.getBytes();
            StringBuilder binaryk = new StringBuilder();
            
            for (byte b : bytesk)
            {
               int val = b;
               for (int i = 0; i < 8; i++)
               {
                  binaryk.append((val & 128) == 0 ? 0 : 1);
                  val <<= 1;
               }
            }
            
            int binlengthk = binaryk.length();
            int appodiffk = 0;
            if(binlengthk < 256){
                appodiffk = 256 - binlengthk;
            }else{
                //TODO dichiarando la differenza tra binlength e 256, vado ad eliminarla,
            }
            
            /* vado ad aggiungere bit pari a 0 alla fine del blocco binario in chiaro fino al raggiungimento di un blocco divisibile per 256 */
            int numero_aggiuntek = 0;
            int num_blocchik = appodiffk;
            int numero_macrok = num_blocchik;
            while(num_blocchik != 0){
                binaryk.append(0);
                num_blocchik--;
            }
            
            /* vado ad aggiungere bit pari a 0 alla fine del blocco binario in chiaro fino al raggiungimento di un blocco divisibile per 256 */
            int binlength = binary.length();
            int appodiff = 0;
            
            if(binlength < 256){
                appodiff = 256 - binlength;
            }else{
                int rest = binary.length()%256;
                for(int i=0; i<256-rest; i++){
                    binary.append(0);
                }
            }
            
            int num = binary.length()/256;
            if(binary.length()%256 != 0){
                num++;
            }
            
            int numero_aggiunte = 0;
            int num_blocchi = appodiff;
            int numero_macro = num_blocchi;
            while(num_blocchi != 0){
                binary.append(0);
                num_blocchi--;
            }
            
            System.out.println(""+binary);
            System.out.println(""+binary.length());
            
            //creare i due array a 4 dim 
            char [][][][]fotoSammlung = new char[4][4][4][4];
            char [][][][]fotoSammlungk = new char[4][4][4][4];
            
            int bit_ins = 0;
            int bit_insk = 0;
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    for(int x=0; x<4; x++){
                        for(int y=0; y<4; y++){
                            fotoSammlungk[i][j][x][y]=binaryk.charAt(bit_insk);
                            bit_insk++;
                        }
                    }
                }
            }
            
            //azzero per sicurezza
            bit_insk=0;
            
            /*******************************/
            
            //inizio computazione
            long tStart = System.currentTimeMillis();
            
            for(int giga=0; giga<num; giga++){
                                                                
                for(int i=0; i<4; i++){
                    for(int j=0; j<4; j++){
                        for(int x=0; x<4; x++){
                            for(int y=0; y<4; y++){
                                fotoSammlung[i][j][x][y]=binary.charAt(bit_ins);
                                //fotoSammlungk[i][j][x][y]=binaryk.charAt(bit_ins);
                                bit_ins++;
                            }
                        }
                    }
                }
                if(true){
                  
                    for(int iiiii=0; iiiii<2; iiiii++){
                        for(int i=0; i<4; i++){
                            char [][] temp1 = new char[4][4];
                            char [][] tempappo1 = new char[4][4];
                            for(int ii=0; ii<i; ii++){
                                for(int q=0; q<4; q++){
                                    for(int z=0; z<4; z++){
                                        for(int p=0; p<4; p++){
                                            if(q==0){
                                                tempappo1[z][p]=fotoSammlung[i][0][z][p];
                                            }
                                            if(q<3){
                                                temp1[z][p]=fotoSammlung[i][q+1][z][p];
                                                fotoSammlung[i][q][z][p] = temp1[z][p];
                                            }else{
                                                fotoSammlung[i][q][z][p] = tempappo1[z][p];
                                            }
                                        }
                                    }
                                }  
                            }
                            //livello a 3 dimensioni
                            for(int j=0; j<4; j++){
                                char [] temp2 = new char[4];
                                char [] tempappo2 = new char[4];
                                for(int ii=0; ii<j; ii++){
                                    for(int z=0; z<4; z++){
                                        for(int p=0; p<4; p++){
                                            if(z==0){
                                                tempappo2[p]=fotoSammlung[i][j][0][p];
                                            }
                                            if(z<3){
                                                temp2[p]=fotoSammlung[i][j][z+1][p];
                                                fotoSammlung[i][j][z][p] = temp2[p];
                                            }else{
                                                fotoSammlung[i][j][z][p] = tempappo2[p];
                                            }
                                        }
                                    }
                                }
                                //livello a 2 dimensioni
                                for(int x=0; x<4; x++){
                                    char temp3 = 'a';
                                    char tempappo3 = 'a';
                                    for(int ii=0; ii<x; ii++){
                                        for(int p=0; p<4; p++){
                                            if(p==0){
                                                tempappo3=fotoSammlung[i][j][x][0];
                                            }
                                            if(p<3){
                                                temp3=fotoSammlung[i][j][x][p+1];
                                                fotoSammlung[i][j][x][p] = temp3;
                                            }else{
                                                fotoSammlung[i][j][x][p] = tempappo3;
                                            }
                                        }
                                    }
                                    //livello a 1 dimensioni
                                    for(int y=0; y<4; y++){

                                        char un = fotoSammlung[i][j][x][y];
                                            char du = fotoSammlungk[i][j][x][y];
                                            if (un==du){
                                                fotoSammlung[i][j][x][y]='0';
                                            } else {
                                                fotoSammlung[i][j][x][y]='1';
                                            }

                                        if(y<3){
                                            //sommo il cubo di posizione i con il cubo di posizione i+1
                                            //somma xor
                                            char un2 = fotoSammlung[i][j][x][y];
                                            char du2 = fotoSammlung[i][j][x][y+1];
                                            if (un2==du2){
                                                fotoSammlung[i][j][x][y]='0';
                                            } else {
                                                fotoSammlung[i][j][x][y]='1';
                                            }
                                        }
                                    }
                                    if(x<3){
                                        //sommo il cubo di posizione i con il cubo di posizione i+1
                                        for(int y=0; y<4; y++){
                                            //somma xor
                                            char un = fotoSammlung[i][j][x][y];
                                            char du = fotoSammlung[i][j][x+1][y];
                                            if (un==du){
                                                fotoSammlung[i][j][x][y]='0';
                                            } else {
                                                fotoSammlung[i][j][x][y]='1';
                                            }
                                        }
                                    }
                                }
                                if(j<3){
                                    //sommo il cubo di posizione i con il cubo di posizione i+1
                                    for(int x=0; x<4; x++){
                                        for(int y=0; y<4; y++){
                                            //somma xor
                                            char un = fotoSammlung[i][j][x][y];
                                            char du = fotoSammlung[i][j+1][x][y];
                                            if (un==du){
                                                fotoSammlung[i][j][x][y]='0';
                                            } else {
                                                fotoSammlung[i][j][x][y]='1';
                                            }
                                        }
                                    }
                                }
                            }
                            if(i<3){
                                //sommo il cubo di posizione i con il cubo di posizione i+1
                                for(int j=0; j<4; j++){
                                    for(int x=0; x<4; x++){
                                        for(int y=0; y<4; y++){
                                            //somma xor
                                            char un = fotoSammlung[i][j][x][y];
                                            char du = fotoSammlung[i+1][j][x][y];
                                            if (un==du){
                                                fotoSammlung[i][j][x][y]='0';
                                            } else {
                                                fotoSammlung[i][j][x][y]='1';
                                            }
                                        }
                                    }
                                }                    
                            }
                        }
                    }
                }
                
                StringBuilder binaryc = new StringBuilder();
                
                //metto il vettore a quattro dimensioni in una stringa binaria
                for(int i=0; i<4; i++){
                    for(int j=0; j<4; j++){
                        for(int x=0; x<4; x++){
                            for(int y=0; y<4; y++){
                                binaryc.append(fotoSammlung[i][j][x][y]);
                            }
                        }
                    }
                }
                System.out.println(binaryc);
                v.addElement(binaryc);
            }
            
            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            
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
            binary.setLength(0);
            v.removeAllElements();
            bit_ins = 0;
    }
    
    @FXML
    public void about(){
        
        MainChose m = new MainChose();
        try{
            m.init();
            m.start(new Stage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void alertKey(){
        if(key.length() <= 32){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Inserimento Chiave");
            alert.setHeaderText("Chiave Inserita con Successo!");
            alert.setContentText("La chiave è stata aggiunta e cambiata in: '" + key + "'.");

            alert.showAndWait();
        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Inserimento Chiave");
            alert.setHeaderText("Errore Inserimento Chiave");
            alert.setContentText("La lunghezza massima della chiave è di 32 caratteri");

            alert.showAndWait();
        }
    }
    
    public void choosePath(){
        fileTxt = fileChooser.showOpenDialog(stage);
        System.out.println(fileTxt);
        keycollection.removeAll(keycollection);
        startkeys();
    }
    
    public void reloadPath(){
        try{
            Path path = Paths.get(file.getPath());
            data = Files.readAllBytes(path);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public void startkeys(){
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
                keys += "\n";
                keys += sw;
                keycollection.add(new KeyRow(sw));
                sw=fIN.readLine();
                
            }
        }
        
        catch(IOException e){
            
        }
        try{
           q.close(); 
        }
        catch(IOException e){}
        
        keycollection.sorted();
        
        tab_keys.setItems(keycollection);
        System.out.println(tab_keys.getItems());
    }
}