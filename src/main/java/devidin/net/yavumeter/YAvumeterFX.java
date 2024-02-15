package devidin.net.yavumeter;

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
	private static VUmeterDisplayer vumeterDisplayer = new VUmeterDisplayer();
	private static Thread monitoringThread = new Thread(vumeterDisplayer);
	public YAvumeterFX() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			logger.debug("Started");
			SoundCardHelper.listMixers();
			/*
			 * logger.debug("Starting monotoring..."); VUmeterDisplayer vumeterDisplayer =
			 * new VUmeterDisplayer(); Thread monitoringThread = new
			 * Thread(vumeterDisplayer); monitoringThread.start();
			 * logger.debug("Monotoring started.");
			 */
			logger.debug("Starting monotoring...");
			// vumeterDisplayer.setScenes(Scene[] scenes);
			monitoringThread.start();
			logger.debug("Monotoring started.");
			
			logger.debug("Starting displaying...");
			launch(args);
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
			//BorderPane root = new BorderPane();
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Parent root = FXMLLoader.load(getClass().getResource("YAvumeter.fxml"));
			Scene scene = new Scene(root, 400, 300);
			primaryStage.setTitle("YA VUmeter");
			primaryStage.setScene(scene);
			primaryStage.show();
			

			primaryStage.setOnCloseRequest(event -> {
				logger.info("Shutdown event. Exiting.");
				System.exit(0);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
