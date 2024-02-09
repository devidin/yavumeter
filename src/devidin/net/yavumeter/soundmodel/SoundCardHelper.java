package devidin.net.yavumeter.soundmodel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
//import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;
//import javax.sound.sampled.*;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Mixer;
//import javax.sound.sampled.Mixer.Info;

public class SoundCardHelper {

	public static Mixer.Info[] getMixersList() {
		return AudioSystem.getMixerInfo();
	}

	public static Line[] getMixersLines(Mixer mixer) {
		Line[] lines = mixer.getTargetLines();

		return lines;
	}

	public static void listMixers() {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		System.out.println("Available Audio Mixers:");
		for (int m = 0; m < mixerInfos.length; m++) {
			listMixerLines(mixerInfos[m], m);
		}
	}

	public static void listMixerLines(Mixer.Info mixerInfo, int m) {
		System.out.println("Mixer " + m + " : " + mixerInfo);
		Mixer mixer = AudioSystem.getMixer(mixerInfo);

		Line.Info[] lineInfos = mixer.getSourceLineInfo();
		listLines(lineInfos, "source");
		lineInfos = mixer.getTargetLineInfo();
		listLines(lineInfos, "target");
	}

	public static void listLines(Line.Info[] lineInfos, String type) {

		for (int l = 0; l < lineInfos.length; l++) {
			System.out.println("      . " + type + " line " + l + ": " + lineInfos[l]);

			if (lineInfos[l] instanceof DataLine.Info) {
				DataLine.Info dataLineInfo = (DataLine.Info) lineInfos[l];
				AudioFormat[] formats = dataLineInfo.getFormats();

				for (AudioFormat format : formats) {
					System.out.println("      .    Supported Audio Format:" + " channels: " + format.getChannels()
							+ " encoding: " + format.getEncoding() + " frameSize: " + format.getFrameSize()
							+ " sampleSizeInBits: " + format.getSampleSizeInBits() + " sampleRate: "
							+ format.getSampleRate() + " frameRate: " + format.getFrameRate());
				}
			}
		}
	}

	static AudioFormat getAudioFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;

		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

		return format;

	}

	public static void audioLevelMonitor(int mixerId, int lineId) {
		// DataLine dataLine = null;
		TargetDataLine targetDataLine = null;
		try {
			// Get the selected audio mixer
			Mixer.Info[] mixersInfos = getMixersList();
			Mixer mixer = AudioSystem.getMixer(mixersInfos[mixerId]);
			System.out.println("======================================================================");
			System.out.println("Monitoring mixer: " + mixersInfos[mixerId]);

			// Get the selected line from the mixer
			Line.Info[] lineInfos = mixer.getSourceLineInfo();
			Line.Info lineInfo = lineInfos[lineId];
			Line line = mixer.getLine(lineInfo);
			System.out.println("Monitoring Line: " + lineInfos[lineId]);

			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
			System.out.println("Selected Audio Format: " + format);
			System.out.println("======================================================================");

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

			// byte[] buffer = new byte[targetDataLine.getBufferSize() / 5];
			byte[] buffer = new byte[targetDataLine.getBufferSize()];
			// byte[] buffer = new byte[64];
			AudioInputStream ais = new AudioInputStream(targetDataLine);

			System.out.println("Output Level:");
			int counter = 0;
			while (true) {

				int b = ais.read(buffer);
				for (byte bv : buffer) {
					System.out.print("\r" + ++counter + ":" + bv + " read " + b + " bytes");
					System.out.flush();
//                    System.out.println("bv);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Closing line...");

			if (targetDataLine != null) {
				targetDataLine.stop();
				targetDataLine.close();
			}

		}

	}

	private static int calculateAmplitude(byte[] buffer, int bytesRead) {
		int maxAmplitude = 0;
		for (int i = 0; i < bytesRead; i += 2) {
			// Convert bytes to 16-bit signed PCM samples
			int sample = (buffer[i + 1] << 8) | (buffer[i] & 0xFF);

			// Calculate amplitude (absolute value of the sample)
			int amplitude = Math.abs(sample);

			// Update max amplitude if the current amplitude is greater
			maxAmplitude = Math.max(maxAmplitude, amplitude);
		}
		return maxAmplitude;
	}
}
