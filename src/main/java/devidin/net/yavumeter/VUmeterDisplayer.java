package devidin.net.yavumeter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import java.io.ObjectInputStream.GetField;

import javax.sound.sampled.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.display.Displayer;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;

public class VUmeterDisplayer implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(VUmeterDisplayer.class);
	private static VUmeterDisplayerConfiguration configuration = null;

	@Override
	public void run() {
		monitor();
	}

	public VUmeterDisplayerConfiguration getConfiguration() {
		if (configuration == null)
			configuration = VUmeterDisplayerConfiguration.loadConfiguration();
		return configuration;
	}

	public void monitor() {
		logger.debug("Monitoring from VUmeterDisplayer");

		TargetDataLine targetDataLine = null;

		Displayer displayer = null;

		try {

			Line.Info selectedLineInfo;
			AudioFormat format = SoundCardHelper.getAudioFormat();
			logger.info("Selected Audio Format: " + format);
			
			// ========== get default line
			DataLine.Info defaultLineInfo = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(defaultLineInfo)) {
				logger.error("Unsupported format: " + format);
			}

			// ========== List accessible lines
			Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
			Mixer selectedMixer = null;

			for (Mixer.Info mixerInfo : mixerInfos) {
				selectedMixer = AudioSystem.getMixer(mixerInfo);
				targetDataLine = SoundCardHelper.findTargetDataLine(mixerInfo.getName(), defaultLineInfo);
				if (targetDataLine != null) {
					logger.info("Possible mixer:" + mixerInfo.getName());
				}
			}
			// ========== SPECIFIED default line
			try {
				targetDataLine = SoundCardHelper.findTargetDataLine(getConfiguration().getMixerName(), defaultLineInfo);
				targetDataLine.open(format);
				targetDataLine.start();
				logger.info("Successfully open specified target mixer's line:"+getConfiguration().getMixerName());

			} catch (Throwable t1) {
				logger.error("Could not open specified target mixer's line '"+getConfiguration().getMixerName()+"'. Trying default.", t1);
				
				if (targetDataLine!=null) {
					targetDataLine.stop();
					targetDataLine.close();
				}
				
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

				try {
					targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
					targetDataLine.open(format);
					targetDataLine.start();
				} catch (Throwable t2) {
					logger.error("Could not open any target line. Abort", t2);
					if (targetDataLine!=null) {
						targetDataLine.stop();
						targetDataLine.close();
					}
					System.exit(0);
				}
			}

			logger.info("Target data Line: " + targetDataLine.getLineInfo());
			byte[] buffer = new byte[(int) getConfiguration().getBufferSize()];
			AudioInputStream ais = new AudioInputStream(targetDataLine);
			logger.info("Buffer size     : " + buffer.length);

			double[] amplitude1; // 1 per channel
			double[] amplitude2 = new double[format.getChannels()]; // 1 per channel
			String displayerClassName = getConfiguration().getDisplayerClass();

			logger.info("Displayer class:" + displayerClassName);

			displayer = (Displayer) Class.forName(displayerClassName).getDeclaredConstructor().newInstance();
			displayer.init();

			/*
			 * Main monitoring loop here <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			 */
			while (true) {
				if (Thread.interrupted()) {
					break;
				}

				if (ais.available() < buffer.length) {
					Thread.sleep(getConfiguration().getIntervalMs());
					displayer.display(amplitude2, format.getChannels());

				} else {

					int b = ais.read(buffer);
					switch (configuration.getLoudnessMode()) {
					case VUmeterDisplayerConfiguration.AVG_LOUDNESS:
						amplitude1 = SoundCardHelper.calculateAmplitudeAVG(buffer, b, format.getChannels());
						break;
					case VUmeterDisplayerConfiguration.RMS_LOUDNESS:
						amplitude1 = SoundCardHelper.calculateAmplitudeRMS(buffer, b, format.getChannels());
						break;

					default:
						logger.error("Invalid loudness mode in configuration file: " + configuration.getLoudnessMode()
								+ "(expected : RMS, AVG)");
						amplitude1 = SoundCardHelper.calculateAmplitudeRMS(buffer, b, format.getChannels());
						break;
					}
					// logger.debug("1 -->"+amplitude1[0]+","+amplitude1[1]);

					switch (configuration.getViewMode()) {
					case VUmeterDisplayerConfiguration.SQUAREROOT_VIEW:
						amplitude2 = SoundCardHelper.squareRootView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.LOG_VIEW:
						amplitude2 = SoundCardHelper.logarithmicView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.EXP_VIEW:
						amplitude2 = SoundCardHelper.expView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.SQUARE_VIEW:
						amplitude2 = SoundCardHelper.squareView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.LINEAR_VIEW:
						amplitude2 = SoundCardHelper.linearView(amplitude1, 128);
						break;
					default:
						logger.error("Invalid view mode in configuration file: " + configuration.getLoudnessMode()
								+ "(expected : SQUAREROOT,SQUARE,LINEAR, LOG (,EXP?))");
						amplitude2 = SoundCardHelper.linearView(amplitude1, 128);
						break;
					}

					// logger.debug("2 -->"+amplitude2[0]+","+amplitude2[1]);

					displayer.display(amplitude2, format.getChannels());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception caused termination: " + e);
		} finally {
			logger.info("Closing line...");

			if (targetDataLine != null) {
				targetDataLine.stop();
				targetDataLine.close();
			}

			if (displayer != null)
				displayer.shutdown();
		}

	}

}
