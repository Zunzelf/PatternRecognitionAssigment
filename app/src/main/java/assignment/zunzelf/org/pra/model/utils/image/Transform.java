package assignment.zunzelf.org.pra.model.utils.image;

import android.graphics.Bitmap;
import android.util.Log;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

/*
* credit to :
* */
public class Transform {
    Filters f = new Filters();
    public Bitmap DFT(Bitmap bm, boolean inverse){
        int w = bm.getWidth();
        int h = bm.getHeight();
        Bitmap freq = bm.copy(Bitmap.Config.ARGB_8888, true); //magnitude
        Bitmap freqR = bm.copy(Bitmap.Config.ARGB_8888, true); //Real
        Bitmap freqI = bm.copy(Bitmap.Config.ARGB_8888, true); //Imaginary
        Bitmap freqA = bm.copy(Bitmap.Config.ARGB_8888, true); //angle
        Bitmap hat = bm.copy(Bitmap.Config.ARGB_8888, true); //magnitude
        Bitmap hatR = bm.copy(Bitmap.Config.ARGB_8888, true); //Real
        Bitmap hatI = bm.copy(Bitmap.Config.ARGB_8888, true); //Imaginary
        Bitmap hatA = bm.copy(Bitmap.Config.ARGB_8888, true); //angle
        int[][] g = new int[h][w]; /** original image */
        double[][] GReal = new double[h][w]; /** Fourier real component */
        double[][] GImaginer = new double[h][w]; /** Fourier imaginary component */
        double[][] GMagnitude = new double[h][w]; /** Fourier Magnitude component*/
        double[][] GAngle = new double[h][w]; /** Fourier Phase component */
        double[][] gReal = new double[h][w]; /** Inverse Fourier real component */
        double[][] gImaginer = new double[h][w]; /** Inverse Fourier imaginary component */
        double[][] gMagnitude = new double[h][w]; /** InverseFourier Magnitude component*/
        double[][] gAngle = new double[h][w]; /** Inverse Fourier Phase component */

        /** Storing Image into regular Array */
        int[] pxls = new int[w*h];
        bm.getPixels(pxls, 0, w, 0, 0, w, h);
        for(int p = 0; p<h; p++){
            for(int q = 0; q < w; q++){
                int idx = (w * p) + q;
                int val = f.getGray(pxls[idx]);
                g[p][q] = val;
            }
        }
        Log.d("DFT", "Start converting...");
        /** DFT */
        for(int p=0; p<h; p++){
            for(int q=0; q<w; q++){

//                for(int y=0; y<h; y++){
                    for(int x=0; x<w; x++){
                        GReal[p][q] += g[p][x]*cos(2 * PI * x * q / w);
                        GImaginer[p][q] += g[p][x]*sin(2 * PI * x * q / w);
//                    }
                }
                GReal[p][q] /= w; /** Scaling, only in Fourier not in inverse*/
                GImaginer[p][q] /= w;
                GMagnitude[p][q] = hypot(GReal[p][q],GImaginer[p][q])*w;
                GAngle[p][q] = atan(GImaginer[p][q]/GReal[p][q]);

                int GVal = (int)GMagnitude[p][q];
                int GColor = 0xFF000000 | (GVal<<16 | GVal<<8 | GVal);
                freq.setPixel(q,p,GColor);

                int GRVal = (int)GReal[p][q];
                int GRColor = 0xFF000000 | (GRVal<<16 | GRVal<<8 | GRVal);
                freqR.setPixel(q,p,GRColor);

                int GIVal = (int)GImaginer[p][q];
                int GIColor = 0xFF000000 | (GIVal<<16 | GIVal<<8 | GIVal);
                freqI.setPixel(q,p,GIColor);

                int GAVal = (int)((GAngle[p][q] + PI/2)*255/PI); /** scaling for display */
                int GAColor = 0xFF000000 | (GAVal<<16 | GAVal<<8 | GAVal);
                freqA.setPixel(q,p,GAColor);
            }
        }
        Log.d("DFT", "Done converting...");
        if(!inverse)
            return freqI;

        for(int p=0; p<h; p++){
            for(int q=0; q<w; q++){
                GReal[p][q] = 0;
            }
        }

        Log.d("IDFT", "Start converting...");
        /** Inverse DFT */
        for(int p=0; p<h; p++){
            for(int q=0; q<w; q++){
                    for(int x=0; x<w; x++){
                        gReal[p][q] += GReal[p][x]*cos(2*PI*x*q/w) + GImaginer[p][x]*sin(2*PI*x*q/w);
                        gImaginer[p][q] += GImaginer[p][x]*cos(2*PI*x*q/w) - GReal[p][x]*sin(2*PI*x*q/w);
                    }
                gMagnitude[p][q] = hypot(gReal[p][q],gImaginer[p][q]);
                gAngle[p][q] = atan(gImaginer[p][q]/gReal[p][q]);

                int gVal = (int)gMagnitude[p][q];
                int gColor = 0xFF000000 | (gVal<<16 | gVal<<8 | gVal);
                hat.setPixel(q,p,gColor);

                int gRVal = (int)gReal[p][q];
                int gRColor = 0xFF000000 | (gRVal<<16 | gRVal<<8 | gRVal);
                hatR.setPixel(q,p,gRColor);

                int gIVal = (int)gImaginer[p][q];
                int gIColor = 0xFF000000 | (gIVal<<16 | gIVal<<8 | gIVal);
                hatI.setPixel(q,p,gIColor);

                int gAVal = (int)((gAngle[p][q] + PI/2)*255/PI); /** scaling for display */
                int gAColor = 0xFF000000 | (gAVal<<16 | gAVal<<8 | gAVal);
                hatA.setPixel(q,p,gAColor);
            }
        }
        Log.d("IDFT", "Done converting...");
        return hat;
    }
}
