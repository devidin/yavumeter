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
	private static final SoundCardHelperConfiguration configuration = SoundCardHelperConfiguration.LoadConfiguration();
	//private static int noiseLevel=1;
	
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
		boolean signed = true;// TODO : configuration.getAudioFormat().getSigned(); only signed supported for now
		boolean bigEndian = true; // TODO : configuration.getAudioFormat().getBigEndian(); only bigEndian supported for now

		javax.sound.sampled.AudioFormat format = new javax.sound.sampled.AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

		return format;

	}
	
	public static double removeNoise(byte sample)
	{
/*		short sample = (short) (amplitude & 0xFF);
		int sample2=sample;
*/		
		byte sample2;
		if (sample<0) 
			sample2 = (byte) -sample;
		else sample2= sample;
		
		if (sample2<=configuration.getNoiseLevel()) 
			return 0;
		else {
			//return (double) sample2* ((double) 128/(128-noiseLevel));
			return (double) (sample2-configuration.getNoiseLevel())* ((double) 128/(128-configuration.getNoiseLevel()));
		}
	}
	public static double[] calculateAmplitudeAVG(byte[] buffer, int bytesRead, int channels) {
		final double integralFactor=Math.PI/2*6;

		
		double[] amplitudes = new double[channels];
		for (int channel = 0; channel < channels; channel++) {
			double totAmplitude = 0;
			for (int i = channel; i < bytesRead; i += 2) {
				double amplitude = removeNoise(buffer[i]);
				
//				testAmplitude(amplitude);
				totAmplitude = totAmplitude + amplitude;
			}
			amplitudes[channel] = integralFactor*totAmplitude / bytesRead;
		}
		return amplitudes;
	}
	public static double calculateAmplitudeAVG(byte[] buffer, int bytesRead) {
		return calculateAmplitudeAVG(buffer, bytesRead, 1) [0];
	}
	public static double[] calculateAmplitudeRMS(byte[] buffer, int bytesRead, int channels) {
		final double integralFactor=3;
		
		double[] amplitudes = new double[channels];
		for (int channel = 0; channel < channels; channel++) {
			double sumOfSquares = 0;
			for (int i = channel; i < bytesRead; i += channels) {
				double amplitude = removeNoise(buffer[i]);// left channel is the first byte (+0), right the second (+1)
//				testAmplitude(amplitude);
				sumOfSquares = sumOfSquares + amplitude * amplitude;
			}
			amplitudes[channel] = Math.max(0,
					Math.min(128, integralFactor*Math.sqrt(((double) channels * sumOfSquares) / (double) bytesRead) )); //- 80));
		}
		return amplitudes;
	}
	private static double calculateAmplitudeRMS(byte[] buffer, int bytesRead) {
		return calculateAmplitudeRMS(buffer, bytesRead, 1)[0];
	}
	//--------------------
	public static double linearView(double amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - linear
		if (amplitude<0)
			return 0;
		else if (amplitude > max)
			return max;
		else
			return amplitude;
	}
	public static double logarithmicView(double amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - logarithmic
		return linearView( (Math.log((1 + amplitude)) * (double) max / Math.log((double) max)), max);
	}
	public static double expView(double amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - logarithmic
		return expView( (Math.exp((amplitude)) * (double) max / Math.exp((double) max)), max);
	}

	public static double squareView(double amplitude, int max) {
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - square
		return linearView((amplitude * amplitude / (double) max), max);
	}
	public static double squareRootView(double amplitude, int max) { // not applicable
		// amplitude: -inf .. +inf - linear
		// returns : 0..max - exponential
		return linearView( (Math.sqrt( amplitude) * (double) max / Math.sqrt((double) max)), max);
	}
	
	//--------------------
	public static double[] linearView(double amplitude[], int max) {
		double[] result = new double[amplitude.length];
		for (int i=0;i<amplitude.length;i++) 
			result[i]=linearView(amplitude[i], max);
		return result;
	}
	public static double[] logarithmicView(double amplitude[], int max) {
		double[] result = new double[amplitude.length];
		for (int i=0;i<amplitude.length;i++) 
			result[i]=logarithmicView(amplitude[i], max);
		return result;
	}

	public static double[] expView(double amplitude[], int max) {
		double[] result = new double[amplitude.length];
		for (int i=0;i<amplitude.length;i++) 
			result[i]=linearView(amplitude[i], max);
		return result;
	}

	public static double[] squareView(double amplitude[], int max) {
		double[] result = new double[amplitude.length];
		for (int i=0;i<amplitude.length;i++) 
			result[i]=squareView(amplitude[i], max);
		return result;
	}

	public static double[] squareRootView(double amplitude[], int max) {
		double[] result = new double[amplitude.length];
		for (int i=0;i<amplitude.length;i++) 
			result[i]=squareRootView(amplitude[i], max);
		return result;
	}
	
	
}
