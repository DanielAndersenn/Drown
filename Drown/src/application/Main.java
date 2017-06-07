package application;
	
import org.opencv.core.Core;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	public final static int IMAGE_WIDTH = 1280; // 640 or 1280
	public final static int IMAGE_HEIGHT = 720; // 360 or 720
	
	public final static int TOLERANCE = 30;
	
	private IARDrone drone = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
			
			BorderPane rootElement = (BorderPane) loader.load();
			Scene scene = new Scene(rootElement,1600,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Drone is love, drone is life");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			MainController controller = loader.getController();
			
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
		
		//Mathias
		
	}
}
