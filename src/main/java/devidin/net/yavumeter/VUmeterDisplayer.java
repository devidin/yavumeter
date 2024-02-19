package devidin.net.yavumeter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

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

		int mixerId = (int) getConfiguration().getMixerID();
		int lineId = (int) getConfiguration().getLineID();

		// TODO: if either of the above is -1, select default input

		TargetDataLine targetDataLine = null;

		Displayer displayer = null;

		try {
			// Get the selected audio mixer
			Mixer.Info[] mixersInfos = SoundCardHelper.getMixersList();
			logger.info("Start monitoring mixer #" + mixerId);
			Mixer mixer = AudioSystem.getMixer(mixersInfos[mixerId]);
			logger.info("Monitoring mixer: (" + mixerId + ")" + mixersInfos[mixerId]);

			// Get the selected line from the mixer
			Line.Info[] lineInfos = mixer.getSourceLineInfo();
			logger.info("Start monitoring line #" + lineId);
			Line.Info lineInfo = lineInfos[lineId];
			Line line = mixer.getLine(lineInfo);
			logger.info("Monitoring Line: " + lineInfos[lineId]);

			AudioFormat format = SoundCardHelper.getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			logger.info("Selected Audio Format: " + format);

			info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Unsupported format: " + format);
			}
			// Obtain and open the line.
			try {
				targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
				targetDataLine.open(format);
			} catch (LineUnavailableException ex) {
				System.out.println("Line unavailable: " + line.toString());
			}
			targetDataLine.start();

			logger.info("Target data Line: " + targetDataLine.getLineInfo());
			byte[] buffer = new byte[(int) getConfiguration().getBufferSize()];
			AudioInputStream ais = new AudioInputStream(targetDataLine);
			logger.info("Buffer size     : " + buffer.length);

			double[] amplitude1; // 1 per channel
			double[] amplitude2=new double[format.getChannels()]; // 1 per channel
			String displayerClassName = getConfiguration().getDisplayerClass();

			logger.debug("Displayer class:" + displayerClassName);

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
					Thread.sleep(20); // TODO make configurable
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
					System.out.println("1 -->"+amplitude1[0]+","+amplitude1[1]);

					switch (configuration.getViewMode()) {
					case VUmeterDisplayerConfiguration.SQUAREROOT_VIEW:
						amplitude2 = SoundCardHelper.squareRootView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.LOG_VIEW:
						amplitude2 = SoundCardHelper.logarithmicView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.SQUARE_VIEW:
						amplitude2 = SoundCardHelper.squareView(amplitude1, 128);
						break;
					case VUmeterDisplayerConfiguration.LINEAR_VIEW:
						amplitude2 = SoundCardHelper.linearView(amplitude1, 128);
						break;
					default:
						logger.error("Invalid view mode in configuration file: " + configuration.getLoudnessMode()
								+ "(expected : LOG,LINEAR,SQUAREROOT,SQUARE)");
						amplitude2 = SoundCardHelper.squareView(amplitude1, 128);
						break;
					}

					System.out.println("2 -->"+amplitude2[0]+","+amplitude2[1]);
					
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
