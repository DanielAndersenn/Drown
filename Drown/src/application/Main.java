package application;
	
import org.opencv.core.Core;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
			
			BorderPane rootElement = (BorderPane) loader.load();
			Scene scene = new Scene(rootElement,1600,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Kom nu Drone");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			UIController controller = loader.getController();
			
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					controller.setClosed();
				}
			}));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
}
