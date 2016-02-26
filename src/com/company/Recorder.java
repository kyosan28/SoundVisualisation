package com.company;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class Recorder extends Thread{
    private AudioFormat format;
    private TargetDataLine line = null;
    private ByteArrayOutputStream out;
    byte[] data;

    public void init(){
        /*                                                              fr size  fr rate
        *                         Формат                        sample/sec     chan   фр/сек
        */
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
        out = new ByteArrayOutputStream();

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
           System.err.println("Line not supported"+ info);
        }

        try {
            // Obtain and open the line
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }

        data = new byte[4096];
    }

    public Recorder() {
        init();
    }

    public void writeToFile(String filename) throws IOException {
        byte[] buffer=out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream;
        audioInputStream = new AudioInputStream(bais, format, buffer.length/format.getFrameSize());

        System.out.println("Запись начата");

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(filename));

        audioInputStream.close();
        System.out.println("Запись завершена");
    }

    public TimeElapsed getElapsedTime(){

        TimeElapsed te= new TimeElapsed((long) (out.size()/(format.getFrameRate()/1000)/format.getFrameSize()));
        return te;
    }

    public void run() {
        line.start();

        while (true)
            readData();

    }

    public void pause() {
        line.stop();
        line.flush();
    }

    public void _resume() {
        line.start();
    }

    public void readData() {
        int numBytesRead;
        numBytesRead = line.read(data, 0, data.length);
        // Save this chunk of data.
        if (numBytesRead != 0) {
            out.write(data, 0, numBytesRead);
        } else{
            try {
                sleep(500);
            } catch (InterruptedException e) {
                System.err.print("Thread sleep error in Recorder");
            }
            return;
        }

    }
}
