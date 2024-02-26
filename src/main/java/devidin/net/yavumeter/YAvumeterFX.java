package devidin.net.yavumeter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class YAvumeterFX extends Application {
	private static final Logger logger = LoggerFactory.getLogger(YAvumeterFX.class);
	private static VUmeterDisplayer vumeterDisplayer = null;
	private static Thread monitoringThread = null;
	private static Stage stage=null;

	public YAvumeterFX() {
		// do nothing
	}

	public static void main(String[] args) {
		try {
			logger.debug("Starting soundcard helper");
			SoundCardHelper.listMixers();
			logger.debug("soundcard helper started.");

			logger.debug("Starting monitoring...");
			vumeterDisplayer = new VUmeterDisplayer();
			monitoringThread = new Thread(vumeterDisplayer);
			monitoringThread.start();
			logger.debug("Monitoring started.");
			
			logger.debug("Starting displaying...");
			launch(args); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< start with YAvumeter.fxml
			
			logger.debug("Displaying ended.");
			
			System.exit(0); // force all threads to terminate
		} catch (Exception e) {
			logger.error("Executation failed with error " + e.getMessage());
			e.printStackTrace();
		}

	}
	@Override
	public void start(Stage primaryStage) {
		try {
			setStage(primaryStage);
			Parent root = FXMLLoader.load(getClass().getResource("/YAvumeter.fxml"));
			primaryStage.setTitle("Yet Another VU meter");
			primaryStage.setScene(new Scene(root, 600, 300));
			primaryStage.show();
			

			primaryStage.setOnCloseRequest(event -> {
				logger.info("Shutdown event. Exiting.");
				System.exit(0);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeScene(String fxml) throws IOException {
		Parent pane = FXMLLoader.load(getClass().getResource(fxml));
		getStage().getScene().setRoot(pane);
	}
	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	void setStage(Stage value) {
		YAvumeterFX.stage = value;
	}
	

}
