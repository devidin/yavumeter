package devidin.net.yavumeter;
import devidin.net.yavumeter.soundmodel.SoundCardHelper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public class DisplayWaveAsText {
	
	//private static final Logger logger = LoggerFactory.getLogger(DisplayWaveAsText.class);


    public static void main(String[] args) {
        SoundCardHelper.listMixers();
        SoundCardHelper.audioLevelMonitor(6, 0);
    }
}