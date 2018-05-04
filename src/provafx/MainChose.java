package provafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainChose extends Application{
    
    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("choseFXML.fxml")); //caricamento del file XML (ricorda: android)
        Scene scene = new Scene(root); //istanzio la scena (?)
        stage.setScene(scene); //setto la scena nello stage 
        stage.show(); //visualizzo lo stage (ricorda: JFrame)
    }
}
