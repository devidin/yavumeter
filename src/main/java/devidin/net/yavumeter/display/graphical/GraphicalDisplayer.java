package devidin.net.yavumeter.display.graphical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devidin.net.yavumeter.display.Displayer;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;

public class GraphicalDisplayer extends Displayer {
	private static GraphicalDisplayerConfiguration configuration = null;
	private static final Logger logger = LoggerFactory.getLogger(GraphicalDisplayer.class);

	public GraphicalDisplayerConfiguration getConfiguration() {
		if (configuration==null) configuration=GraphicalDisplayerConfiguration.LoadConfiguration();
		return configuration;
	}
	
	public void init() {
		getConfiguration();
		logger.info("Configuration loaded:"+getConfiguration());
	}
	
	public void shutdown() {
		logger.debug("Shutdown complete");
	}
	
	public GraphicalDisplayer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void displayLR(int[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayLRasNumber(int[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayLRasNumbers(int[] amplitudeLR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayAsNumber(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayAsNumbers(int[] amplitudeLR, int channels) {
		// TODO Auto-generated method stub

	}

}
