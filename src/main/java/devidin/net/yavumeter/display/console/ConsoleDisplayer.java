package devidin.net.yavumeter.display.console;

import devidin.net.yavumeter.display.Displayer;

public class ConsoleDisplayer extends Displayer {
	private static int WIDTH = 128;
	private static int MAX_AMPLITUDE = 128;

	public void displayLR(int[] amplitudeLR) {
		
		System.out.print("[" + ">".repeat(amplitudeLR[0] / 2) + " ".repeat(64 - amplitudeLR[0] / 2) + "|"
				+ " ".repeat(64 - amplitudeLR[1] / 2) + "<".repeat(amplitudeLR[1] / 2) + "]\r");
				
		//display(amplitudeLR, 2);
	}

	public void displayLRasNumber(int[] amplitudeLR) {
		System.out.print((amplitudeLR[0] + amplitudeLR[1]) / 2);
		System.out.print('\r');
	}

	public void displayLRasNumbers(int[] amplitudeLR) {
		System.out.print(amplitudeLR[0] + " - " + amplitudeLR[1]);
		System.out.print('\r');
	}

	public void display(int[] amplitude, int channels) {
		int channelWidth = (int) WIDTH / channels;
		double ratio = (double) channelWidth / (double) MAX_AMPLITUDE;
		int width;
		System.out.print("|");
		for (int channel = 0; channel < channels; channel++) {
			width = (int) (amplitude[channel] * ratio);
			System.out.print("*".repeat(width));
			System.out.print(" ".repeat((int) (channelWidth - width))); // pad with blanks to width
			System.out.print("|");
		}
		System.out.print('\r');
	}

	public void displayAsNumbers(int[] amplitude, int channels) {
		for (int channel = 0; channel < channels; channel++) {
			System.out.print(amplitude[channel] + " ");
		}
		System.out.print('\r');
	}

	public void displayAsNumber(int[] amplitude, int channels) {
		int totAmplitude = 0;
		for (int channel = 0; channel < channels; channel++) {
			totAmplitude+=amplitude[channel];
		}
		System.out.print(totAmplitude+ "\r    ");	
	}


}
