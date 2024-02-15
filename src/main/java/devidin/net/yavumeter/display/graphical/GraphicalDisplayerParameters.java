package devidin.net.yavumeter.display.graphical;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.configuration.Configuration;

//import javafx.scene.image.Image;
public class GraphicalDisplayerParameters extends GraphicalDisplayerConfiguration {
	private double minAngle;
	private double maxAngle;
	private double needleAngle;
	private int yMax=-1;
	private static Logger logger = null;
	//private InputStream backgroundImageStream = null;
	//private File imageFile = null;
	private BufferedImage bufferedImage = null;

	public static GraphicalDisplayerParameters LoadConfiguration() {
		GraphicalDisplayerParameters parameters = null;

		try {
			parameters = (GraphicalDisplayerParameters) Configuration
					.loadConfiguration(GraphicalDisplayerParameters.class, "GraphicalDisplayerConfiguration.yml");
		} catch (Exception e) {
		    Throwable cause = e.getCause();
		    cause.printStackTrace();
			logger.error(e.toString());
		    logger.error(cause.getStackTrace().toString());
			logger.info("Setting to defaults");

			parameters = new GraphicalDisplayerParameters();
		}
		parameters.setCalculatedParameters();
		return parameters;
	}

	public String toString() {
		return super.toString() + "min angle:" + minAngle + ", max angle:" + maxAngle + ", width:"+getWidth()+", height:"+getHeight() ;
	}

	public GraphicalDisplayerParameters() {
		super();
		if (logger == null)
			logger = LoggerFactory.getLogger(GraphicalDisplayerParameters.class);
		//setCalculatedParameters();
	}

	double getMinAngle() {
		return minAngle;
	}

	void setMinAngle() {
		minAngle = Math.acos((getxMin() - getxC()) / getNeedleLength());
	}

	double getMaxAnbgle() {
		return maxAngle;
	}

	void setMaxAngle() {
		maxAngle = Math.acos((getxMax() - getxC()) / getNeedleLength());
	}

	int getyMax() {
		return yMax;
	}

	void setyMax() {
		this.yMax = (int) (getyC() + getNeedleLength() * Math.sin(maxAngle));
	}

	void setCalculatedParameters() {
		setMinAngle();
		setMaxAngle();
		setyMax();
		//setBackgroundImageStream();
		setBufferedImage();
	}
/*
	private InputStream getBackgroundImageStream() {
		return backgroundImageStream;
	}

	private void setBackgroundImageStream() {
		try {
			this.backgroundImageStream = new FileInputStream(getFileName());
		} catch (FileNotFoundException e) {
			this.backgroundImageStream = null;
			logger.error("Background image file not found:" + getFileName() + ":"+e.getMessage());
		}
	}
*/
	@SuppressWarnings("unused")
	private int getWidth() {
		return getBufferedImage().getWidth();
	}

	@SuppressWarnings("unused")
	private int getHeight() {
		return getBufferedImage().getHeight();
	}

	private BufferedImage getBufferedImage() {
		if (bufferedImage == null)
			setBufferedImage();
		return bufferedImage;
	}

	private void setBufferedImage() {
		try {
			//File imageFile = new File(getFileName());
			//File imageFile = Configuration.readFileFromClassPath(getFileName());
			//bufferedImage = ImageIO.read(imageFile);
			URL resourceUrl = getClass().getClassLoader().getResource(getFileName());
			logger.info( "Image ressource file URL:"+resourceUrl);
			bufferedImage = ImageIO.read(resourceUrl);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to read image file: " + getFileName() + ":" + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private double getNeedleAngle() {
		return maxAngle - minAngle;
	}

}