package devidin.net.yavumeter.display;

public abstract interface Displayer {
	
	public abstract void displayLR(double[] amplitudeLR);
	public abstract void displayLRasNumber(double[] amplitudeLR);
	public abstract void displayLRasNumbers(double[] amplitudeLR);
	
	public abstract void display(double[] amplitudeLR, int channels);
	public abstract void displayAsNumber(double[] amplitudeLR, int channels);
	public abstract void displayAsNumbers(double[] amplitudeLR, int channels);
	
	public abstract void init();
	public abstract void shutdown();
	
	
}
