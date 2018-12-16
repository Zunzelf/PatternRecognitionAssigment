package assignment.zunzelf.org.pra.model.algorithm.recognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import assignment.zunzelf.org.pra.model.algorithm.revised.ThinningAlgorithm;
import assignment.zunzelf.org.pra.model.utils.image.Filters;
import assignment.zunzelf.org.pra.model.utils.image.Segmentation;

public class OCRecognition {
    public List<int[][]> seekObjects(Bitmap bitmap){
        Bitmap bm = new ThinningAlgorithm().zhangSuen(bitmap);
        return (new Segmentation()).ffSegmentation(bm);
    }
    public List<Bitmap> scanImg(Bitmap bm){
        Bitmap bitmap = toBinary(bm);
        List<Bitmap> res = new ArrayList<>();
        List<int[][]> objs = seekObjects(bitmap);
        int i = 0;

        Log.d("Feature", "Extracting Features..");
        for (int[][] obj : objs){
            res.add(arr2Img(obj));
            String holeFeature = hole(obj);
            String regionFeature = region(obj);
            String feature = regionFeature + holeFeature;
            System.out.println();
            Log.d("Feature", "Object-"+i+" : "+feature);
            i++;
        }
        return res;
    }
    public Pair<List<Bitmap>, String> readImg(Bitmap bm, String[][] model){
        Bitmap bitmap = toBinary(bm);
        String recRes = "";
        List<Bitmap> res = new ArrayList<>();
        List<int[][]> objs = seekObjects(bitmap);
        int i = 0;

        Log.d("Feature", "Extracting Features..");
        for (int[][] obj : objs){
            res.add(arr2Img(obj));
            String holeFeature = hole(obj);
            String regionFeature = region(obj);
            String feature = regionFeature + holeFeature;
            recRes += inference(feature, model) + " " ;
            Log.d("Feature", "Object-"+i+" : "+feature);
            i++;
        }
        return new Pair<List<Bitmap>, String>(res, recRes);
    }

    public String hole(int[][] img){
        boolean isHole = false;
        String res="";
        int[][] temp = img.clone();
        int top = 0;
        boolean upper = false;
        int bot = 0;
        boolean dual = false;
        Bitmap filled = arr2Img(temp);
        new Filters().BucketFill(filled, new Point(0, 0), Color.WHITE, Color.BLACK);
        for (int y = 0; y < temp[0].length; y++){
            if(filled.getPixel(img.length/2, y) > Color.BLACK) {
                if(!isHole){
                    isHole = true;
                }
                if(upper && !dual){
                    dual = true;
                }
                if(y < temp[0].length/2)
                    top += 1;
                else if(y >= temp[0].length/2)
                    bot += 1;
            }
            if(filled.getPixel(img.length/2, y) == Color.BLACK && top > 0 && !upper)
                upper = true;

        }
        if(!isHole)
            res = "0000";
        else if(dual && top > 0 && bot > 0)
            res = "1110";
        else if(100*(top + 0.0)/(bot+top) >= 70)
            res = "1100";
        else if(100*(bot + 0.0)/(bot+top) >= 68)
            res = "1010";
        else
            res = "1001";
        return res;
    }

    public String region(int[][] img){
        int[] res = new int[]{0,0,0,0,0,0,0,0,0};
        int w = img.length;
        int h = img[0].length;
        int xlb =(int) Math.round(w * 0.45);
        int xub =(int) Math.round(xlb + w * 0.1);
        int ylb =(int) Math.round(w * 0.45);
        int yub =(int) Math.round(ylb + w * 0.1);
        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++){
                if(img[x][y] == Color.BLACK){
                    int i = 0;
                    String comb = "";
                    Point[] p = new Segmentation().getNeighbours(x, y);
                    Point pt = p[0];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 0;
                    }
                    pt = p[1];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 1;
                    }
                    pt = p[2];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 2;
                    }
                    pt = p[3];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 3;
                    }
                    pt = p[4];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 4;
                    }
                    pt = p[5];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 5;
                    }
                    pt = p[6];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 6;
                    }
                    pt = p[7];
                    if(img[pt.x][pt.y] != Color.WHITE){
                        i += 1;
                        comb += 7;
                    }
                    // getting noisy edge
                    if (i == 2){
                        if(comb.equals("01") || comb.equals("12") || comb.equals("34") || comb.equals("45")
                                || comb.equals("56") || comb.equals("67") || comb.equals("07") || comb.equals("23")){
                            i = 1;
                        }
                    }
                    if (i == 3){
                        if(comb.equals("123") || comb.equals("345") || comb.equals("567") || comb.equals("017"))
                            i = 1;
                    }

                    if(i == 1){
                        res[8] += 1;
                            // top - left
                        if(x <= xlb && y <= ylb)
                            res[0] = 1;
                            // top - center
                        else if(x < xub && x > xlb && y <= ylb)
                            res[1] = 1;
                            // top - right
                        else if(x >= xub && y <= ylb)
                            res[2] = 1;
                            // center - left
                        else if(x < xlb && y > ylb && y < yub)
                            res[3] = 1;
                            // center - right
                        else if(x >= xub && y > ylb && y < yub)
                            res[4] = 1;
                            // bottom - left
                        else if(x <= xlb && y >= yub)
                            res[5] = 1;
                            // bottom - center
                        else if(x < xub && x > xlb && y >= yub)
                            res[6] = 1;
                            // bottom - right
                        else if(x >= xub && y >= yub)
                            res[7] = 1;
                    }
                }
            }
        }
        return mkString(res, "");
    }

    public Bitmap arr2Img(int[][] arr){
        Bitmap res = Bitmap.createBitmap(arr.length, arr[0].length, Bitmap.Config.ARGB_8888);
        for (int j = 0; j< arr[0].length; j++)
            for (int i = 0; i< arr.length; i++)
                res.setPixel(i, j, arr[i][j]);
        return res;
    }

    public Bitmap toBinary(Bitmap img){
        int w = img.getWidth();
        int h = img.getHeight();
        Bitmap bm = img.copy(img.getConfig(), true);

        int[] pixels = new int[w*h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int j = 0; j< h; j++)
            for (int i = 0; i< w; i++){
                int idx = (j * w) + i;
                if(new Filters().getGray(pixels[idx]) < 0.85 * 255)
                    pixels[idx] = Color.BLACK;
                else
                    pixels[idx] = Color.WHITE;
        }
        bm.setPixels(pixels, 0, w, 0, 0, w, h);
        return bm;
    }

    public String mkString(int[] arr, String separator){
        String res = "";
        for(int i = 0; i < arr.length;i++)
            if (i == 0 || i == arr.length - 1)
                res += arr[i];
            else
                res += separator + arr[i] + separator;
        return res;
    }

    public String inference(String feature, String[][] model){
        String res = "<unk>";
        for (int i = 0; i < model.length; i++){
            if(feature.equals(model[i][0]))
                res = model[i][1];
        }
        return res;
    }

}
