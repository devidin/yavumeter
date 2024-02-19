package devidin.net.yavumeter.display.graphical;

import java.awt.image.BufferedImage;
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

	public double getMinAngle() {
		return minAngle;
	}

	void setMinAngle() {
		//minAngle = Math.acos((getxMin() - getxC()) / getNeedleLength());
		minAngle = Math.atan2(getyMin()-getyC(),getxMin()-getxC() );

		//if (minAngle<0) minAngle+=Math.PI*2;
		System.out.println("setMinAngle: xC="+getxC()+",yC=" +getyC()+",xMin=" +getxMin()+",yMin=" +getyMin(
				)+",needleLength="+ getNeedleLength()+"-->"+minAngle);
	}

	public double getMaxAngle() {
		return maxAngle;
	}

	void setMaxAngle() {
		maxAngle = Math.atan2(getyMax()-getyC(),getxMax()-getxC());
		//if (maxAngle<0) maxAngle+=Math.PI*2;

		System.out.println("setMaxAngle: xC="+getxC()+",yC=" +getyC()+",xMax=" +getxMax()+",yMax=" +getyMax()
		+",needleLength="+ getNeedleLength()+"-->"+maxAngle);
	}

	public double getMaxAmplitudeAngle() {
		double result = maxAngle-minAngle;
		
		return result;
	}

	void setCalculatedParameters() {
		setMinAngle();
		setMaxAngle();
		setBufferedImage();
	}
	
	private int getWidth() {
		return getBufferedImage().getWidth();
	}

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

}
