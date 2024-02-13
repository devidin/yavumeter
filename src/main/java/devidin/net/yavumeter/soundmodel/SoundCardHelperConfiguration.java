package devidin.net.yavumeter.soundmodel;

import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.configuration.Configuration;
import devidin.net.yavumeter.display.console.ConsoleDisplayerConfiguration;

public class SoundCardHelperConfiguration {
    private AudioFormat audioFormat;
    private long channels;

    public AudioFormat getAudioFormat() { return audioFormat; }
    public void setAudioFormat(AudioFormat value) { this.audioFormat = value; }

    public long getChannels() { return channels; }
    public void setChannels(long value) { this.channels = value; }
	public SoundCardHelperConfiguration() {
		// TODO Auto-generated constructor stub
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

}
