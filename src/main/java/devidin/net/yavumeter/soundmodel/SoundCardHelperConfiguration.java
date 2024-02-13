package devidin.net.yavumeter.soundmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.configuration.Configuration;

public class SoundCardHelperConfiguration extends Configuration {
	private AudioFormat audioFormat;
	private long channels;

	private static Logger logger = null;
	private static SoundCardHelperConfiguration configuration = null;

	public SoundCardHelperConfiguration getConfiguration() {
		if (configuration==null) configuration=(SoundCardHelperConfiguration) SoundCardHelperConfiguration.LoadConfiguration();
		return configuration;
	}


	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public void setAudioFormat(AudioFormat value) {
		this.audioFormat = value;
	}

	public long getChannels() {
		return channels;
	}

	public void setChannels(long value) {
		this.channels = value;
	}

	public SoundCardHelperConfiguration() {

	}

	public static SoundCardHelperConfiguration LoadConfiguration() {
		if (logger == null)
			logger = LoggerFactory.getLogger(Configuration.class);

		try {
			return (SoundCardHelperConfiguration) Configuration.loadConfiguration(SoundCardHelperConfiguration.class,
					"SoundCardHelperConfiguration.yml");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.info("Setting to defaults");

			return new SoundCardHelperConfiguration();
		}

	}


}
