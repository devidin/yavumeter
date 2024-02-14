package devidin.net.yavumeter;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public class YAvumeter {
	
    private static final Logger logger = LoggerFactory.getLogger(YAvumeter.class);


    public static void main(String[] args) {
    	logger.debug("Started");
        SoundCardHelper.listMixers();
        new VUmeterDisplayer().monitor();
    	logger.debug("Ended");
    }
}