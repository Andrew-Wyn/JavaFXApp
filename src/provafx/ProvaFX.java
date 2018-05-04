package provafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProvaFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml")); //caricamento del file XML (ricorda: android)
        
        Scene scene = new Scene(root); //istanzio la scena (?)
        
        stage.setScene(scene); //setto la scena nello stage 
        stage.show(); //visualizzo lo stage (ricorda: JFrame)
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
