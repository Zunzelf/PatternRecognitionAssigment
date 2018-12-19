package assignment.zunzelf.org.pra.model.utils.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import java.util.LinkedList;
import java.util.Queue;

public class Filters {

    public Bitmap skinFilter(Bitmap bm){
        return skinFilter(bm, false);
    }
    public Bitmap skinFilter(Bitmap bm, boolean inverse){
        Bitmap res = bm.copy(bm.getConfig(), true);
        int h = bm.getHeight();
        int w = bm.getWidth();
        int[] pixels = new int[w*h];
        res.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++)
            {
                int index = y * w + x;
                int R = (pixels[index] >> 16) & 0xff;     //bitwise shifting
                int G = (pixels[index] >> 8) & 0xff;
                int B = pixels[index] & 0xff;
                double c = 0.5*R - 0.419*G - 0.081*B;
                if (c >= 10 && c <= 45){
                    if(inverse)
                        pixels[index] = Color.BLACK;
                    else
                        pixels[index] = Color.WHITE;
                }else {
                    if(inverse)
                        pixels[index] = Color.WHITE;
                    else
                        pixels[index] = Color.BLACK;
                }
            }}
        res.setPixels(pixels, 0, w, 0, 0, w, h);
        return res;
    }
    public Bitmap sobelFilter(Bitmap bm){
        int maxGval = 0;
        int h = bm.getHeight();
        int w = bm.getWidth();
        int[][] edgeColors = new int[w][h];
        int maxGradient = -1;
        int index = 0;
        int x,y;
        Bitmap res = bm.copy(bm.getConfig(), true);
        int[] pixels = new int[w*h];
        res.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int i = 1; i < w - 1; i++) {
            for (int j = 1; j < h - 1; j++) {
                // i - 1, j - 1
                x = i - 1; y = j - 1;
                index = (y * w)+ x;
                int val00 = getGray(pixels[index]);
                // i - 1, j
                x = i - 1; y = j;
                index = (y * w)+ x;
                int val01 = getGray(pixels[index]);
                // i - 1, j + 1
                x = i - 1; y = j + 1;
                index = (y * w)+ x;
                int val02 = getGray(pixels[index]);

                // i, j - 1
                x = i; y = j - 1;
                index = (y * w)+ x;
                int val10 = getGray(pixels[index]);
                // i, j
                x = i; y = j;
                index = (y * w)+ x;
                int val11 = getGray(pixels[index]);
                // i, j + 1
                x = i; y = j + 1;
                index = (y * w)+ x;
                int val12 = getGray(pixels[index]);

                // i + 1, j - 1
                x = i + 1; y = j - 1;
                index = (y * w)+ x;
                int val20 = getGray(pixels[index]);
                // i + 1, j
                x = i + 1; y = j;
                index = (y * w)+ x;
                int val21 = getGray(pixels[index]);
                // i + 1, j + 1
                x = i + 1; y = j + 1;
                index = (y * w)+ x;
                int val22 = getGray(pixels[index]);

                // matrix filter
                int gx =  ((-1 * val00) + (0 * val01) + (1 * val02))
                        + ((-2 * val10) + (0 * val11) + (2 * val12))
                        + ((-1 * val20) + (0 * val21) + (1 * val22));

                int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                        + ((0 * val10) + (0 * val11) + (0 * val12))
                        + ((1 * val20) + (2 * val21) + (1 * val22));

                double gval = Math.sqrt((gx * gx) + (gy * gy));
                int g = (int) gval;

                if(maxGradient < g) {
                    maxGradient = g;
                }
                edgeColors[i][j] = g;
            }
        }

        double scale = 255.0 / maxGradient;

        for (int i = 1; i < w - 1; i++) {
            for (int j = 1; j < h - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;
                index = (j * w) + i;
                pixels[index] = edgeColor;
            }
        }
        res.setPixels(pixels, 0, w, 0, 0, w, h);
        return res;
    }
    public int  getGray(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
        return gray;
    }
    /*
     * Dilation and Erosion Algorithm
     * source : https://blog.ostermiller.org/dilate-and-erode
     * modified by : zunzelf
     */
    int[] dilate(int[] image, int w, int h){
        for (int i=0; i < w; i++){
            for (int j=0; j < h; j++){
                int index = (w * j) + i;
                int index2 = (w * j) + i - 1;
                int index3 = (w * (j - 1)) + i;
                int index4 = (w * j) + i + 1;
                int index5 = (w * (j + 1)) + i;
                if (image[index] == Color.WHITE){
                    // i-1, j
                    if (i>0 && image[index2]==Color.BLACK) image[index2] = 2;
                    //  i, j-1
                    if (j>0 && image[index3]==Color.BLACK) image[index3] = 2;
                    //  i+1,j & i+1<w
                    if (i+1 < w && image[index4]==Color.BLACK) image[index4] = 2;
                    //  i, j+1 & j+1<h
                    if (j+1 < h && image[index5]==Color.BLACK) image[index5] = 2;
                }
            }
        }
        for (int i=0; i<w; i++){
            for (int j=0; j<h; j++){
                int index = (w * j) + i;
                if (image[index] == 2){
                    image[index] = Color.WHITE;
                }
            }
        }
        return image;
    }
    public Bitmap dilation(Bitmap bm){
        Bitmap res = bm.copy(bm.getConfig(), true);
        int w = bm.getWidth(); int h = bm.getHeight();
        int[] pxls = new int[w * h];
        res.getPixels(pxls, 0, w, 0, 0, w, h);
        pxls = dilate(pxls, w, h);
        res.setPixels(pxls, 0, w, 0, 0, w, h);
        return res;
    }
    public Bitmap dilation(Bitmap bm, int k){
        Bitmap res = bm.copy(bm.getConfig(), true);
        int w = bm.getWidth(); int h = bm.getHeight();
        int[] pxls = new int[w * h];
        res.getPixels(pxls, 0, w, 0, 0, w, h);
        for (int i = 0; i < k; i++)
            pxls = dilate(pxls, w, h);
        res.setPixels(pxls, 0, w, 0, 0, w, h);
        return res;
    }
    public static Bitmap createBlackAndWhite(Bitmap src) {
        int white = 0xFFFFFFFF;
        int black = 0xFF000000;
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final float factor = 255f;
        final float redBri = 0.2126f;
        final float greenBri = 0.2126f;
        final float blueBri = 0.0722f;

        int length = width * height;
        int[] inpixels = new int[length];
        int[] oupixels = new int[length];

        src.getPixels(inpixels, 0, width, 0, 0, width, height);

        int point = 0;
        for(int pix: inpixels){
            int R = (pix >> 16) & 0xFF;
            int G = (pix >> 8) & 0xFF;
            int B = pix & 0xFF;

            float lum = (redBri * R / factor) + (greenBri * G / factor) + (blueBri * B / factor);

            if (lum > 0.4) {
                oupixels[point] = white;
            }else{
                oupixels[point] = black;
            }
            point++;
        }
        bmOut.setPixels(oupixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
    public void BucketFill(Bitmap bmp, Point pt, int targetColor, int replacementColor){
        Queue<Point> q = new LinkedList<Point>();
        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (bmp.getPixel(n.x, n.y) != targetColor)
                continue;
            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                bmp.setPixel(w.x, w.y, replacementColor);
                if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }
            while ((e.x < bmp.getWidth() - 1)
                    && (bmp.getPixel(e.x, e.y) == targetColor)) {
                bmp.setPixel(e.x, e.y, replacementColor);

                if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }}
    public Bitmap inverseImage(Bitmap bm){
        Bitmap res = bm.copy(bm.getConfig(), true);
        int w = res.getWidth();
        int h = res.getHeight();
        int[] pixels = new int[w*h];
        res.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++){
                int idx = (w * y)+x;
                int gray = getGray(pixels[idx]);
                if(gray > 125){
                    pixels[idx] = Color.BLACK;
                }
                else
                    pixels[idx] = Color.WHITE;
            }
        res.setPixels(pixels, 0, w, 0, 0, w, h);
        return res;
    }
    public Bitmap drawRect(Bitmap bm, int[] points){
        Bitmap res = bm.copy(bm.getConfig(), true);
        for(int y = points[3]; y <= (points[1] + 1); y++){
            for(int x = points[2]; x <= points[0] + 1; x++){
                if((x >= points[2]) && (x <= points[2] + 3))
                    res.setPixel(x, y, Color.GREEN);
                else if ((x <= points[0]) && (x >= points[0] - 3))
                    res.setPixel(x, y, Color.GREEN);
                else if ((y >= points[3]) && (y <= points[3] + 3))
                    res.setPixel(x, y, Color.GREEN);
                else if(y >= points[1] - 3 && y <= points[1]){
                    res.setPixel(x, y, Color.GREEN);}
            }
        }
        return res;
    }
    public Bitmap drawRect(Bitmap bm, int[] points, int color){
        Bitmap res = bm.copy(bm.getConfig(), true);
        for(int y = points[3]; y <= (points[1] + 1); y++){
            for(int x = points[2]; x <= points[0] + 1; x++){
                if((x >= points[2]) && (x <= points[2] + 3))
                    res.setPixel(x, y, color);
                else if ((x <= points[0]) && (x >= points[0] - 3))
                    res.setPixel(x, y, color);
                else if ((y >= points[3]) && (y <= points[3] + 3))
                    res.setPixel(x, y, color);
                else if(y >= points[1] - 3 && y <= points[1]){
                    res.setPixel(x, y, color);}
            }
        }
        return res;
    }
    public Bitmap drawRect(Bitmap bm, int[] points, int color, int size){
        Bitmap res = bm.copy(bm.getConfig(), true);
        for(int y = points[3]; y <= (points[1] + 1); y++){
            for(int x = points[2]; x <= points[0] + 1; x++){
                if((x >= points[2]) && (x <= points[2] + size))
                    res.setPixel(x, y, color);
                else if ((x <= points[0]) && (x >= points[0] - size))
                    res.setPixel(x, y, color);
                else if ((y >= points[3]) && (y <= points[3] + size))
                    res.setPixel(x, y, color);
                else if(y >= points[1] - size && y <= points[1]){
                    res.setPixel(x, y, color);}
            }
        }
        return res;
    }
}
