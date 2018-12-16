package assignment.zunzelf.org.pra.model.utils.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Segmentation {

    Filters f = new Filters();
    /*
     * FloodFill/Bomb algorithm for erasing object
     * */
    int[][] logBook = new int[][]{{}};
    Point max;
    Point min;
    public Point[] getNeighbours(int x, int y){
        return new Point[]{
                new Point(x+1, y),
                new Point(x+1, y-1),
                new Point(x, y-1),
                new Point(x-1, y-1),
                new Point(x-1, y),
                new Point(x-1, y+1), //
                new Point(x, y+1),
                new Point(x+1, y+1)
        };
    }
    public int[][] bomb(int[] img, int x, int y, int w,boolean fill){
        List<Point> p_temp = new ArrayList<>();
        Queue<Point> pts = new LinkedList<>();
        pts.offer(new Point(x, y));
        max = new Point(x, y);
        min = new Point(x, y);
        while(pts.size()>0){
            Point temp = pts.poll();
            int idx = (w * temp.y) + temp.x;
            int val = f.getGray(img[idx]);
            if (val < 0.75 * 255) { // black found!
                if(logBook[temp.x][temp.y] == 0) { // not yet visited
                    p_temp.add(temp);
                    if(temp.x < min.x) min.x = temp.x;
                    if(temp.y < min.y) min.y = temp.y;
                    if(temp.x > max.x) max.x = temp.x;
                    if(temp.y > max.y) max.y = temp.y;
                    for(Point p : getNeighbours(temp.x, temp.y)){
                        if((p.x < w && p.y < img.length/w) && (p.x >= 0 && p.y >= 0))
                            pts.offer(p);
                    }
                }
            }
            logBook[temp.x][temp.y] = 1;
        }
        Log.d("bomb@",min.x+", "+min.y+", "+max.x+", "+max.y);
        int tw = (max.x - min.x);
        int th = (max.y - min.y);
        int[][] res = new int[(tw + 4)][(th + 4)];
        if(fill)
            for (int m = 0; m < th + 4; m++)
                for (int n = 0; n < tw + 4; n++)
                    res[n][m] = Color.WHITE;

        for (Point pt : p_temp){
                Point idy = new Point(pt.x + tw - max.x + 1,  pt.y + th - max.y + 1);
                int idx = (pt.y * w )+pt.x;
                res[idy.x][idy.y] = img[idx];
        }
        return res;
    }
    /*
     * For drawing boundary box
     * input    : input bitmap, box position(Xmax, Ymax, Xmin, Ymin)
     * output   : edited bitmap
     * */
    public Bitmap drawBox(Bitmap bm, int[] points){
        Bitmap res = bm.copy(bm.getConfig(), true);
        for(int y = points[3]; y <= (points[1] + 1); y++){
            for(int x = points[2]; x <= points[0] + 1; x++){
                if(x >= bm.getWidth() || y >= bm.getHeight())
                    continue;
                if((x >= points[2]) && (x <= points[2] + 10))
                    res.setPixel(x, y, Color.GREEN);
                else if ((x <= points[0]) && (x >= points[0] - 10))
                    res.setPixel(x, y, Color.GREEN);
                else if ((y >= points[3]) && (y <= points[3] + 10))
                    res.setPixel(x, y, Color.GREEN);
                else if(y >= points[1] - 10 && y <= points[1]){
                    res.setPixel(x, y, Color.GREEN);}
            }
        }
        return res;
    }
    /*
    * Segmenting objects using floodfill
    * */
    public List<int[][]> ffSegmentation(Bitmap bm){
        return ffSegmentation(bm, true);
    }
    public List<int[][]> ffSegmentation(Bitmap bm, boolean fill){
        int w = bm.getWidth();
        int h = bm.getHeight();
        int objs = 0;
        List<int[][]> res = new ArrayList<>();
        int[] pixels = new int[w * h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        logBook = new int[w][h];
        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                int idx = (w * y) + x;
                int val = f.getGray(pixels[idx]);
                if (val < 0.75 * 255) { // black found!
                    if(logBook[x][y] == 0){ // not yet visited
                        objs++;
                        res.add(bomb(pixels, x, y, w, fill));
                    }
                }
            }
        }
        Log.d("Segmentation", "objects : " + objs);
        return res;
    }
    public Pair<List<int[][]>, List<Point[]>> ffSegmentationSet(Bitmap bm){
        return ffSegmentationSet(bm, true);
    }
    public Pair<List<int[][]>, List<Point[]>> ffSegmentationSet(Bitmap bm, boolean fill){
        int w = bm.getWidth();
        int h = bm.getHeight();
        int objs = 0;
        List<Point[]> resp = new ArrayList<>();
        List<int[][]> res = new ArrayList<>();
        int[] pixels = new int[w * h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        logBook = new int[w][h];
        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                int idx = (w * y) + x;
                int val = f.getGray(pixels[idx]);
                if (val < 0.75 * 255) { // black found!
                    if(logBook[x][y] == 0){ // not yet visited
                        objs++;
                        res.add(bomb(pixels, x, y, w, fill));
                        resp.add(new Point[]{min, max});
                    }
                }
            }
        }
        Log.d("Segmentation", "objects : " + objs);
        return new Pair<List<int[][]>, List<Point[]>>(res, resp);
    }

}
