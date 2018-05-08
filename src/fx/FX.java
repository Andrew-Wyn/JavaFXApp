package fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class FX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fx/View/FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        try{
            stage.getIcons().add(
                new Image(
                   FX.class.getResourceAsStream( "games.ico" ))); 
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
