package devidin.net.yavumeter;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public class Main {
	
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
    	logger.debug("Vu meter started");
        SoundCardHelper.listMixers();
        SoundCardHelper.audioLevelMonitor(6, 0);
    	logger.debug("Vu meter ended");
    }
}