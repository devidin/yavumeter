package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.System;

import devidin.net.yavumeter.VUmeterDisplayer;
import devidin.net.yavumeter.YAvumeterFX;
import devidin.net.yavumeter.display.Displayer;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class GraphicalDisplayer extends Application implements Displayer {
	private static GraphicalDisplayerParameters parameters = null;
	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayer.class);
	private static GraphicalDisplayer root = new GraphicalDisplayer(); // just to access static methods
	private static VUmeterDisplayer vumeterDisplayer = null;
	private static Thread monitoringThread = null;
	private static Stage stage = null;
	private static Scene rootScene = null;

	public static GraphicalDisplayerParameters getParamaters() {
		if (parameters == null)
			parameters = GraphicalDisplayerParameters.LoadConfiguration();
		return parameters;
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
			launch(args); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< start with Splash.fxml

			logger.debug("Displaying ended.");

			System.exit(0); // force all threads to terminate
		} catch (Exception e) {
			logger.error("Executation failed with error " + e.getMessage());
			e.printStackTrace();
		}

	}

	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("/Splash.fxml"));
			primaryStage.setTitle("Yet Another VU meter");
			rootScene = new Scene(root);
			primaryStage.setScene(rootScene);
			primaryStage.show();
			// try {rootScene.notify();} catch (java.lang.IllegalMonitorStateException e) {}
			// try {primaryStage.notifyAll();} catch (java.lang.IllegalMonitorStateException
			// e) {}
			// Platform.runLater(monitoringThread);
			ImageView retrievedImageView = (ImageView) rootScene.lookup("#splashImage");
			retrievedImageView.setLayoutX(retrievedImageView.getLayoutX() + 0.0001);

			BorderPane borderPane = (BorderPane) rootScene.lookup("#splashBorderPane");
			borderPane.setVisible(false);
			borderPane.setVisible(true);

			Thread.sleep(500);
			init2();

			primaryStage.setOnCloseRequest(event -> {
				logger.info("Shutdown event. Exiting.");
				System.exit(0);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Start complete");
	}

	public void changeScene(String fxml) throws IOException {
		Parent pane = FXMLLoader.load(getClass().getResource(fxml));
		stage.getScene().setRoot(pane);
	}

	public void init() {

	}

	public void init2() {
		logger.debug("Loading configuration...");
		getParamaters();
		logger.info("Configuration loaded:" + getParamaters());

		logger.debug("changing view to GraphicalDisplayer...");
		try {
			root.changeScene("/GraphicalDisplayerBasic.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< change to
			// GraphicalDisplayer
		root.getStage().sizeToScene();

		for (int i = 0; i < 2; i++) {
			try {

				String needleLabel = "#needle" + i;
				String imageLabel = "#image" + i;
				Line needle = (Line) rootScene.lookup(needleLabel);
				ImageView image = (ImageView) rootScene.lookup(imageLabel);

				needle.setLayoutX(image.getLayoutX());
				needle.setLayoutY(image.getLayoutY());
				needle.setStartX(0);
				needle.setStartY(0);
				needle.setEndX(image.getFitWidth() / 2);
				needle.setEndY(image.getFitHeight());

				System.out.println("Needle draw ok " + i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

	/*
	 * public void display2(int[] amplitudeLR, int channels) { // TODO
	 * Auto-generated method stub long startTime = System.currentTimeMillis();
	 * logger.info("display " + amplitudeLR.length + " bytes of " + channels +
	 * " channels."); if (root.getStage() == null)
	 * System.out.println("display() invoked, stage not set yet. Go to sleep.");
	 * else
	 * System.out.println("display() invoked, not yet implemented. Go to sleep.");
	 * 
	 * try { Thread.sleep(500); } catch (InterruptedException e) {
	 * logger.info("Interrupted: " + e); } long endTime =
	 * System.currentTimeMillis();
	 * 
	 * System.out.println("Awaken. Execution time: " + (endTime - startTime)); }
	 */
	public void display(int[] amplitudeLR, int channels) {

		if (rootScene == null) {
			System.out.println("not yet ready to display...");
			return;
		}
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < amplitudeLR.length; i++) {

			String needleLabel = "#needle" + i;
			String imageLabel = "#image" + i;
			Line needle = (Line) rootScene.lookup(needleLabel);
			ImageView image = (ImageView) rootScene.lookup(imageLabel);

			if (needle == null) {
				System.out.println("Missing:" + needleLabel);
				return;
			}
			if (image == null) {
				System.out.println("Missing:" + imageLabel);
				return;
			}

			long[] segment = resizeSegement(calculateSegment(amplitudeLR[i], 128), image);

			needle.setStartX(segment[0]);
			needle.setStartY(segment[1]);
			needle.setEndX(segment[2]);
			needle.setEndY(segment[3]);
		}

		long endTime = System.currentTimeMillis();

		System.out.println("Execution time: " + (endTime - startTime));
	}

	@Override
	public void displayAsNumber(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayAsNumbers(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	public static double[] calculateSegment(int amplitude, int maxAmplitude) {
		double[] coordinates = new double[4];

		double mu = getParamaters().getMaxAngle() - getParamaters().getMinAngle();
		double alpha = getParamaters().getMinAngle() + (double) amplitude * mu / (double) maxAmplitude;

		double X = getParamaters().getxC() + Math.cos(alpha) * getParamaters().getNeedleLength();
		double Y = getParamaters().getyC() - Math.sin(alpha) * getParamaters().getNeedleLength();

		// double intersection[]=calculateIntersection()

		double xI = getParamaters().getxC();
		double yI = getParamaters().getyC();

		coordinates[0] = xI;
		coordinates[1] = yI;

		coordinates[2] = X;
		coordinates[3] = Y;

		System.out.println("amplitude=" + amplitude + ",maxAmplitude=" + maxAmplitude
				+ ",minAngle="+getParamaters().getMinAngle()
				+ ",maxAngle="+getParamaters().getMaxAngle()
				+ ",mu=" + mu + ",alpha=" + alpha + ",X=" + X + ",Y=" + Y + ",xI=" + xI + ",yI=" + yI);

		return coordinates;
	}

	public static long[] resizeSegement(double coordinates[], ImageView image) {

		long[] segment = new long[4];
		// X's
		segment[0] = (long) (coordinates[0] * image.getFitWidth() / getParamaters().getReferenceWidth());
		segment[2] = (long) (coordinates[2] * image.getFitWidth() / getParamaters().getReferenceWidth());
		// Ys
		segment[1] = (long) (coordinates[1] * image.getFitHeight() / getParamaters().getReferenceHeight());
		segment[3] = (long) (coordinates[3] * image.getFitHeight() / getParamaters().getReferenceHeight());

		return segment;

	}

	// Calculate the intersection point of two line segments
	public static double[] calculateIntersection(double x1, double y1, double x2, double y2, double x3, double y3,
			double x4, double y4) {
		double[] intersection = new double[2];

		double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		// Check if the lines are parallel (denominator is zero)
		if (denominator == 0) {
			return null; // No intersection
		}

		double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
		double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator;

		// Check if the intersection point is within the line segments
		if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
			intersection[0] = x1 + t * (x2 - x1);
			intersection[1] = y1 + t * (y2 - y1);
			return intersection;
		} else {
			return null; // Intersection is outside the line segments
		}
	}

	public static Stage getStage() {
		return stage;
	}

}
