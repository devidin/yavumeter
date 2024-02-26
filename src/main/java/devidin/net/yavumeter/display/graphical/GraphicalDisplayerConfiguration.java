package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;

import devidin.net.yavumeter.configuration.Configuration;
import org.slf4j.LoggerFactory;

public class GraphicalDisplayerConfiguration extends Configuration {
	private static Logger logger = LoggerFactory.getLogger(GraphicalDisplayerConfiguration.class);
	// base image
	private String background;
	private String color;
	private String fileType;
	private boolean preserveAspectRatio;
	// dimensions
	private long referenceWidth;// = 1000; // width of the reference image, not necessarily the actual one
	private long referenceHeight;// = 1000; // eight of the reference image, not necessarily the actual one
	private long xC; // needle origin center
	private long yC;
	private long xMin;       // needle end for minimum amplitude
	private long yMin;
	// only one of the 2 below should be configured - the other will be calculated
	private long xMax;      
	private long yMax;      
	//private long yMax; (Y calculated from needle length, max angle & origin)
	
	// colors
	private long needleRed;
	private long needleGreen;
	private long needleBlue;
	private long needleWidth;
	private boolean needleShadow;
	private long needleShadowOffsetX;
	private long needleShadowOffsetY;
		
	private long backgroundRed;
	private long backgroundGreen;
	private long backgroundBlue;

	// following parameters are not configurable (calculated with setters)
	private String fileName;
	private String foregroundFileName;
	private long needleLength=1; // avoid div / 0

	private double maxDownSpeed = 0.5; 
	private double maxUpSpeed = 5.0; 
	
	
	public String toString() {

		return "file:" + fileName + ", C(" + xC + "," + yC + ")" + ", min(" + xMin + "," + yMin + ")" + ", max(" + xMax
				+ ","+yMax+")" + ", needleColorRGB(" + needleRed + "," + needleGreen + "," + needleBlue + ")"
				+ ", needleLength :" + needleLength
				+ ", references (width="+getReferenceWidth()+", height="+getReferenceHeight();
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String value) { 
		this.background = value;
		setFileName();
	}

	public String getColor() {
		return color;
	}

	public void setColor(String value) {
		this.color = value;
		setFileName();
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String value) {
		this.fileType = value;
		setFileName();
	}

	public long getxC() {
		return xC;
	}

	public void setxC(long value) {
		this.xC = value;
		setNeedleLength();
	}

	public long getyC() {
		return yC;
	}

	public void setyC(long value) {
		this.yC = value;
		setNeedleLength();
	}

	public long getxMin() {
		return xMin;
	}

	public void setxMin(long value) {
		this.xMin = value;
		setNeedleLength();
	}

	public long getyMin() {
		return yMin;
	}

	public void setyMin(long value) {
		this.yMin = value;
		setNeedleLength();
	}

	public long getxMax() {
		return xMax;
	}

	public void setxMax(long value) {
		this.xMax = value;
		setNeedleLength();
	}


	public long getyMax() {
		return yMax;
	}


	public void setyMax(long value) {
		this.yMax = value;
		setNeedleLength();
	}


	public long getReferenceHeight() {
		return referenceHeight;
	}

	public void setReferenceHeight(long value) {
		this.referenceHeight = value;
	}

	public long getNeedleRed() {
		return needleRed;
	}

	public void setNeedleRed(long value) {
		this.needleRed = value;
	}

	public long getNeedleGreen() {
		return needleGreen;
	}

	public void setNeedleGreen(long value) {
		this.needleGreen = value;
	}

	public long getNeedleBlue() {
		return needleBlue;
	}

	public void setNeedleBlue(long value) {
		this.needleBlue = value;
	}

	public long getNeedleLength() {
		return needleLength;
	}

	public void setNeedleLength() {
		long minNeedleLength=(long) Math.sqrt((xC - xMin) * (xC - xMin) + (yC - yMin) * (yC - yMin));
		long maxNeedleLength=(long) Math.sqrt((xC - xMax) * (xC - xMax) + (yC - yMax) * (yC - yMax));
		this.needleLength=(minNeedleLength+maxNeedleLength)/2; // approximate = average length
	}

