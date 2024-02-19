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
		minAngle = Math.acos((double)(getxMin()-getxC() ) / (double)getNeedleLength());
		
		if (getyC()<getyMin()) {
			minAngle=-minAngle; // rotate opposite direction
		} else if (getxMin()<getxC()) {
			//minAngle=Math.PI-minAngle;
		}
			
		/*
		if (getxC()<getxMin()) {
			minAngle=-minAngle; // rotate opposite direction
		}*/
		System.out.println("setMinAngle: xC="+getxC()+",yC=" +getyC()+",xMin=" +getxMin()+",yMin=" +getyMin(
				)+",needleLength="+ getNeedleLength()+"-->"+minAngle);
	}

	public double getMaxAngle() {
		return maxAngle;
	}

	void setMaxAngle() {
		//maxAngle = Math.acos((getxMax() - getxC()) / getNeedleLength());
		if (getxMax() != -1) {
			maxAngle = Math.acos((double)(getxMax() - getxC()) / (double)getNeedleLength());

			logger.warn("Both xMax and yMax were configured. yMax ignored.");
		} else {
			if (getyMax()==-1) {
				logger.warn("None of xMax nor yMax were configured. yMax set to yMin:"+getyMin());
				setyMax(getyMin());
			}
			maxAngle = Math.asin((double)(getyC()-getyMax() ) / (double)getNeedleLength());
		}
		if (getyC()<getyMin()) {
			maxAngle=-maxAngle; // rotate opposite direction
		} else if (getxMin()<getxC()) {
			maxAngle=Math.PI-maxAngle;
		}
		System.out.println("setMaxAngle: xC="+getxC()+",yC=" +getyC()+",xMax=" +getxMax()+",yMax=" +getyMax()
		+",needleLength="+ getNeedleLength()+"-->"+maxAngle);
	}

	public double getMaxAmplitudeAngle() {
		double result = maxAngle-minAngle;
		
//		if (result<0) result+= 2*Math.PI;
/*		if (result<0) result= -result;
 * 
 */
		return result;
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

}
