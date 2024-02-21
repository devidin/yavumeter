package devidin.net.yavumeter.display.graphical;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.VUmeterDisplayer;
import devidin.net.yavumeter.display.Displayer;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GraphicalDisplayer extends Application implements Displayer {
	private static GraphicalDisplayerParameters parameters = null;

	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayer.class);
	private static GraphicalDisplayer root = new GraphicalDisplayer(); // just to access static methods
	private static VUmeterDisplayer vumeterDisplayer = null;
	private static Thread monitoringThread = null;

	private static Stage stage = null;
	private static Scene rootScene = null;
	private static boolean topBarVisible = true;

	private static double[] previousAmplitude = new double[] { 0, 0 };
	private static long previousCallTimeMillis = System.currentTimeMillis();
	private static int maxDownSpeed = 1; // TODO make configurable
	private static int maxUpSpeed = 16; // "

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
			logger.error("Executation failed with error ", e);
		}

	}

	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("/Splash.fxml"));
			stage.setTitle("Yet Another VU meter");
			stage.initStyle(StageStyle.DECORATED); // show title and complete bar for now
			topBarVisible = true;
			rootScene = new Scene(root);
			stage.setScene(rootScene);
			stage.show();

			Thread.sleep(500);
			init2();

			stage.setOnCloseRequest(event -> {
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
			logger.error("Initialization failed.", e);
		}
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< change to GraphicalDisplayer
		root.getStage().sizeToScene();

		for (int i = 0; i < 2; i++) {
			try {

				resizeItemsWidth(); // make sure content is properly aligned
				resizeItemsHeight();

				String needleLabel = "#needle" + i;
				String imageLabel = "#image" + i;
				Line needle = (Line) rootScene.lookup(needleLabel);
				ImageView image = (ImageView) rootScene.lookup(imageLabel);

				needle.setVisible(true);
				needle.setSmooth(true);
				Color needleColor = Color.rgb((int) getParamaters().getNeedleRed(),
						(int) getParamaters().getNeedleGreen(), (int) getParamaters().getNeedleBlue());
				needle.setStroke(needleColor);
				needle.setStrokeWidth((int) getParamaters().getNeedleWidth());
				needle.setStrokeLineCap(StrokeLineCap.ROUND);

				if (getParamaters().isNeedleShadow()) {
					DropShadow shadow = new DropShadow();
					shadow.setBlurType(BlurType.GAUSSIAN);
					shadow.setColor(Color.rgb(64, 64, 64));
					shadow.setHeight(5);
					shadow.setWidth(getParamaters().getNeedleWidth());
					shadow.setRadius(5);
					shadow.setOffsetX(-6);
					shadow.setOffsetY(6);

					needle.setEffect(shadow);
				}
				needle.setLayoutX(image.getLayoutX());
				needle.setLayoutY(image.getLayoutY());
				/*
				 * needle.setStartX(0); needle.setStartY(0); needle.setEndX(image.getFitWidth()
				 * / 2); needle.setEndY(image.getFitHeight());
				 */
				needle.setVisible(false);

				setListeners();

				logger.debug("Needle draw ok " + i);
			} catch (Exception e) {
				logger.error("Failed to initialize needle " + i, e);
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
	public void displayLR(double[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayLRasNumber(double[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayLRasNumbers(double[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	public synchronized void display(double[] amplitude, int channels) {

		if (rootScene == null) {
			logger.debug("not yet ready to display...");
			return;
		}
		long startTime = System.currentTimeMillis();

		double[] intertialAmplitude = makeInertial(amplitude);

		for (int i = 0; i < intertialAmplitude.length; i++) {

			String needleLabel = "#needle" + i;
			String imageLabel = "#image" + i;
			Line needle = (Line) rootScene.lookup(needleLabel);
			ImageView image = (ImageView) rootScene.lookup(imageLabel);

			if (needle == null) {
				logger.error("Missing:" + needleLabel);
				return;
			}
			if (image == null) {
				logger.error("Missing:" + imageLabel);
				return;
			}

			/*
			 * if (needle == null || image == null) return;
			 */
			double[] segment = resizeSegementToImage(calculateSegment(intertialAmplitude[i], 128), image);

			if (segment != null) {
				needle.setStartX(segment[0]);
				needle.setStartY(segment[1]);
				needle.setEndX(segment[2]);
				needle.setEndY(segment[3]);
				needle.setVisible(true);
			} else {
				needle.setVisible(false);
			}
		}

		long endTime = System.currentTimeMillis();
		// System.out.println("Last call was " +(startTime-previousCallTimeMillis)+ "ms
		// ago. Execution time: " + (endTime - startTime)+"ms");
		previousCallTimeMillis = startTime;
	}

	double[] makeInertial(double[] amplitude) {
		double[] intertialAmplitude = new double[amplitude.length];

		for (int i = 0; i < intertialAmplitude.length; i++) {
			if (amplitude[i] > previousAmplitude[i] + maxUpSpeed) {
				intertialAmplitude[i] = previousAmplitude[i] + maxUpSpeed;
				// System.out.println("Up too fast");
			} else if (amplitude[i] < previousAmplitude[i] - maxDownSpeed) {
				intertialAmplitude[i] = previousAmplitude[i] - maxDownSpeed;
				// System.out.println("Down too fast");
			} else {
				intertialAmplitude[i] = amplitude[i];
			}
		}
		previousAmplitude = intertialAmplitude;
		return intertialAmplitude;
	}

	@Override
	public void displayAsNumber(double[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayAsNumbers(double[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	public static double[] calculateSegment(double amplitude, int maxAmplitude) {
		double[] coordinates = new double[4];

		double mu = getParamaters().getMaxAmplitudeAngle();
		double alpha = getParamaters().getMinAngle() + amplitude * mu / (double) maxAmplitude;

		double X = getParamaters().getxC() + Math.cos(alpha) * getParamaters().getNeedleLength();
		double Y = getParamaters().getyC() + Math.sin(alpha) * getParamaters().getNeedleLength();

		double[] segment = new double[] { getParamaters().getxC(), getParamaters().getyC(), X, Y };
		double[] rectangle = new double[] { 0, 0, getParamaters().getReferenceWidth(),
				getParamaters().getReferenceHeight() };

		double[] intersection = calculateIntersection(segment, rectangle);
		if (intersection != null) {
			coordinates[0] = intersection[0];
			coordinates[1] = intersection[1];
		} else {
			coordinates[0] = getParamaters().getxC();
			coordinates[1] = getParamaters().getyC();
		}
		coordinates[2] = X;
		coordinates[3] = Y;

		/*
		 * System.out.println("amplitude=" + amplitude + ",maxAmplitude=" +
		 * maxAmplitude; + ",minAngle="+getParamaters().getMinAngle() +
		 * ",maxAngle="+getParamaters().getMaxAngle() + ",mu=" + mu + ",alpha=" + alpha
		 * + ",X=" + X + ",Y=" + Y + ",xI=" + xI + ",yI=" + yI);
		 */
		return coordinates;
	}

	public static double[] resizeSegementToImage(double coordinates[], ImageView image) {

		if (coordinates == null)
			return null;
		double[] segment = new double[4];
		// X's
		segment[0] = (coordinates[0] * (double) image.getFitWidth() / (double) getParamaters().getReferenceWidth());
		segment[2] = (coordinates[2] * (double) image.getFitWidth() / (double) getParamaters().getReferenceWidth());
		// Ys
		segment[1] = (coordinates[1] * (double) image.getFitHeight() / (double) getParamaters().getReferenceHeight());
		segment[3] = (coordinates[3] * (double) image.getFitHeight() / (double) getParamaters().getReferenceHeight());
		/*
		 * System.out.println("Resize ("+coordinates[0]+","+coordinates[1]+"),("+
		 * coordinates[2]+","+coordinates[3]+") with FitWidth="+image.getFitWidth()+
		 * ",ReferenceWidth="+getParamaters().getReferenceWidth()
		 * +",FitHeight="+image.getFitHeight()+",ReferenceHeight="+getParamaters().
		 * getReferenceHeight());
		 */
		return segment;

	}

	// Calculate the intersection point of two line segments
	public static double[] calculateIntersection(double x1, double y1, double x2, double y2, double x3, double y3,
			double x4, double y4) {
		double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		// Check if the lines are parallel (denominator is zero)
		if (denominator == 0) {
			return null; // No intersection
		}

		double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
		double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator;

		// Check if the intersection point is within the line segments
		if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
			double[] intersection = new double[2];

			intersection[0] = x1 + t * (x2 - x1);
			intersection[1] = y1 + t * (y2 - y1);

			return intersection;
		} else {
			return null; // Intersection is outside the line segments
		}
	}

	public static double[] calculateIntersection(double[] segment, double[] rectangle) {
		double[] result;

		double rLeft = rectangle[0]; // upper left corner
		double rTop = rectangle[1];

		double rRight = rectangle[2]; // lower right corner
		double rBottom = rectangle[3];

		result = calculateIntersection(segment[0], segment[1], segment[2], segment[3], rLeft, rBottom, rRight, rBottom); // base
		if (result != null)
			return result;

		result = calculateIntersection(segment[0], segment[1], segment[2], segment[3], rLeft, rTop, rRight, rTop); // top
		if (result != null)
			return result;

		result = calculateIntersection(segment[0], segment[1], segment[2], segment[3], rLeft, rTop, rLeft, rBottom); // left
		if (result != null)
			return result;

		result = calculateIntersection(segment[0], segment[1], segment[2], segment[3], rRight, rTop, rRight, rBottom); // right
		if (result != null)
			return result;

		return null; // no intersection with image

	}

	public static Stage getStage() {
		return stage;
	}

	public void setListeners() {
		stage.widthProperty().addListener(observable -> {
			resizeItemsWidth();
		});
		stage.heightProperty().addListener(observable -> {
			resizeItemsHeight();
		});

		rootScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				swapStageStyle(mouseEvent);
			}
		});
	}

	public void resizeItemsHeight() {
		int m = 2; // matrix mxn
		int n = 1;

		double itemHeight = stage.getHeight() / n;

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				int index = j * m + i;
				try {

					String imageLabel = "#image" + index;
					logger.debug("Resizing " + imageLabel);
					ImageView image = (ImageView) rootScene.lookup(imageLabel);
					image.setFitHeight(itemHeight);

					String paneLabel = "#pane" + index;
					logger.debug("Repositioning " + paneLabel);
					Pane pane = (Pane) rootScene.lookup(paneLabel);
					pane.setLayoutY(j * itemHeight);

				} catch (Exception e) {
					logger.error("Failed to resizeItemsHeight " + i, e);
				}
			}
		}
	}

	public void resizeItemsWidth() {
		int m = 2; // matrix mxn
		int n = 1;

		double itemWidth = stage.getWidth() / m;

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				int index = j * m + i;
				try {

					String imageLabel = "#image" + index;
					logger.debug("Resizing " + imageLabel);
					ImageView image = (ImageView) rootScene.lookup(imageLabel);
					image.setFitWidth(itemWidth);

					String paneLabel = "#pane" + index;
					logger.debug("Repositioning " + paneLabel);
					Pane pane = (Pane) rootScene.lookup(paneLabel);
					pane.setLayoutX(i * itemWidth);

				} catch (Exception e) {
					logger.error("Failed to resizeItemsWidth " + i, e);
				}
			}
		}
	}

	public void swapStageStyle(MouseEvent event) {
		logger.debug("mouse click detected! " + event.getSource());

		switch (event.getButton()) {
		case MouseButton.PRIMARY:
			logger.debug(" primary mouse click detected! " + event.getSource());
			//cf. https://stackoverflow.com/questions/40773411/what-prevents-changing-primarystage-initstyle-in-javafx
			Stage newAppStage = new Stage();
			newAppStage.setScene(rootScene);

			PauseTransition delay = new PauseTransition(Duration.seconds(1));
			delay.setOnFinished(e -> {
				logger.debug(" on finished started :" + e);
				stage.hide();
				stage.close();
				topBarVisible = !topBarVisible;
				if (topBarVisible)
					newAppStage.initStyle(StageStyle.DECORATED);
				else
					newAppStage.initStyle(StageStyle.UNDECORATED);
				newAppStage.show();
				logger.debug(" on finished completed :" + e);
			});
			delay.play();
			stage.show();
			logger.debug("mouse clicked completed " + event.getSource());
			
			break;
		case MouseButton.SECONDARY:
			logger.debug("secondary mouse click detected! " + event.getSource());
			break;
		case MouseButton.MIDDLE:
			logger.debug("middle mouse click detected! " + event.getSource());
			break;

		case MouseButton.NONE:
		default:
			logger.error("Unexpected button type:" + event.getButton());
		}
	}
}
