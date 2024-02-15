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
			Mixer mixer = AudioSystem.getMixer(mixersInfos[mixerId]);
			logger.info("Monitoring mixer: " + mixersInfos[mixerId]);

			// Get the selected line from the mixer
			Line.Info[] lineInfos = mixer.getSourceLineInfo();
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
			System.out.println("Target data Line: " + targetDataLine.getLineInfo());
			System.out.println("Buffer size     : " + targetDataLine.getBufferSize());

			byte[] buffer = new byte[(int) getConfiguration().getBufferSize()];
			AudioInputStream ais = new AudioInputStream(targetDataLine);

			int[] amplitude;
			String displayerClassName = getConfiguration().getDisplayerClass();
			logger.debug("Displayer class:" + displayerClassName);

			displayer = (Displayer) Class.forName(displayerClassName).getDeclaredConstructor().newInstance();

			displayer.init();

			
			/*
			 * Main monitoring loop here
			 */
			while (true) {
				if (Thread.interrupted()) {
					break;
				}
				int b = ais.read(buffer);
				amplitude = SoundCardHelper.calculateAmplitudeRMS(buffer, b, format.getChannels());
				displayer.display(amplitude, format.getChannels());
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
