package devidin.net.yavumeter.display.console;

import devidin.net.yavumeter.display.Displayer;

public class ConsoleDisplayer extends Displayer {

	public void displayLR(int[] amplitudeLR) {
		System.out.print("["+">".repeat(amplitudeLR[0]/2)
				  +" ".repeat(64-amplitudeLR[0]/2)
				  +"|"
				  +" ".repeat(64-amplitudeLR[1]/2)
				  +"<".repeat(amplitudeLR[1]/2)
				  +"]\r");

	}

	public void displayLRasNumber(int[] amplitudeLR) {
		System.out.print((amplitudeLR[0]+amplitudeLR[1])/2);
		System.out.print('\r');
	}

	public void displayLRasNumbers(int[] amplitudeLR) {
		System.out.print(amplitudeLR[0] + " - "+amplitudeLR[1]);
		System.out.print('\r');
	}

}
