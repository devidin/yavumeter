package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;

import devidin.net.yavumeter.configuration.Configuration;
import org.slf4j.LoggerFactory;

public class GraphicalDisplayerConfiguration extends Configuration {
	private static Logger logger = null;
	// base image
	private String background;
	private String color;
	private String fileType;
	// dimensions
	private long referenceWidth;// = 1000; // width of the reference image, not necessarily the actual one
	private long referenceHeight = 1000; // eight of the reference image, not necessarily the actual one
	private double referenceDiag = Math.sqrt(referenceWidth * referenceWidth + referenceHeight * referenceHeight);
	private long xC; // needle origin center
	private long yC;
	private long xMin; // needle end for minimum amplitude
	private long yMin;
	private long xMax; // needle end for maximum amplitude 
	//private long yMax; (Y calculated from needle length, max angle & origin)
	
	private long stuff;
	// colors
	private long needleRed;
	private long needleGreen;
	private long needleBlue;

	// following parameters are not configurable (calculated with setters)
	private String fileName;
	private long needleLength=1; // avoid div / 0

	public String toString() {

		return "file:" + fileName + ", C(" + xC + "," + yC + ")" + ", min(" + xMin + "," + yMin + ")" + ", max(" + xMax
				+ ",*)" + ", needleColorRGB(" + needleRed + "," + needleGreen + "," + needleBlue + ")"
				+ ", needleLength :" + needleLength
				+ ", references (width="+getReferenceWidth()+", height="+getReferenceHeight()+", Diag="+getReferenceDiag()+", )"
				+ ", normalized: C(" + getxC01() + "," + getyC01() + ")" + ", min("
				+ getxMin01() + "," + getyMin01() + ")" + ", max(" + getxMax01() + ",*)";
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String value) { // TODO only one is needed
		this.background = value;
		setFileName();
	}

	public void setbackground(String value) {// TODO only one is needed
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
	}

	public long getReferenceHeight() {
		return referenceHeight;
	}

	public void setReferenceHeight(long value) {
		this.referenceHeight = value;
		//setReferenceDiag();
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
		this.needleLength = (long) Math.sqrt((xC - xMin) * (xC - xMin) + (yC - yMin) * (yC - yMin));
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

		/*
		 * xC (default = width/2) yC (default = -height / 5) xMin (default = witdh/10)
		 * yMin (default = height/3) xMax (default = 9*width/10) yMax : not configurable
		 * needleRed = 0..255 (default = 0) needleGreen = 0..255 (default = 0)
		 * needleBlue = 0..255 (default = 0)
		 */

		setBackground("default");
		setColor(null);
		setFileType("jpg");

		setReferenceWidth(1000);
		setReferenceHeight(1000);

		setxC(500);
		setyC(1000);

		setxMin(100);
		setyMin(700);

		setxMax(900);

		setNeedleBlue(128);
		setNeedleGreen(128);
		setNeedleRed(128);

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

	public double getReferenceDiag() {
		return referenceDiag;
	}

	public void setReferenceDiag() {
		//this.referenceDiag = Math.sqrt(referenceWidth * referenceWidth + referenceHeight * referenceHeight);
		;
	}

	public double getxC01() {
		return ((double) getxC()) / getReferenceWidth();
	}

	public double getyC01() {
		return ((double) getyC()) / getReferenceHeight();
	}

	public double getxMin01() {
		return ((double) getxMin()) / getReferenceWidth();
	}

	public double getyMin01() {
		return ((double) getyMin()) / getReferenceHeight();
	}

	public double getxMax01() {
		return ((double) getxMax()) / getReferenceWidth();
	}


	public double getNeedleLength01() {
		return needleLength / getReferenceDiag();
	}

	public long getReferenceWidth() {
		return referenceWidth;
	}

	public void setReferenceWidth(long value) {
		this.referenceWidth = value;
	}

}
