package devidin.net.yavumeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.configuration.Configuration;

public class VUmeterDisplayerConfiguration extends Configuration {
	private static Logger logger = null;

    private String displayerClass;
    private long bufferSize;
    private long mixerID;
    private long lineID;
    private String viewMode;
    private String loudnessMode;
    
	public static final String LOG_VIEW="LOG"; 
	public static final String LINEAR_VIEW="LINEAR"; 
	public static final String EXP_VIEW="EXP"; 
	public static final String SQUARE_VIEW="SQUARE"; 
	
	public static final String RMS_LOUDNESS="RMS"; 
	public static final String AVG_LOUDNESS="AVG"; 

    public String getDisplayerClass() { return displayerClass; }
    public void setDisplayerClass(String value) { this.displayerClass = value; }

    public long getBufferSize() { return bufferSize; }
    public void setBufferSize(long value) { this.bufferSize = value; }

    public long getMixerID() { return mixerID; }
    public void setMixerID(long value) { this.mixerID = value; }

    public long getLineID() { return lineID; }
    public void setLineID(long value) { this.lineID = value; }
    

	public VUmeterDisplayerConfiguration() {
		setDisplayerClass("devidin.net.yavumeter.display.ConsoleDisplayer");
		setBufferSize(64);
		setLineID(0);
		setMixerID(4);
		setLoudnessMode(RMS_LOUDNESS);
		setViewMode(LOG_VIEW);
	}
	
	public static VUmeterDisplayerConfiguration loadConfiguration() {
		if (logger == null)
			logger = LoggerFactory.getLogger(Configuration.class);

		try {
			return (VUmeterDisplayerConfiguration) Configuration.loadConfiguration(VUmeterDisplayerConfiguration.class,
					"VUmeterDisplayerConfiguration.yml");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.info("Setting to defaults");
			
			return new VUmeterDisplayerConfiguration();
		}

	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String value) {
		this.viewMode = value;
	}

	public String getLoudnessMode() {
		return loudnessMode;
	}

	public void setLoudnessMode(String value) {
		this.loudnessMode = value;
	}
}

