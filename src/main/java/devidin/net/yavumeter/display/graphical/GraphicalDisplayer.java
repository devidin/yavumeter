package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.System;

import devidin.net.yavumeter.YAvumeterFX;
import devidin.net.yavumeter.display.Displayer;
import javafx.stage.Stage;

public class GraphicalDisplayer extends Displayer {
	private static GraphicalDisplayerParameters configuration = null;
	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayer.class);
	private static Stage stage = null;
	private static YAvumeterFX root=new YAvumeterFX(); // just to access static methods
	
	public GraphicalDisplayerParameters getConfiguration() {
		if (configuration == null)
			configuration = GraphicalDisplayerParameters.LoadConfiguration();
		return configuration;
	}

	public void init() {
		logger.debug("Loading configuration...");
		getConfiguration();
		logger.info("Configuration loaded:" + getConfiguration());

		try {
			Thread.sleep(2000);
			root.changeScene("/GraphicalDisplayer.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    // Calculate the intersection point of two line segments
    public static double[] calculateIntersection(double x1, double y1, double x2, double y2,
                                                 double x3, double y3, double x4, double y4) {
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

}
