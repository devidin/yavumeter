package devidin.net.yavumeter.display.graphical;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.configuration.Configuration;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class GraphicalDisplayerParameters extends GraphicalDisplayerConfiguration {
	private double minAngle;
	private double maxAngle;
	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayerParameters.class);;
	private Image image = null;
	private Image foregroundImage = null;
	private Image logoImage = null;
	
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

		return super.toString() + "; min angle:" + minAngle + ", max angle:" + maxAngle + ", width:" + getWidth()
				+ ", height:" + getHeight() + ", Calculated Min("
				+ (getxC() + Math.cos(getMinAngle()) * getNeedleLength()) + ","
				+ (getyC() + Math.sin(getMinAngle()) * getNeedleLength()) + ")" + ", Calculated Max("
				+ (getxC() + Math.cos(getMaxAngle()) * getNeedleLength()) + ","
				+ (getyC() + Math.sin(getMaxAngle()) * getNeedleLength()) + ")";
	}

	public GraphicalDisplayerParameters() {
		super();
	}

	public double getMinAngle() {
		return minAngle;
	}

	void setMinAngle() {
		minAngle = Math.atan2(getyMin() - getyC(), getxMin() - getxC());
		/*
		 * if (minAngle < 0) { minAngle += 2 * Math.PI;
		 * logger.debug("minAngle negative. Set Positive:"+minAngle); }
		 */
		logger.debug("setMinAngle: xC=" + getxC() + ",yC=" + getyC() + ",xMin=" + getxMin() + ",yMin=" + getyMin()
				+ ",needleLength=" + getNeedleLength() + "-->" + minAngle);

		logger.debug("Calculated Min(" + (getxC() + Math.cos(getMinAngle()) * getNeedleLength()) + ","
				+ (getyC() + Math.sin(getMinAngle()) * getNeedleLength()) + ")");
	}

	public double getMaxAngle() {
		return maxAngle;
	}

	void setMaxAngle() {
		maxAngle = Math.atan2(getyMax() - getyC(), getxMax() - getxC());
		/*
		 * if (maxAngle < 0) { maxAngle += 2 * Math.PI;
		 * logger.debug("minAngle negative. Set Positive."+maxAngle); }
		 */
		logger.debug("setMaxAngle: xC=" + getxC() + ",yC=" + getyC() + ",xMax=" + getxMax() + ",yMax=" + getyMax()
				+ ",needleLength=" + getNeedleLength() + "-->" + maxAngle);
		logger.debug("Calculated Max(" + (getxC() + Math.cos(getMaxAngle()) * getNeedleLength()) + ","
				+ (getyC() + Math.sin(getMaxAngle()) * getNeedleLength()) + ")");
	}

	public double getMaxAmplitudeAngle() {
		double result = maxAngle - minAngle;
		

		if (result > Math.PI) { // wide angle > 180°
			logger.debug("Exceeded amplitude (+):" + result + ", " + result * 180 / Math.PI + "°");
			//result = Math.PI * 2 - result;
			result = -(Math.PI * 2 - result);
		} else if (result < -Math.PI) {
			logger.debug("Exceeded amplitude (-):" + result + ", " + result * 180 / Math.PI + "°");
			//result = -Math.PI * 2 - result;
			result = -(-Math.PI * 2 - result);
			}
		
		//logger.debug("Maximum amplitude=" + result + ", " + result * 180 / Math.PI + "°");
		return result;
	}

	void setCalculatedParameters() {
		setMinAngle();
		setMaxAngle();
		/*
		 * if (Math.abs(getMaxAmplitudeAngle()) > Math.PI) { swapAngles(); }
		 */
		setImage();
	}

	private double getWidth() {
		return getImage().getWidth();
	}

	private double getHeight() {
		return getImage().getHeight();
	}
	public Image getImage() {
		setImage();
		return image;
	}

	public void setImage() {
		if (image!=null) return;
		URL resourceUrl = getClass().getClassLoader().getResource(getFileName());
		logger.info("Image ressource file URL:" + resourceUrl);
		try {
			image=SwingFXUtils.toFXImage(ImageIO.read(resourceUrl), null);
		} catch (IOException e) {
			logger.error("Failed to load image "+getFileName(), e);
		}
		
	}
	
	public double getSurfaceRatio(double actualHeight,double actualWidth) {
		double refHeight = getReferenceHeight();
		double refWidth = getReferenceWidth();
		return Math.sqrt(actualHeight * actualHeight + actualWidth * actualWidth)
				/ Math.sqrt(refHeight * refHeight + refWidth * refWidth);
	}

	public void setForegroundImage() {
		if (foregroundImage!=null) return;
		URL resourceUrl = getClass().getClassLoader().getResource(getForegroundFileName());
		logger.info("Image ressource file URL:" + resourceUrl);
		try {
			foregroundImage=SwingFXUtils.toFXImage(ImageIO.read(resourceUrl), null);
		} catch (IOException e) {
			logger.error("Failed to load foreground image "+getForegroundFileName(), e);
		}
		
	}
	public Image getForegroundImage() {
		setForegroundImage();
		return foregroundImage;
	}

	public Image getLogoImage() {
		setLogoImage();
		return logoImage;
	}

	public void setLogoImage() {
		if (logoImage!=null) return;
		URL resourceUrl = getClass().getClassLoader().getResource(getLogoFileName());
		logger.info("Image ressource file URL:" + resourceUrl);
		try {
			logoImage=SwingFXUtils.toFXImage(ImageIO.read(resourceUrl), null);
		} catch (IOException e) {
			logger.error("Failed to load logo image "+getLogoFileName(), e);
		}	}
}
