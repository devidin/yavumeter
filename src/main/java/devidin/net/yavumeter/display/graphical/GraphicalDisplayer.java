package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.System;
import devidin.net.yavumeter.display.Displayer;
import javafx.stage.Stage;

public class GraphicalDisplayer extends Displayer {
	private static GraphicalDisplayerParameters configuration = null;
	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayer.class);
	private static Stage stage = null;
	
	public GraphicalDisplayerParameters getConfiguration() {
		if (configuration == null)
			configuration = GraphicalDisplayerParameters.LoadConfiguration();
		return configuration;
	}

	public void init() {
		logger.debug("Loading configuration...");
		getConfiguration();
		logger.info("Configuration loaded:" + getConfiguration());
		/*
		 * logger.debug("Starting displayer..."); 
		 * app = new GraphicalDisplayer();
		 * logger.info("Strating app:"+getConfiguration());
		 */
	}

	public void shutdown() {
		logger.debug("Shutdown complete");
	}

	public GraphicalDisplayer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void displayLR(int[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayLRasNumber(int[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayLRasNumbers(int[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		logger.info("display "+amplitudeLR.length+" bytes of "+ channels +" channels.");
		if (stage==null)
			System.out.println("display() invoked, stage not set yet. Go to sleep.");
		else
			System.out.println("display() invoked, not yet implemented. Go to sleep.");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.info("Interrupted: " + e);
		}
		long endTime = System.currentTimeMillis();

		System.out.println("Awaken. Execution time: " + (endTime - startTime));
	}

	@Override
	public void displayAsNumber(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayAsNumbers(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	private static Stage getStage() {
		return stage;
	}

	private static void setStage(Stage stage) {
		GraphicalDisplayer.stage = stage;
		logger.info("Stage set.");
	}

}
