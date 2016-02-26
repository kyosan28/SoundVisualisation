package com.company;

import javax.sound.sampled.*;
import java.io.*;

public class JPlayer {

	String filename;
	AudioInputStream ais;
	ByteArrayOutputStream bais;
    AudioFormat format;

    public JPlayer(String filename) {
		this.filename = filename;
		this.initialize();
	}

	public void initialize() {
		try {
			ais = AudioSystem.getAudioInputStream(new File(filename));
		} catch (Exception e) {
			System.out.println("Exception in initializing. " + e);
		}
		bais=new ByteArrayOutputStream();
        format = ais.getFormat();

	}

    public static double[][] bytesToDouble(int channels,int bytes_in_sample,boolean big_endian,byte[] data){
        double[][] result=new double[data.length/bytes_in_sample/channels][channels];

        for(int i=0;i<channels;i++){
            for(int j=0;j<bytes_in_sample;j++) {
                for(int k=0;k<result.length;k++){
                    if(big_endian)result[k][i]+=(j*256)*data[k*channels*bytes_in_sample + i*bytes_in_sample+j];
                    else result[k][i]+=(bytes_in_sample - j*256)*data[k*channels*bytes_in_sample + i*bytes_in_sample+j];
                }
            }
        }
        return result;
    }

    public void read(boolean arg1){
        if(arg1){
            int nBytesRead = 0;
            byte[] abData = new byte[1280];
            while (nBytesRead != -1)
                try {
                    nBytesRead = ais.read(abData, 0, abData.length);
                    bais.write(abData, 0, nBytesRead);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public byte[] read(){
        int nBytesRead = 0;
        byte[] abData = new byte[12800];
        while (nBytesRead != -1) {
            try {
                nBytesRead = ais.read(abData, 0, abData.length);
                if(nBytesRead != -1){
                    bais.write(abData,0,nBytesRead);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bais.toByteArray();
    }

    public void play(int start){play(start,bais.toByteArray(),format);}

    public void play(){this.play(0);}

    public static void play(int start,byte[] data,AudioFormat format){
        new Thread(() -> {
            SourceDataLine line = null;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                line.write(data, start,data.length);
            } catch (Exception e) {
                System.out.println("Exception in initializing. getting line. " + e);
            }
            line.drain();
            line.close();
        }).start();
    }



}