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
}

