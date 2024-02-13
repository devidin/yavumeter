package devidin.net.yavumeter.display;

public abstract class Displayer {
	
	public abstract void displayLR(int[] amplitudeLR);
	public abstract void displayLRasNumber(int[] amplitudeLR);
	public abstract void displayLRasNumbers(int[] amplitudeLR);
	
	public abstract void display(int[] amplitudeLR, int channels);
	public abstract void displayAsNumber(int[] amplitudeLR, int channels);
	public abstract void displayAsNumbers(int[] amplitudeLR, int channels);

}
