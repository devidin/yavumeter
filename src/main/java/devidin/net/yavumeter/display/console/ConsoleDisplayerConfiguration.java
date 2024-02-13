package devidin.net.yavumeter.display.console;

import devidin.net.yavumeter.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleDisplayerConfiguration extends Configuration {
	private static Logger logger = null;
	private long width;
	private long maxAmplitude;
	private String displayMethod;

	public long getWidth() {
		return width;
	}

	public void setWidth(long value) {
		this.width = value;
	}

	public long getMaxAmplitude() {
		return maxAmplitude;
	}

	public void setMaxAmplitude(long value) {
		this.maxAmplitude = value;
	}

	public String getDisplayMethod() {
		/*
		 * One of: display, displayAsNumber, displayAsNumbers, displayLR, displayAsNumberLR, displayAsNumbersLR  
		 */
		return displayMethod;
	}

	public void setDisplayMethod(String value) {
		this.displayMethod = value;
	}

	public ConsoleDisplayerConfiguration() {
		setWidth(132);
		setMaxAmplitude(128);
		setDisplayMethod("display");
	}
	
	public static ConsoleDisplayerConfiguration LoadConfiguration() {
		if (logger == null)
			logger = LoggerFactory.getLogger(Configuration.class);

		try {
			return (ConsoleDisplayerConfiguration) Configuration.loadConfiguration(ConsoleDisplayerConfiguration.class,
					"ConsoleDisplayerConfiguration.yml");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.info("Setting to defaults");
			
			return new ConsoleDisplayerConfiguration();
		}

	}

	public Configuration loadConfiguration() {
		return LoadConfiguration();
	}
}
