package com.example.audioprocessor;

public class SinWave {

    private int amplitude;
    private int frequency;
    private double phase;


    public void setWave(int frequency) {
        this.amplitude = 32767;
        this.frequency = frequency;
        this.phase = 0;
    }

    public void setWave(int frequency, double phase) {
        this.amplitude = 32767;
        this.frequency = frequency;
        this.phase = phase;
    }

    public void setWave(int frequency, double phase, int amplitude) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phase = phase;
    }

    // 默认为16bit单声道采样.
    public int sample(int samplingRate, int duration, byte[] buffer) {
        int sampleCount = (int) samplingRate * duration;
        int sampleLength = sampleCount * 2;
        for (int i = 0; i < sampleCount; i++) {
            short sampledValue = (short) Math.floor(amplitude * Math.sin(2 * Math.PI * frequency * i / samplingRate + phase));
            buffer[2 * i] = (byte) (sampledValue & 0xff);
            buffer[2 * i + 1] = (byte) ((sampledValue >> 8) & 0xff);
        }
        return sampleLength;
    }

}
