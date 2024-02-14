package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;

import devidin.net.yavumeter.configuration.Configuration;
import org.slf4j.LoggerFactory;

public class GraphicalDisplayerConfiguration extends Configuration {
	private static Logger logger = null;

	private String background;
	private String color;
	private String fileType;
	private long xC;
	private long yC;
	private long xMin;
	private long yMin;
	private long xMax;
	private long needleRed;
	private long needleGreen;
	private long needleBlue;

	// following parameters are not configurable (calculated with setters)
	private String fileName;
	private long needleLength;

	public String toString() {
		return "file:"+fileName
				+", C("+xC+","+yC+")"
				+", min("+xMin+","+yMin+")"
				+", max("+xMax+",*)"
				+", needleColorRGB("+needleRed+","+needleGreen+","+needleBlue+")"
				+", needleLength :"+needleLength
				+"["
				+super.toString()
				+"]";
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
		this.needleLength = (long) Math.sqrt((xC-xMin)*(xC-xMin)+(yC-yMin)*(yC-yMin));
	}


	public String getFileName() {

		if (fileName != null) return fileName;
		setFileName();

		return fileName;
	}

	private void setFileName() {
		
		if (background==null || fileType== null)
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

		setNeedleBlue(128);
		setNeedleGreen(128);
		setNeedleRed(128);

		setxC(-1);
		setyC(-1);

		setxMin(-1);
		setyMin(-1);

		setxMax(-1);
		
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
					+ configuration.getFileName() );
		}
		return configuration;

	}

	public Configuration loadConfiguration() {
		return LoadConfiguration();
	}

}
