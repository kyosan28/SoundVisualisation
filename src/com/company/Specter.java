package com.company;

/**
 * Created by okyo on 20.02.16.
 */
public class Specter {


    public static double[][] fft(double[][] x) {
        int N = x.length;

        // base case
        if (N == 1) return new double[][] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        double[][] even = new double[N/2][];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        double[][] q = fft(even);

        // fft of odd terms
        double[][] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        double[][] r = fft(odd);

        // combine
        double[][] y = new double[N][];

        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            double[] wk = {Math.cos(kth), Math.sin(kth)};
            double[] temp=times(r[k],wk);
            y[k]       =new double[]{q[k][0]+temp[0],q[k][1]+temp[1]};
            y[k + N/2] =new double[]{q[k][0]-temp[0],q[k][1]-temp[1]};
        }
        return y;
    }

    static double[] times(double[] w,double[] x){
        double[] r={x[0]*w[0]-x[1]*w[1],x[0]*w[1]+x[1]*w[0]};
        return r;
    }




}
