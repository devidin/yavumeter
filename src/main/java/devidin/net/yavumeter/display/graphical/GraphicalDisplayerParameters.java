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
	private static Logger logger = null;
	private BufferedImage bufferedImage = null;

	public static GraphicalDisplayerParameters LoadConfiguration() {
		GraphicalDisplayerParameters parameters = null;

		try {
			parameters = (GraphicalDisplayerParameters) Configuration
					.loadConfiguration(GraphicalDisplayerParameters.class, "GraphicalDisplayerConfiguration.yml");
		} catch (Exception e) {
		    
		    e.printStackTrace();
			logger.error(e.toString());
		    logger.error(e.getStackTrace().toString());
			logger.info("Setting to defaults");

			parameters = new GraphicalDisplayerParameters();
		}
		parameters.setCalculatedParameters();
		return parameters;
	}

	public String toString() {
		return super.toString() + ", min angle:" + minAngle + ", max angle:" + maxAngle + ", width:"+getWidth()+", height:"+getHeight() ;
	}

	public GraphicalDisplayerParameters() {
		super();
		try {
			if (logger == null)
				logger = LoggerFactory.getLogger(GraphicalDisplayerParameters.class);
			//setCalculatedParameters();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	double getMinAngle() {
		return minAngle;
	}

	void setMinAngle() {
		//minAngle = Math.acos((getxMin() - getxC()) / getNeedleLength());
		minAngle = Math.acos((double)(getxMin()-getxC() ) / (double)getNeedleLength());
		System.out.println("setMinAngle:" +getxMin()+"," +getxC()+","+ getNeedleLength()+"-->"+minAngle);
	}

	double getMaxAngle() {
		return maxAngle;
	}

	void setMaxAngle() {
		//maxAngle = Math.acos((getxMax() - getxC()) / getNeedleLength());
		maxAngle = Math.acos((double)(getxMax() - getxC()) / (double)getNeedleLength());
		System.out.println("setMaxAngle:" +getxMax()+"," +getxC()+","+ getNeedleLength()+"-->"+maxAngle);
	}

	long getyMax() {
		return (long) (getyC() + getNeedleLength() * Math.sin(maxAngle));
	}

	void setCalculatedParameters() {
		setMinAngle();
		setMaxAngle();
		setBufferedImage();
	}

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
			URL resourceUrl = getClass().getClassLoader().getResource(getFileName());
			logger.info( "Image ressource file URL:"+resourceUrl);
			bufferedImage = ImageIO.read(resourceUrl);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to read image file: " + getFileName() + ":" + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private double getAngleRange() {
		return maxAngle - minAngle;
	}

}