	public String getFileName() {

		if (fileName != null)
			return fileName;
		setFileName();

		return fileName;
	}

	private void setFileName() {

		if (background == null || fileType == null)
			this.fileName = null;
		else if (color == null)
			this.fileName = background + "." + fileType;
		else
			this.fileName = background + "." + color + "." + fileType;
	}

	public GraphicalDisplayerConfiguration() {

		setBackground("default");
		setColor(null);
		setFileType("jpg");

		setReferenceWidth(800);
		setReferenceHeight(600);

		setPreserveAspectRatio(true);
		
		setxC(400);
		setyC(800);

		setxMin(100);
		setyMin(100);

		setxMax(700);
		setyMax(100);

		setNeedleBlue(0);
		setNeedleGreen(255);
		setNeedleRed(0);
		setNeedleWidth(2);
		setNeedleShadow(true);

		setBackgroundBlue(128);
		setBackgroundGreen(128);
		setBackgroundRed(128);
		
		setNeedleShadowOffsetX(-6);
		setNeedleShadowOffsetY(-6);

	}

	public static GraphicalDisplayerConfiguration LoadConfiguration() {
		if (logger == null)
			logger = LoggerFactory.getLogger(Configuration.class);
		GraphicalDisplayerConfiguration configuration = null;
		try {
			configuration = (GraphicalDisplayerConfiguration) Configuration
					.loadConfiguration(GraphicalDisplayerConfiguration.class, "GraphicalDisplayerConfiguration.yml");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.info("Setting to defaults");

			configuration = new GraphicalDisplayerConfiguration();
		}

		configuration.setFileName();

		if (configuration.getBackground() == null || configuration.getFileType() == null) {
			logger.error("Background or type are not specified for background file. Using default :"
					+ configuration.getFileName());
		}
		return configuration;

	}

	public Configuration loadConfiguration() {
		return LoadConfiguration();
	}

	public long getReferenceWidth() {
		return referenceWidth;
	}

	public void setReferenceWidth(long value) {
		this.referenceWidth = value;
	}

	public long getNeedleWidth() {
		return needleWidth;
	}

	public void setNeedleWidth(long value) {
		this.needleWidth = value;
	}

	public boolean isNeedleShadow() {
		return needleShadow;
	}

	public void setNeedleShadow(boolean value) {
		this.needleShadow = value;
	}

	public String getForegroundFileName() {
		return foregroundFileName;
	}

	public void setForegroundFileName(String value) {
		this.foregroundFileName = value;
	}

	public long getBackgroundRed() {
		return backgroundRed;
	}

	public void setBackgroundRed(long value) {
		this.backgroundRed = value;
	}

	public long getBackgroundGreen() {
		return backgroundGreen;
	}

	public void setBackgroundGreen(long value) {
		this.backgroundGreen = value;
	}

	public long getBackgroundBlue() {
		return backgroundBlue;
	}

	public void setBackgroundBlue(long value) {
		this.backgroundBlue = value;
	}

	public long getNeedleShadowOffsetX() {
		return needleShadowOffsetX;
	}

	public void setNeedleShadowOffsetX(long needleShadowOffsetX) {
		this.needleShadowOffsetX = needleShadowOffsetX;
	}

	public long getNeedleShadowOffsetY() {
		return needleShadowOffsetY;
	}

	public void setNeedleShadowOffsetY(long needleShadowOffsetY) {
		this.needleShadowOffsetY = needleShadowOffsetY;
	}

	public boolean isPreserveAspectRatio() {
		return preserveAspectRatio;
	}

	public void setPreserveAspectRatio(boolean preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public double getMaxDownSpeed() {
		return maxDownSpeed;
	}

	public void setMaxDownSpeed(double maxDownSpeed) {
		this.maxDownSpeed = maxDownSpeed;
	}

	public double getMaxUpSpeed() {
		return maxUpSpeed;
	}

	public void setMaxUpSpeed(double maxUpSpeed) {
		this.maxUpSpeed = maxUpSpeed;
	}
}
