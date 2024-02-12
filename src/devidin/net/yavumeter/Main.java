package devidin.net.yavumeter;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public class Main {
	
	//private static final Logger logger = LoggerFactory.getLogger(DisplayWaveAsText.class);
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
    	logger.debug("Ceci est un message de débogage.");
        SoundCardHelper.listMixers();
        SoundCardHelper.audioLevelMonitor(6, 0);
    }
}