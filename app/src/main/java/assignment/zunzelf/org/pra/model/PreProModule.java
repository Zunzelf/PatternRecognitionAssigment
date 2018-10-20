package assignment.zunzelf.org.pra.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class PreProModule {

    HistogramUtils hu = new HistogramUtils();
    // Cumulative histogram single channel
    public int[] accHist(int[] origin, double weight){
        int [] res = origin.clone();
        int count = 0;
        for (int x = 1;x < res.length; x++){
            count = count + res[x];
            res[x] = res[0] + (int)Math.round(weight*count);
        }
        return res;
    }

    // Histogram equalization single channel
    public int[][] histEq(int[] origin, double weight){
        int [] cuh = accHist(origin, weight);
        int max = cuh[cuh.length-1];
        int [][] res = new int[2][cuh.length];
        for (int i = 0; i < cuh.length; i++){
            // equalized value
            double val = (cuh[i] + 0.0)/max;
            int beta = (int)Math.floor(val*255);
            // for lookup table
            res[0][i] = beta;
            // for histogram
            res[1][beta] = res[1][beta] + origin[i];
        }
        return res;
    }

    // get RGB equalized histogram
    public int[][] RGBHisteq(Bitmap bm, double weight) {
        int[][] res = new int[3][256];
        int[][] hist = hu.RGBHist(bm);
        res = new int[][]{
                histEq(hist[0], weight)[0],
                histEq(hist[1], weight)[0],
                histEq(hist[2], weight)[0]
        };
        return res;
    }

    // utilities
    public double[] normalize(int[] hist){
        int len = hist.length;
        double[] res = new double[len];
        double max = hist[len-1]+0.0;
        for (int i = 0; i < len; i++){
            res[i] = hist[i]/max;
        }
        return res;
    }

    public int getClosestIndex(double[] a, double x) {
        int low = 0;
        int high = a.length - 1;
        if (high < 0)
            throw new IllegalArgumentException("The array cannot be empty");
        while (low < high) {
            int mid = (low + high) / 2;
            assert(mid < high);
            double d1 = Math.abs(a[mid  ] - x);
            double d2 = Math.abs(a[mid+1] - x);
            if (d2 <= d1) {
                low = mid+1;
            }
            else {
                high = mid;
            }
        }
        return high;
    }

    // transformation functions :
    // image transformation using equalization with inner cfd
    public Bitmap eqTransform(Bitmap bm, double weight){
        int[][] histeq = RGBHisteq(bm, weight);
        return eqTransform(bm, histeq);
    }
    // Image transformation using equalization with outer cfd
    public Bitmap eqTransform(Bitmap bm, int[][] cfd){
        Bitmap res = bm.copy(Bitmap.Config.ARGB_8888, true);
        res.setHasAlpha(true);
        int h = res.getHeight();
        int w = res.getWidth();
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                int pixel = res.getPixel(x, y);
                int r = Color.red(pixel);
                int b = Color.blue(pixel);
                int g = Color.green(pixel);
                res.setPixel(x, y, Color.rgb(cfd[0][r],cfd[1][g],cfd[2][b]));
            }
        }
        return res;
    }

    // Image transformation by matching other histogram with inner cfd
    public Bitmap specTransform(int[] mask, Bitmap bm){
        int[][] histeq = RGBHisteq(bm, 1);
        return specTransform(histeq, mask, bm);
    }
    // Image transformation by matching other histogram with outer cfd
    public Bitmap specTransform(int[][] cfd, int[] mask, Bitmap bm){
        Bitmap res = bm.copy(Bitmap.Config.ARGB_8888, true);
        int[][] lookUp = new int[3][256];
        int h = res.getHeight();
        int w = res.getWidth();
        res.setHasAlpha(true);
        // get normalized histograms
        double[][] normCfd = new double[3][256];
        normCfd[0] = normalize(cfd[0]);
        normCfd[1] = normalize(cfd[1]);
        normCfd[2] = normalize(cfd[2]);
        double [] mskAcc = normalize(accHist(mask, 1));
        // matching to mask
        for(int i = 0; i < 256; i++){
            lookUp[0][i] = getClosestIndex(mskAcc, normCfd[0][i]); //RED
            lookUp[1][i] = getClosestIndex(mskAcc, normCfd[1][i]); //GREEN
            lookUp[2][i] = getClosestIndex(mskAcc, normCfd[2][i]); //BLUE
        }
        // map to bitmap
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                int pixel = res.getPixel(x, y);
                int r = Color.red(pixel);
                int b = Color.blue(pixel);
                int g = Color.green(pixel);
                res.setPixel(x, y, Color.rgb(lookUp[0][r],lookUp[1][g],lookUp[2][b]));
            }
        }
        return res;
    }

    // Image smoothing using filter based on convolution
    public Bitmap smoothTransform(Bitmap bm, int size){
        Bitmap res = bm.copy(Bitmap.Config.ARGB_8888, true);
        res.setHasAlpha(true);
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(size);
        convMatrix.setAll(1);
        convMatrix.Matrix[1][1] = 5;
        convMatrix.Factor = 5 + 8;
        convMatrix.Offset = 1;
        return convMatrix.computeConvolution(bm, convMatrix);
    }

    public int[] generateHistogram(int a, int b, int c){
        int[] res = new int[256];
        double tempY = 3000.0;
        res[0] = a;
        res[255] = c;
        double x = a;
        double m = (tempY-a)/b;
        for (int i = 1; i < 255; i++){
            if(i == b){
                x = tempY*10;
                m = (c - tempY)/(255.0-b);
            }
            res[i] = (int) Math.round((m*i)+x);
//            Log.d("gradients", "c : "+ x +", m : "+ m +", x : "+ i +", y : "+ res[i]);
        }
        return res;
    }
}
