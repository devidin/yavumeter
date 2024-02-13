package devidin.net.yavumeter.soundmodel;

import devidin.net.yavumeter.configuration.Configuration;

public class AudioFormat {
    private long sampleRate;
    private long sampleSizeInBits;
    private long channels;
    private boolean signed;
    private boolean bigEndian;

    public long getSampleRate() { return sampleRate; }
    public void setSampleRate(long value) { this.sampleRate = value; }

    public long getSampleSizeInBits() { return sampleSizeInBits; }
    public void setSampleSizeInBits(long value) { this.sampleSizeInBits = value; }

    public long getChannels() { return channels; }
    public void setChannels(long value) { this.channels = value; }

    public boolean getSigned() { return signed; }
    public void setSigned(boolean value) { this.signed = value; }

    public boolean getBigEndian() { return bigEndian; }
    public void setBigEndian(boolean value) { this.bigEndian = value; }
	public AudioFormat() {
		setSampleRate(16000);
		setSampleSizeInBits(8);
		setChannels(2);
		setSigned(true);
		setBigEndian(true);
	}

}
