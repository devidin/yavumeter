package devidin.net.yavumeter.display.graphical;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.VUmeterDisplayer;
import devidin.net.yavumeter.display.Displayer;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

public class GraphicalDisplayer extends Application implements Displayer {
	private static GraphicalDisplayerParameters parameters = null;

	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayer.class);
	private static final String TITLE_BAR = "Yet Another VU meter";
	private static final String FXML_FILE = "/GraphicalDisplayerBasic.fxml";
	private static GraphicalDisplayer application = new GraphicalDisplayer(); // just to access static methods

	// monitoring thread
	private static VUmeterDisplayer vumeterDisplayer = null;
	private static Thread monitoringThread = null;
	// decoration variables
	private static Stage decoratedStage = null;
	private static Stage undecoratedStage = null;
	private static Stage activeStage = null; // one of the above
	private static boolean topBarVisible = true; // decorated
	private static double originalWidth;
	private static double originalHeight;
	private static double originalX;
	private static double originalY;
	// smoothing variables
	private static double[] previousAmplitude = new double[] { 0, 0 };

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
			launch(args);
			logger.debug("Displaying ended.");

			System.exit(0); // force all threads to terminate
		} catch (Exception e) {
			logger.error("Executation failed with error ", e);
		}

	}

	public void start(Stage startUpStage) {
		logger.debug("Loading configuration...");
		getParamaters();
		logger.info("Configuration loaded:" + getParamaters());

		logger.debug("Building stages...");
		undecoratedStage = buildStage(StageStyle.UNDECORATED, FXML_FILE);
		decoratedStage = buildStage(StageStyle.DECORATED, FXML_FILE);
		logger.debug("Stages built.");

		logger.debug("Initializing stages items...");
		initializeItems(undecoratedStage);
		initializeItems(decoratedStage);
		logger.debug("Stages items initialized.");

		activeStage = decoratedStage;
		activeStage.show();
		centerStage(activeStage);

		resizeItems(); // make sure content is properly aligned
	}

	public void init() { // do nothing

	}

	private void centerStage(Stage stage) {
		// Get the primary screen
		Screen screen = Screen.getPrimary();

		// Get the bounds of the screen
		Rectangle2D bounds = screen.getVisualBounds();

		// Calculate the center coordinates
		double centerX = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) / 2;
		double centerY = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) / 2;

		// Set the stage position
		stage.setX(centerX);
		stage.setY(centerY);

		logger.debug("centerStage(" + stage.getWidth() + "," + stage.getHeight() + "): bounds=" + bounds + ", center=("
				+ centerX + "," + centerY + ")");
	}

	public Stage buildStage(StageStyle style, String fxmlFilePath) {

		Pane newPane = null;
		try {
			newPane = application.loadPane(fxmlFilePath);
		} catch (IOException e) {
			logger.error("Initialization failed.", e);
		}

		Scene newScene = new Scene(newPane);
		Stage newStage = new Stage();
		newStage.setTitle(TITLE_BAR);
		newStage.initStyle(style);
		newStage.setScene(newScene);
		newStage.sizeToScene();
		setListeners(newStage);

		return newStage;
	}

	public void initializeItems(Stage stage) {
		for (int i = 0; i < 2; i++) {
			try {

				String needleLabel = "#needle" + i;
				String imageLabel = "#image" + i;
				String foregroundLabel = "#foreground" + i;

				/*
				 * ImageView foregroundImageView = (ImageView)
				 * stage.getScene().lookup(foregroundLabel); try {
				 * foregroundImageView.setImage(getParamaters().getForegroundImage());
				 * //foregroungImageView.setVisible(true); ; } catch (Exception e) {
				 * logger.warn("Foreground not found."); foregroundImageView.setVisible(false);
				 * ; }
				 */
				// layer 0 : background
				Color backgroundColor = Color.rgb((int) getParamaters().getBackgroundRed(),
						(int) getParamaters().getBackgroundGreen(), (int) getParamaters().getBackgroundBlue());
				Rectangle background = (Rectangle) stage.getScene().lookup("#background");
				background.setFill(backgroundColor);

				// layer 1 : VU meter image
				ImageView imageView = (ImageView) stage.getScene().lookup(imageLabel);
				imageView.setImage(getParamaters().getImage());
				imageView.setPreserveRatio(getParamaters().isPreserveAspectRatio());
				// layer 2 : needle (2.1) & shadow (2.0)
				Line needle = (Line) stage.getScene().lookup(needleLabel);
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
					shadow.setOffsetX(getParamaters().getNeedleShadowOffsetX());
					shadow.setOffsetY(getParamaters().getNeedleShadowOffsetY());

					needle.setEffect(shadow);
				}
				needle.setLayoutX(imageView.getLayoutX());
				needle.setLayoutY(imageView.getLayoutY());
				logger.debug("Needle draw ok " + i);

				// layer 3 : foreground
				// nothing to change

			} catch (Exception e) {
				logger.error("Failed to initialize needle " + i, e);
			}
		}

	}

	public Pane loadPane(String fxml) throws IOException {
		Pane pane = FXMLLoader.load(getClass().getResource(fxml));
		return pane;
	}

	public void setListeners(Stage stage) {

		stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("maximizedProperty:" + newValue);
			{
				if (!oldValue) {
					stage.setWidth(originalWidth);
					stage.setHeight(originalHeight);
					stage.setX(originalX);
					stage.setY(originalY);
					logger.debug("stage reset to original position");
				} else {
					if (!newValue) {
						originalWidth = stage.getWidth();
						originalHeight = stage.getHeight();
						originalX = stage.getX();
						originalY = stage.getY();
						logger.debug("stage information saved to allow future reset to original position");
					}
				}
			}
		});
		stage.resizableProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("resizableProperty:" + newValue);
		});
		stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("iconifiedProperty:" + newValue);
		});
		stage.getScene().widthProperty().addListener((observable, oldvalue, newvalue) -> {
			if (oldvalue != newvalue) { // avoid infinite loop
				resizeItems();
				resizeItems();
			}
		});
		stage.getScene().heightProperty().addListener((observable, oldvalue, newvalue) -> {
			if (oldvalue != newvalue) { // avoid infinite loop
				resizeItems();
				resizeItems();
			}
		});

		stage.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				handleMouseEvent(mouseEvent);
			}
		});

		stage.setOnCloseRequest(event -> {
			logger.info("Shutdown event. Exiting.");
			System.exit(0);
		});
	}

	public void resizeItems() {
		resizeItemsHeight();
		resizeItemsWidth();
	}

	public synchronized void resizeItemsHeight() {
		int m = 2; // matrix mxn (future use)
		int n = 1;

		/*
		 * try { activeStage.setHeight(activeStage.getHeight()); // register user's
		 * setting } catch (Throwable t) {// will fail if not triggered by user : ignore
		 * logger.debug("resizeItemsHeight", t); }
		 */
		double itemHeight = activeStage.getScene().getHeight() / n;

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				int index = j * m + i;
				try {
					String paneLabel = "#pane" + index;
					logger.debug("Repositioning vertically " + paneLabel);

					String imageLabel = "#image" + index;
					logger.debug("Resizing height " + imageLabel);
					ImageView image = (ImageView) activeStage.getScene().lookup(imageLabel);
					image.setFitHeight(itemHeight);
					double actualHeight = image.getBoundsInParent().getHeight();
					image.setLayoutY((itemHeight - actualHeight) / 2); // center vertically

					String foregroundImageLabel = "#foreground" + index;
					ImageView foregroundImage = (ImageView) activeStage.getScene().lookup(foregroundImageLabel);
					if (foregroundImage != null) {
						foregroundImage.setLayoutY(image.getLayoutY());
						foregroundImage.setFitHeight(image.getBoundsInParent().getHeight());
						logger.debug("Repositioning foreground vertically " + foregroundImageLabel + "--> height="
								+ image.getBoundsInParent().getHeight() + ",Y=" + image.getLayoutY());
					}

					Pane pane = (Pane) activeStage.getScene().lookup(paneLabel);
					pane.setLayoutY(j * itemHeight);

					Rectangle background = (Rectangle) activeStage.getScene().lookup("#background");
					background.setHeight(activeStage.getHeight());
					// pane.setPrefHeight(itemHeight);

					/*
					 * if (activeStage != undecoratedStage) //
					 * undecoratedStage.getScene().getRoot().set(activeStage.getScene().getHeight())
					 * ; ((Pane) undecoratedStage.getScene().getRoot())
					 * .setPrefHeight(activeStage.getScene().getHeight());
					 */
					logger.debug("New height: " + activeStage.getScene().getHeight());

				} catch (Exception e) {
					logger.error("Failed to resizeItemsHeight " + i, e);
				}
			}
		}
	}

	public synchronized void resizeItemsWidth() {
		int m = 2; // matrix mxn (future use)
		int n = 1;

		/*
		 * try { activeStage.setWidth(activeStage.getWidth()); // register user's
		 * settings } catch (Throwable t) { // will fail if not triggered by user :
		 * ignore logger.debug("resizeItemsWidth", t); }
		 */

		double itemWidth = activeStage.getScene().getWidth() / m;

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				int index = j * m + i;
				try {
					String paneLabel = "#pane" + index;
					logger.debug("Repositioning horizontally" + paneLabel);
					String imageLabel = "#image" + index;
					logger.debug("Resizing width " + imageLabel);
					ImageView image = (ImageView) activeStage.getScene().lookup(imageLabel);
					image.setFitWidth(itemWidth);
					double actualWidth = image.getBoundsInParent().getWidth();
					image.setLayoutX((itemWidth - actualWidth) / 2); // center horizontally

					String foregroundImageLabel = "#foreground" + index;
					ImageView foregroundImage = (ImageView) activeStage.getScene().lookup(foregroundImageLabel);
					if (foregroundImage != null) {
						foregroundImage.setLayoutX(image.getLayoutX());
						foregroundImage.setFitWidth(actualWidth);
						logger.debug("Repositioning foreground horizontally " + foregroundImageLabel + "--> width="
								+ actualWidth + ",X=" + image.getLayoutX());
					}
					String needleLabel = "#needle" + index;
					Line needle = (Line) activeStage.getScene().lookup(needleLabel);

					Pane pane = (Pane) activeStage.getScene().lookup(paneLabel);
					pane.setLayoutX(i * itemWidth);
					pane.setPrefWidth(itemWidth);

					Rectangle background = (Rectangle) activeStage.getScene().lookup("#background");
					background.setWidth(activeStage.getWidth());

					/*
					 * if (activeStage != undecoratedStage)
					 * //undecoratedStage.setWidth(activeStage.getScene().getWidth()); ((Pane)
					 * undecoratedStage.getScene().getRoot())
					 * .setPrefWidth(activeStage.getScene().getWidth());
					 */
					logger.debug("New width: " + activeStage.getScene().getWidth());

					/*
					 * pane.getChildren().clear(); if (foregroundImage != null)
					 * pane.getChildren().addAll(image, needle, foregroundImage); else
					 * pane.getChildren().addAll(image, needle);
					 */
				} catch (Exception e) {
					logger.error("Failed to resizeItemsWidth " + i, e);
				}
			}
		}
	}

	public void logPositions(String message) {
		logPositions(decoratedStage, "  decorated " + message);
		logPositions(undecoratedStage, "undecorated " + message);
	}

	public void logPositions(Stage stage, String stageString) {
		logger.debug("Postion of stage " + stageString + ": height=" + stage.getHeight() + ",width=" + stage.getWidth()
				+ ",X=" + stage.getX() + ",Y=" + stage.getY());
	}

	public void swapDecorations() {
		// alternative cf.
		// https://stackoverflow.com/questions/40773411/what-prevents-changing-primarystage-initstyle-in-javafx

		// double decorationHeight = decoratedStage.getHeight() -
		// decoratedStage.getScene().getHeight();

		topBarVisible = !topBarVisible;
		if (topBarVisible) {
			((Pane) decoratedStage.getScene().getRoot()).setPrefHeight(decoratedStage.getScene().getHeight());
			((Pane) decoratedStage.getScene().getRoot()).setPrefWidth(decoratedStage.getScene().getWidth());
			activeStage.hide();
			activeStage = decoratedStage;
		} else {
			logPositions("before");
			double decorationWidth = decoratedStage.getWidth() - decoratedStage.getScene().getWidth();
			((Pane) undecoratedStage.getScene().getRoot()).setPrefHeight(decoratedStage.getHeight() - 6); // 5-6 pixels
																											// missing???
			((Pane) undecoratedStage.getScene().getRoot()).setPrefWidth(decoratedStage.getScene().getWidth());
			undecoratedStage.setX(decoratedStage.getX() + decorationWidth / 2);
			undecoratedStage.setY(decoratedStage.getY());

			activeStage.hide();
			activeStage = undecoratedStage;
			logPositions("after ");
		}

		activeStage.show();
		resizeItems();
		resizeItems();
	}

	public synchronized void handleMouseEvent(MouseEvent event) {
		logger.debug("mouse click detected! " + event.getSource());

		switch (event.getButton()) {
		case MouseButton.PRIMARY:
			logger.debug(" PRIMARY mouse click detected! " + event.getSource());
			swapDecorations();
			logger.debug("mouse clicked completed " + event.getSource());

			break;
		case MouseButton.SECONDARY:
			logger.debug("SECONDARY mouse click detected! " + event.getSource());
			break;
		case MouseButton.MIDDLE:
			logger.debug("MIDDLE mouse click detected! " + event.getSource());
			break;

		case MouseButton.NONE:
		default:
			logger.error("Unexpected button type:" + event.getButton());
		}
	}

	public void shutdown() {// do nothing
		logger.debug("Shutdown complete");
	}

	// display and calculations
	public synchronized void display(double[] amplitude, int channels) {

		if (activeStage == null || activeStage.getScene() == null) {
			logger.debug("not yet ready to display..., rootStage=" + activeStage + ", scene is not accessible.");
			return;
		}

		double[] intertialAmplitude = makeInertial(amplitude);

		for (int i = 0; i < intertialAmplitude.length; i++) {

			String needleLabel = "#needle" + i;
			String imageLabel = "#image" + i;
			Line needle = (Line) activeStage.getScene().lookup(needleLabel);
			ImageView image = (ImageView) activeStage.getScene().lookup(imageLabel);

			if (needle == null) {
				logger.debug("Missing:" + needleLabel);
				return;
			}
			if (image == null) {
				logger.debug("Missing:" + imageLabel);
				return;
			}

			double[] segment = resizeSegementToImage(calculateSegment(intertialAmplitude[i], 128), image);

			if (segment != null) {

				Platform.runLater(() -> {
					needle.setStartX(segment[0]);
					needle.setStartY(segment[1]);
					needle.setEndX(segment[2]);
					needle.setEndY(segment[3]);
					needle.setVisible(true);
				});
			} else {
				needle.setVisible(false);
			}
		}
	}

	double[] makeInertial(double[] amplitude) {
		double[] intertialAmplitude = new double[amplitude.length];

		for (int i = 0; i < intertialAmplitude.length; i++) {
			if (amplitude[i] > previousAmplitude[i] + getParamaters().getMaxUpSpeed()) {
				intertialAmplitude[i] = previousAmplitude[i] + getParamaters().getMaxUpSpeed();
			} else if (amplitude[i] < previousAmplitude[i] - getParamaters().getMaxDownSpeed()) {
				intertialAmplitude[i] = previousAmplitude[i] - getParamaters().getMaxDownSpeed();
			} else {
				intertialAmplitude[i] = amplitude[i];
			}
		}
		previousAmplitude = intertialAmplitude;
		return intertialAmplitude;
	}

	public static double[] calculateSegment(double amplitude, int maxAmplitude) {
		double[] coordinates = new double[4];

		double mu = getParamaters().getMaxAmplitudeAngle();
		double alpha = getParamaters().getMinAngle() + amplitude * mu / (double) maxAmplitude;

		double X = getParamaters().getxC() + Math.cos(alpha) * getParamaters().getNeedleLength();
		double Y = getParamaters().getyC() + Math.sin(alpha) * getParamaters().getNeedleLength();

		double[] segment = new double[] { getParamaters().getxC(), getParamaters().getyC(), X, Y };
		double[] rectangle = new double[] { 0, 0, getParamaters().getReferenceWidth() - 1,
				getParamaters().getReferenceHeight() - 1 }; // -1: make sure there is not one dot outside borders

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
		double actualWidth = image.getBoundsInParent().getWidth();
		double actualHeight = image.getBoundsInParent().getHeight();
		double horizontalOffset = image.getLayoutX() + (image.getFitWidth() - actualWidth) / 2;
		double verticalOffset = image.getLayoutY() + (image.getFitHeight() - actualHeight) / 2;

		horizontalOffset = image.getBoundsInParent().getMinX();
		verticalOffset = image.getBoundsInParent().getMinY();
		// X's
		segment[0] = horizontalOffset + (coordinates[0] * actualWidth / (double) getParamaters().getReferenceWidth());
		segment[2] = horizontalOffset + (coordinates[2] * actualWidth / (double) getParamaters().getReferenceWidth());
		// Ys
		segment[1] = verticalOffset + (coordinates[1] * actualHeight / (double) getParamaters().getReferenceHeight())
				- 1;
		segment[3] = verticalOffset + (coordinates[3] * actualHeight / (double) getParamaters().getReferenceHeight())
				- 1;

		/*
		 * // X's segment[0] = (coordinates[0] * (double) image.getFitWidth() / (double)
		 * getParamaters().getReferenceWidth()); segment[2] = (coordinates[2] * (double)
		 * image.getFitWidth() / (double) getParamaters().getReferenceWidth()); // Ys
		 * segment[1] = (coordinates[1] * (double) image.getFitHeight() / (double)
		 * getParamaters().getReferenceHeight()); segment[3] = (coordinates[3] *
		 * (double) image.getFitHeight() / (double)
		 * getParamaters().getReferenceHeight());
		 */
		/*
		 * System.out.println("Resize ("+coordinates[0]+","+coordinates[1]+"),("+
		 * coordinates[2]+","+coordinates[3]+") with FitWidth="+image.getFitWidth()+
		 * ",ReferenceWidth="+getParamaters().getReferenceWidth()
		 * +",FitHeight="+image.getFitHeight()+",ReferenceHeight="+getParamaters().
		 * getReferenceHeight());
		 */
		return segment;

	}

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

	// following methods unused
	public void displayLR(double[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	public void displayLRasNumber(double[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	public void displayLRasNumbers(double[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	public void displayAsNumber(double[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	public void displayAsNumbers(double[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}
}
