package devidin.net.yavumeter.soundmodel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoundCardHelper {
	private static final Logger logger = LoggerFactory.getLogger(SoundCardHelper.class);

	public static Mixer.Info[] getMixersList() {
		return AudioSystem.getMixerInfo();
	}

	public static Line[] getMixersLines(Mixer mixer) {
		Line[] lines = mixer.getTargetLines();

		return lines;
	}

	public static void listMixers() {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		logger.info("Available Audio Mixers...");
		for (int m = 0; m < mixerInfos.length; m++) {
			listMixerLines(mixerInfos[m], m);
		}
	}

	public static void listMixerLines(Mixer.Info mixerInfo, int m) {
		logger.info("Mixer " + m + " : " + mixerInfo);
		Mixer mixer = AudioSystem.getMixer(mixerInfo);

		Line.Info[] lineInfos = mixer.getSourceLineInfo();
		listLines(lineInfos, "source");
		lineInfos = mixer.getTargetLineInfo();
		listLines(lineInfos, "target");
	}

	public static void listLines(Line.Info[] lineInfos, String type) {

		for (int l = 0; l < lineInfos.length; l++) {
			logger.info("      . " + type + " line " + l + ": " + lineInfos[l]);

			if (lineInfos[l] instanceof DataLine.Info) {
				DataLine.Info dataLineInfo = (DataLine.Info) lineInfos[l];
				AudioFormat[] formats = dataLineInfo.getFormats();

				for (AudioFormat format : formats) {
					logger.info("      .    Supported Audio Format:" + " channels: " + format.getChannels()
							+ " encoding: " + format.getEncoding() + " frameSize: " + format.getFrameSize()
							+ " sampleSizeInBits: " + format.getSampleSizeInBits() + " sampleRate: "
							+ format.getSampleRate() + " frameRate: " + format.getFrameRate());
				}
			}
		}
	}

	public static AudioFormat getAudioFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;

		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

		return format;

	}
	private static int calculateAmplitudeAvgAll(byte[] buffer, int bytesRead) {

		int totAmplitude = 0;
		for (int i = 0; i < bytesRead; i += 2) {
			int amplitude = Math.abs(buffer[i] & 0xFF);
			totAmplitude = totAmplitude + amplitude;
		}
		return totAmplitude / bytesRead;
	}

	private static int calculateAmplitudeRMSAll(byte[] buffer, int bytesRead) {
		/*
		double sumOfSquares = 0;
		for (int i = 0; i < bytesRead; i += 2) {
			int amplitude = Math.abs(buffer[i] & 0xFF);
			sumOfSquares = sumOfSquares + amplitude * amplitude;
		}
		return (int) Math.sqrt(sumOfSquares) / bytesRead;
		*/
		return calculateAmplitudeRMS(buffer, bytesRead, 1)[0];
	}

	private static int[] calculateAmplitudeAvgLR(byte[] buffer, int bytesRead) {
		int[] leftRight = new int[2];
		int totAmplitude = 0;
		for (int channel = 0; channel < 2; channel++) {
			for (int i = 0; i < bytesRead; i += 2) {
				int amplitude = Math.abs(buffer[i + channel] & 0xFF); // left channel is the first byte (+0), right the
																		// second (+1)
				totAmplitude = totAmplitude + amplitude;
			}
			leftRight[channel] = Math.min(128, (int) totAmplitude / bytesRead);
		}
		return leftRight;
	}

	public static int[] calculateAmplitudeRMS(byte[] buffer, int bytesRead) {
/*		int[] leftRight = new int[2];
		for (int channel = 0; channel < 2; channel++) {
			double sumOfSquares = 0;
			for (int i = 0; i < bytesRead; i += 2) {
				int amplitude = buffer[i + channel] & 0xFF;// left channel is the first byte (+0), right the second (+1)
				sumOfSquares = sumOfSquares + amplitude * amplitude;
			}
			leftRight[channel] = Math.max(0, Math.min(128, (int) Math.sqrt(sumOfSquares / bytesRead) - 30)); 
		}
		return leftRight;*/
		return calculateAmplitudeRMS(buffer, bytesRead, 2);
	}
	public static int[] calculateAmplitudeRMS(byte[] buffer, int bytesRead, int channels) {
		int[] amplitudes = new int[channels];
		for (int channel = 0; channel < channels; channel++) {
			double sumOfSquares = 0;
			for (int i = channel; i < bytesRead; i += channels) {
				int amplitude = buffer[i] & 0xFF;// left channel is the first byte (+0), right the second (+1)
				sumOfSquares = sumOfSquares + amplitude * amplitude;
			}
			amplitudes[channel] = Math.max(0, Math.min(128, (int) Math.sqrt((channels * sumOfSquares) / bytesRead ) -80  )); 
		}
		return amplitudes;
	}

}
