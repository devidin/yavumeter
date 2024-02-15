package devidin.net.yavumeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class YAvumeterFX extends Application {
	private static final Logger logger = LoggerFactory.getLogger(YAvumeterFX.class);

	public YAvumeterFX() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			logger.debug("Started");
			SoundCardHelper.listMixers();

			logger.debug("Starting monotoring...");
			VUmeterDisplayer vumeterDisplayer = new VUmeterDisplayer();
			Thread monitoringThread = new Thread(vumeterDisplayer);
			monitoringThread.start();
			logger.debug("Monotoring started.");

			logger.debug("Starting displaying...");
			launch(args);
			logger.debug("Displaying ended.");
			
			monitoringThread.interrupt();
			logger.debug("Monotoring stopped.");
		} catch (Exception e) {
			logger.error("Executation failed with error "+e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
