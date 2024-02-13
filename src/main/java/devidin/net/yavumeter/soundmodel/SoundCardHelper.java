package devidin.net.yavumeter.soundmodel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.display.console.ConsoleDisplayerConfiguration;

public class SoundCardHelper {
	private static final Logger logger = LoggerFactory.getLogger(SoundCardHelper.class);
	private static final SoundCardHelperConfiguration configuration = SoundCardHelperConfiguration.LoadConfiguration();
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

	public static javax.sound.sampled.AudioFormat getAudioFormat() {
		float sampleRate = configuration.getAudioFormat().getSampleRate();
		int sampleSizeInBits = 8; // TODO : (int) configuration.getAudioFormat().getSampleSizeInBits(); only 8 bits supported for now
		int channels = (int) configuration.getAudioFormat().getChannels();
		boolean signed = configuration.getAudioFormat().getSigned();
		boolean bigEndian = configuration.getAudioFormat().getBigEndian();

		javax.sound.sampled.AudioFormat format = new javax.sound.sampled.AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

		return format;

	}

	private static int[] calculateAmplitudeAvg(byte[] buffer, int bytesRead, int channels) {
		int[] amplitudes = new int[channels];
		int totAmplitude = 0;
		for (int channel = 0; channel < channels; channel++) {
			for (int i = 0; i < bytesRead; i += 2) {
				int amplitude = Math.abs(buffer[i] & 0xFF);
				totAmplitude = totAmplitude + amplitude;
			}
			amplitudes[channel] = totAmplitude / bytesRead;
		}
		return amplitudes;
	}

	// same as above, but merge all channels as one
	private static int calculateAmplitudeAvg(byte[] buffer, int bytesRead) {
		return calculateAmplitudeAvg(buffer, bytesRead, 1) [0];
	}
	public static int[] calculateAmplitudeRMS(byte[] buffer, int bytesRead, int channels) {
		int[] amplitudes = new int[channels];
		for (int channel = 0; channel < channels; channel++) {
			double sumOfSquares = 0;
			for (int i = channel; i < bytesRead; i += channels) {
				int amplitude = buffer[i] & 0xFF;// left channel is the first byte (+0), right the second (+1)
				sumOfSquares = sumOfSquares + amplitude * amplitude;
			}
			amplitudes[channel] = Math.max(0,
					Math.min(128, (int) Math.sqrt((channels * sumOfSquares) / bytesRead) - 80));
		}
		return amplitudes;
	}

	// same as above, but merge all channels as one
	private static int calculateAmplitudeRMS(byte[] buffer, int bytesRead) {
		return calculateAmplitudeRMS(buffer, bytesRead, 1)[0];
	}

	public static int linearView(int amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - linear
		if (amplitude<0)
			return 0;
		else if (amplitude > max)
			return max;
		else
			return (int) ((int) Math.exp(((double) amplitude)) * (double) max / Math.exp((double) max));
	}
	
	public static int logarithmicView(int amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - logarithmic
		return linearView( (int) (Math.log((1 + (double) amplitude) * (double) max) / Math.log((double) max)), max);
	}

	public static int squareView(int amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - square
		return linearView((int) ((double) amplitude * (double) amplitude / (double) max), max);
	}

	public static int exponentialView(int amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - exponential
		return linearView((int) ((int) Math.exp(((double) amplitude)) * (double) max / Math.exp((double) max)), max);
	}
	

}
