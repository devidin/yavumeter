package devidin.net.yavumeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.configuration.Configuration;

public class VUmeterDisplayerConfiguration extends Configuration {
	private static Logger logger = null;

    private String displayerClass;
    private long bufferSize;
    private long intervalMs=15; //ms
    
    private String viewMode;
    private String loudnessMode;
    private String mixerName;
    
	public static final String LOG_VIEW="LOG"; 
	public static final String EXP_VIEW="EXP"; 
	public static final String LINEAR_VIEW="LINEAR"; 
	public static final String SQUARE_VIEW="SQUARE"; 
	public static final String SQUAREROOT_VIEW="SQUAREROOT"; 
	
	public static final String RMS_LOUDNESS="RMS"; 
	public static final String AVG_LOUDNESS="AVG"; 

    public String getDisplayerClass() { return displayerClass; }
    public void setDisplayerClass(String value) { this.displayerClass = value; }

    public long getBufferSize() { return bufferSize; }
    public void setBufferSize(long value) { this.bufferSize = value; }


	public VUmeterDisplayerConfiguration() {
		setDisplayerClass("devidin.net.yavumeter.display.ConsoleDisplayer");
		setBufferSize(64);
		setLoudnessMode(AVG_LOUDNESS);
		setViewMode(LINEAR_VIEW);
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
	public long getIntervalMs() {
		return intervalMs;
	}
	public void setIntervalMs(long value) {
		this.intervalMs = value;
	}
	public String getMixerName() {
		return mixerName;
	}
	public void setMixerName(String mixerName) {
		this.mixerName = mixerName;
	}

}

