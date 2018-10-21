package assignment.zunzelf.org.pra.model.utils;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import assignment.zunzelf.org.pra.model.datamodel.SkeletonFeature;

public class NewImageUtil {

    // -----------------------------
    // Image Enhancement Algorithm
    // -----------------------------

    public static Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = width * height;
        int[] pixels = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < size; i++) {
            int pixel = pixels[i];
            int grayscale = (((pixel & 0x00ff0000) >> 16) + ((pixel & 0x0000ff00) >> 8) + (pixel & 0x000000ff)) / 3;

            if (grayscale < threshold) {
                pixels[i] = pixel & 0xff000000;
            } else {
                pixels[i] = pixel | 0x00ffffff;
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    // -----------------------------
    // Feature Extraction Algorithm
    // -----------------------------

    public static Bitmap[] getSkeleton(Bitmap bitmap) {
        int count;
        int[] border;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        int[] pixelsb = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsb, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = SubImageUtil.floodFill(pixels, i, j, width);

                    do {
                        count = SubImageUtil.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    SubImageUtil.customStep(pixelsb, border[0], border[1], border[2], border[3], i, j, width);
                }
            }
        }

        return new Bitmap[]{
                Bitmap.createBitmap(pixelsa, width, height, bitmap.getConfig()),
                Bitmap.createBitmap(pixelsb, width, height, bitmap.getConfig())
        };
    }

    public String getSkeletonFeature(Bitmap bitmap) { //for making dataset~
        int count;
        int[] border, border2;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        String stringBuffer = "";

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = SubImageUtil.floodFill(pixels, i, j, width);

                    do {
                        count = SubImageUtil.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    border2 = SubImageUtil.getNewBorder(pixelsa, border[0], border[1], border[2], border[3], width);
                    SkeletonFeature sf = SubImageUtil.extractFeature(pixelsa, border2[0], border2[1], border2[2], border2[3], width);

                    stringBuffer += "&"+(String.format("%d&%d&%d&%d&%d&%d&%d&%d&%d&%d",
                            sf.endpoints.size(),
                            sf.hTop ? 1 : 0,
                            sf.hMid  ? 1 : 0,
                            sf.hBottom  ? 1 : 0,
                            sf.vLeft ? 1 : 0,
                            sf.vMid ? 1 : 0,
                            sf.vRight ? 1 : 0,
                            sf.lTop ? 1 : 0,
                            sf.lMid ? 1 : 0,
                            sf.lBottom ? 1 : 0));
                }
            }
        }

        return (stringBuffer).toString();
    }

    public List<String> getSkeletonFeatures(Bitmap bitmap) {
        int count;
        int[] border, border2;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        List<String> stringBuffer = new ArrayList<String>();

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = SubImageUtil.floodFill(pixels, i, j, width);

                    do {
                        count = SubImageUtil.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    border2 = SubImageUtil.getNewBorder(pixelsa, border[0], border[1], border[2], border[3], width);
                    SkeletonFeature sf = SubImageUtil.extractFeature(pixelsa, border2[0], border2[1], border2[2], border2[3], width);
                    // 10-feature
                    stringBuffer.add(String.format("&%d&%d&%d&%d&%d&%d&%d&%d&%d&%d",
                            sf.endpoints.size(), sf.hTop ? 1 : 0,
                            sf.hMid  ? 1 : 0, sf.hBottom  ? 1 : 0,
                            sf.vLeft ? 1 : 0, sf.vMid ? 1 : 0,
                            sf.vRight ? 1 : 0, sf.lTop ? 1 : 0,
                            sf.lMid ? 1 : 0, sf.lBottom ? 1 : 0));
                }
            }
        }

        return stringBuffer;
    }

    private int[] intArr(String[] string) { //Note the [] after the String.
        int number[] = new int[string.length];

        for (int i = 1; i < string.length; i++) {
            number[i] = Integer.parseInt(string[i]);
        }
        return number;
    }

    public double MSE(int[] j1, int[] j2){
        double res = 0.0;
        for(int i = 0; i < j1.length; i++){
            res += Math.pow((j1[i]-j2[i]),2);
        }
        return res;
    }

    public String inference(String[][] models, String inp){
        int[] j1 = intArr(models[0][0].replaceAll("\\s+","").split("&"));
        int[] j2 = intArr(inp.replaceAll("\\s+","").split("&"));
        String res = models[0][1];
        double mse = MSE(j1, j2);
        for(int i = 1; i < models.length; i++){
            j1 = intArr(models[i][0].replaceAll("\\s+","").split("&"));
            j2 = intArr(inp.replaceAll("\\s+","").split("&"));
            if(j1.length != j2.length)
                continue;
            double msex = MSE(j1, j2);
            System.out.println(mse+"?"+msex+"|"+models[i][1]);
            System.out.println(inp+"?"+models[i][0]+"|"+models[i][1]);
            if(mse > msex) {
                res = models[i][1];
                mse = msex;
            }
        }
        return res;
    }

    public String inferences(String[][] models, Bitmap inp){
        String res = "";
        List<String> features = getSkeletonFeatures(inp);

        for(String feature : features){
            res += ""+inference(models, feature);
        }
        return res;
    }
}

