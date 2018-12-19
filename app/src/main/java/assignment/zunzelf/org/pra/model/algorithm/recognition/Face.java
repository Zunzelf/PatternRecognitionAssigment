package assignment.zunzelf.org.pra.model.algorithm.recognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import assignment.zunzelf.org.pra.model.utils.image.Filters;
import assignment.zunzelf.org.pra.model.utils.image.Segmentation;



public class Face {
    Filters filter = new Filters();
    Segmentation seg = new Segmentation();
    public Pair<List<Bitmap>, List<List<Point>>> scanImg(Bitmap bm){
        Bitmap bitmap = filter.skinFilter(bm, true);
        List<List<Point>> pts = new ArrayList<>();
        int bw = bm.getWidth();
        int bh = bm.getHeight();
        List<Bitmap> res = new ArrayList<>();
        int sumSize = 0;
        Pair<List<int[][]>, List<Point[]>> detected = seg.ffSegmentationSet(bitmap);
        List<int[][]> objs = detected.first;
        List<Point[]> coors = detected.second;
        int i = 0;
        for (int[][] obj : objs){
            int w = obj.length;
            int h = obj[0].length;
            int size = w*h;
            sumSize += size;
            Point max = coors.get(i)[1];
            Point min = coors.get(i)[0];
        }
        for (int[][] obj : objs){
            int w = obj.length;
            int h = obj[0].length;
            int size = w*h;
            if(size >= sumSize/objs.size() && size >= 0.2*(bw*bh)){
                Bitmap temp = arr2Img(obj);
                filter.BucketFill(temp, new Point(0,0), Color.WHITE, Color.BLACK);
                res.add(filter.inverseImage(temp));
                pts.add(plotting(filter.inverseImage(temp)));
            }
        }
        return new Pair<List<Bitmap>, List<List<Point>>>(res, pts);
    }
    public Pair<Bitmap, Pair<List<Bitmap>, List<List<Point>>>> scanImg2(Bitmap bm){
        Bitmap bitmap = filter.skinFilter(bm, true);
        List<List<Point>> pts = new ArrayList<>();
        int bw = bm.getWidth();
        int bh = bm.getHeight();
        List<Bitmap> res = new ArrayList<>();
        Bitmap bm_res = bm.copy(bm.getConfig(), true);
        int sumSize = 0;
        Pair<List<int[][]>, List<Point[]>> detected = seg.ffSegmentationSet(bitmap);
        List<int[][]> objs = detected.first;
        List<Point[]> coors = detected.second;
        int i = 0;
        for (int[][] obj : objs){
            int w = obj.length;
            int h = obj[0].length;
            int size = w*h;
            sumSize += size;
        }
        i = 0;
        for (int[][] obj : objs){
            int w = obj.length;
            int h = obj[0].length;
            int size = w*h;
            if(size >= sumSize/objs.size() && size >= 0.2*(bw*bh)){
                Bitmap temp = arr2Img(obj);
                filter.BucketFill(temp, new Point(0,0), Color.WHITE, Color.BLACK);
                res.add(filter.inverseImage(temp));
                List<Point> features = plotting(filter.inverseImage(temp));
                pts.add(features);
                Point max = coors.get(i)[1];
                Point min = coors.get(i)[0];
                bm_res = new Filters().drawRect(bm_res, new int[]{max.x, max.y, min.x, min.y});
                for(Point feat : features){
                    bm_res = new Filters().drawRect(bm_res, new int[]{min.x + feat.x + 1, min.y + feat.y +1, min.x + feat.x, min.y + feat.y}, Color.RED);
                }
            }
            i++;
        }
        return new Pair<Bitmap, Pair<List<Bitmap>, List<List<Point>>>>(bm_res, new Pair<List<Bitmap>, List<List<Point>>>(res, pts));
    }
    public Bitmap arr2Img(int[][] arr){
        Bitmap res = Bitmap.createBitmap(arr.length, arr[0].length, Bitmap.Config.ARGB_8888);
        for (int j = 0; j< arr[0].length; j++)
            for (int i = 0; i< arr.length; i++)
                res.setPixel(i, j, arr[i][j]);
        return res;
    }
    public List<Point> plotting(Bitmap face){
        List<Point> res = new ArrayList<>();
        Pair<List<int[][]>, List<Point[]>> objsPair = seg.ffSegmentationSet(face);
        List<int[][]> objs = objsPair.first;
        List<Point[]> pts = objsPair.second;
        int i = 0;
        for (int[][] obj : objs){
            Point max = pts.get(i)[1];
            Point min = pts.get(i)[0];
            int w = obj.length;
            int h = obj[0].length;
            if(obj.length > 0.05 * face.getWidth() && obj[0].length <= 0.5 * face.getHeight() && obj[0].length > 0.05 * face.getHeight()){
                if (max.y < face.getHeight()/2){
                    int wx = max.x - min.x;
                    int hx = max.y - min.y;
                    int rx = (int) Math.round((wx / (wx + hx + 0.0)) * 100.0);
                    int ry = (int) Math.round((hx / (wx + hx + 0.0)) * 100.0);
                    if(rx >= 70 && rx < 82){
                        Point coor = new Point(min.x + (wx/2), min.y + (hx/2));
                        res.add(coor);
                    }
                }
            }
            i++;
        }
        return res;
    }


}
