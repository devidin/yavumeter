<<<<<<< HEAD
package devidin.net.yavumeter;

import devidin.net.yavumeter.soundmodel.SoundCardHelper;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
=======
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
package devidin.net.yavumeter;

import devidin.net.yavumeter.soundmodel.SoundCardHelper;
>>>>>>> branch 'main' of https://github.com/devidin/yavumeter.git

public class DisplayWaveAsText {
    public static void main(String[] args) {
        SoundCardHelper.listMixers();
        SoundCardHelper.audioLevelMonitor(6, 0);
    }
}