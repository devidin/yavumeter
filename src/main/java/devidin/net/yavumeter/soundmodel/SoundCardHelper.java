package devidin.net.yavumeter.soundmodel;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
			//byte[] buffer = new byte[targetDataLine.getBufferSize()];
			//byte[] buffer = new byte[1];
			byte[] buffer = new byte[1024];
			AudioInputStream ais = new AudioInputStream(targetDataLine);

			System.out.println("Output Level:");
			int[] amplitudeLR;
			while (true) {

				int b = ais.read(buffer);
				amplitudeLR=calculateAmplitudeRMSLR(buffer, b);
				//displayLR(amplitudeLR);
				displayLRasNumbers(amplitudeLR);
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
	private static void displayLR(int[] amplitudeLR) {
		System.out.print("["+">".repeat(amplitudeLR[0]/2)
				  +" ".repeat(64-amplitudeLR[0]/2)
				  +"|"
				  +" ".repeat(64-amplitudeLR[1]/2)
				  +"<".repeat(amplitudeLR[1]/2)
				  +"]\r");
	}
	
	private static void displayLRasNumber(int[] amplitudeLR) {
		//System.out.print("\r    ");
		System.out.print(amplitudeLR[0]);
		System.out.print('\r');
	}
	private static void displayLRasNumbers(int[] amplitudeLR) {
		//System.out.print("\r    ");
		System.out.print(amplitudeLR[0] + " - "+amplitudeLR[1]);
		System.out.print('\r');
	}

	private static int calculateAmplitudeAvg(byte[] buffer, int bytesRead) {
			
		int totAmplitude = 0;
		for (int i = 0; i < bytesRead; i += 2) {
			int amplitude = Math.abs(buffer[i] & 0xFF);
			totAmplitude = totAmplitude+ amplitude;
		}
		return totAmplitude/bytesRead;
	}
	private static int calculateAmplitudeRMS(byte[] buffer, int bytesRead) {
		double sumOfSquares = 0;
		for (int i = 0; i < bytesRead; i += 2) {
			int amplitude = Math.abs(buffer[i] & 0xFF);
			sumOfSquares = sumOfSquares+ amplitude*amplitude;
		}
		return (int) Math.sqrt(sumOfSquares)/bytesRead;
	}
	
	private static int[] calculateAmplitudeAvgLR(byte[] buffer, int bytesRead) {
		int[] leftRight = new int[2];
		int totAmplitude = 0;
		for (int channel=0;channel<2;channel++) {
			for (int i = 0; i < bytesRead; i += 2) {
				int amplitude = Math.abs(buffer[i+channel] & 0xFF); // left channel  is the first byte (+0), right the second (+1)
				totAmplitude = totAmplitude+ amplitude;
			}
			//leftRight[channel]=(int)totAmplitude/bytesRead;
			leftRight[channel]=Math.min(128,(int)totAmplitude/bytesRead);
		}
		return leftRight;
	}
	private static int[] calculateAmplitudeRMSLR(byte[] buffer, int bytesRead) {
		int[] leftRight = new int[2];
		for (int channel=0;channel<2;channel++) {
			double sumOfSquares = 0;
			for (int i = 0; i < bytesRead; i += 2) {
				int amplitude = buffer[i+channel] & 0xFF;// left channel  is the first byte (+0), right the second (+1)
				sumOfSquares = sumOfSquares+ amplitude*amplitude;
			}
			//leftRight[channel]=(int) Math.sqrt(sumOfSquares)/bytesRead;
			leftRight[channel]=Math.max(0,Math.min(128,(int) Math.sqrt(sumOfSquares/bytesRead)-30)); // never 0 (??)
		}
		return leftRight;
	}
	
}