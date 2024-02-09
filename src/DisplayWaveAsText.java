//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import soundmodel.SoundCardHelper;

public class DisplayWaveAsText {
    public static void main(String[] args) {
        SoundCardHelper.listMixers();
        SoundCardHelper.audioLevelMonitor(6, 0);
    }
}