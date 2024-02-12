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
import devidin.net.yavumeter.display.console.ConsoleDisplayer;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;

public class VUmeterDisplayer {
    private static final Logger logger = LoggerFactory.getLogger(VUmeterDisplayer.class);
    
	public static void monitor() {
        //SoundCardHelper.audioLevelMonitor(6, 0);
		int mixerId=6; // TODO: configurable
		int lineId=0;  // TODO: configurable
		
		TargetDataLine targetDataLine = null;
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
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
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

			byte[] buffer = new byte[1024]; // TODO: make configurable
			AudioInputStream ais = new AudioInputStream(targetDataLine);

			System.out.println("Output Level:");
			int[] amplitudeLR;
			Displayer displayer = new ConsoleDisplayer();
			while (true) {

				int b = ais.read(buffer);
				amplitudeLR = SoundCardHelper.calculateAmplitudeRMSLR(buffer, b); // TODO: make configurable: RMS / average

				displayer.displayLR(amplitudeLR);
				displayer.displayLRasNumbers(amplitudeLR); // TODO: make configurable: visualization method
				displayer.displayLRasNumber(amplitudeLR);
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

		}

		
	}

}
