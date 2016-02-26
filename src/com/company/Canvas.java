package com.company;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.*;

public class Canvas extends JComponent{


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    enum Type{
        sound,
        spectr,
        none
    }

    private Type type;
    byte[] data;
    // data chanel part  sp_num
    double[][][][] specter;

    public void calcSpecter(){
        if(data==null)return;
        //define size
        int size=512;
        //temp
        double[][] data_=JPlayer.bytesToDouble(2,2,false,data);
        double[][][][] data_3=new double[data_.length / size*2][2][512][2];
        for(int i=0;i*256+512<data_.length ;i++){
            for(int j=0;j<512;j++)
                for(int ch=0;ch<data_[0].length;ch++) {
                    data_3[i][ch][j][0] = data_[i * 256 + j][ch];
                    data_3[i][ch][j][1] = 0;
                }
            for(int ch=0;ch<data_[0].length;ch++) {
                data_3[i][ch] = Specter.fft(data_3[i][ch]);
            }
        }
        specter=data_3;

    }

    public void setData(byte[] data) {

        this.data= data;
    }

    public Canvas(){
        setSize(256,1500);
        Timer t=new Timer(1000/60,e->repaint());
        //t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(gray);
        int shift=(int)sliderData*getWidth();
        if(type!=null)
        switch (type){

            case sound:
                drawSound(g2d);
                break;
            case spectr:
                drawSpecter(g2d);
                break;
            case none:
            default:
        }

        drawAxes(Direction.VERTICAL,0,getWidth(),g2d,256);


    }
    double sliderData=0;
    public int setSliderData(double d){
        if(sliderData!=d)repaint();
        sliderData=d;
        if(null==data)return 1;
        else {
            return data.length / getWidth();
        }

    }

    enum Direction{HORIZONTAL,VERTICAL}

    void drawAxes(Direction direction,int start,int stop,Graphics2D g,int step){
        if (step < -step) step = -step;
        if(start>stop){
            int tmp=start;
            start=stop;
            stop=tmp;
        }
        if(step<=0)return;

        int shift=(int)sliderData*getWidth(),sh2=0;
        for(int i=0;i<256;i++)
            if((shift+i)%256==0){
                sh2=i;
                break;
            }

        Paint p=g.getPaint();
        g.setPaint(new Color(34, 224, 190,128));
        g.setStroke(new BasicStroke(0.5f) );



        for(int i=start+sh2;i<stop;i+=step){
            if(direction==Direction.HORIZONTAL) {
                g.drawLine(0, i-sh2, getWidth(), i-sh2 );
                g.setFont(new Font("Serif", Font.PLAIN, 15));
                g.drawString(""+(shift+i),40,i-sh2-256);
            }
            else {
                g.drawLine(i,0,i,getHeight());
                g.setFont(new Font("Serif", Font.PLAIN, 15));
                g.drawString(""+(shift+i),i,40);
            }
        }
        g.setPaint(p) ;



    }
    private void drawSpecter(Graphics2D g){
        if(specter==null)return;
        int shift=(int)sliderData;
        for(int ch=0;ch<specter[shift].length;ch++)
            for(int i=0;i<specter[shift][ch].length;i++){

            }


    }

    private void drawSound(Graphics2D g){
        if(data == null || g == null )return;
        int shift=(int)sliderData*getWidth();

        g.setPaint(BLACK);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
        g.setPaint(GREEN);

        //System.out.println(shift+"- "+getWidth()+" :"+sliderData+" / "+data.length);
        for(int i=shift;i<data.length && i < shift+getWidth();i++) {
            g.drawLine(i-shift, 256+data[i],i-shift, 256);
        }

    }

}