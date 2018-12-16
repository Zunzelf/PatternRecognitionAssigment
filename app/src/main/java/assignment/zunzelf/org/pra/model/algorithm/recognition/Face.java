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
        List<int[][]> objs = seg.ffSegmentation(bitmap);
        int i = 0;
        for (int[][] obj : objs){
            int w = obj.length;
            int h = obj[0].length;
            int size = w*h;
            sumSize += size;
        }
        for (int[][] obj : objs){
            int w = obj.length;
            int h = obj[0].length;
            int size = w*h;
//            System.out.println("size "+size+" || "+sumSize/objs.size()+" || "+(bw*bh));
            if(size >= sumSize/objs.size() && size >= 0.2*(bw*bh)){
//                res.add(filter.inverseImage(arr2Img(obj)));
                Bitmap temp = arr2Img(obj);
                filter.BucketFill(temp, new Point(0,0), Color.WHITE, Color.BLACK);
                res.add(filter.inverseImage(temp));
//                res = plotting(filter.inverseImage(temp));
                pts.add(plotting(filter.inverseImage(temp)));
            }
        }
        return new Pair<List<Bitmap>, List<List<Point>>>(res, pts);
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
                    System.out.println(wx+", "+hx);
                    System.out.println(min.x+", "+min.y+", "+max.x+", "+max.y+" || size : "+(obj.length*obj[0].length) + " ration w : " + rx +"| h : " + ry);
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
