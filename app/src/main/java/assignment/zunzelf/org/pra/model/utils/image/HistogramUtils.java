package assignment.zunzelf.org.pra.model.utils.image;

import android.graphics.Bitmap;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;

public class HistogramUtils {
    public int[][] RGBHist(Bitmap image){
        int[] r_hist = new int[256];
        int[] g_hist = new int[256];
        int[] b_hist = new int[256];
        int w = image.getWidth();
        int h = image.getHeight();

        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++){
                int rgb = image.getPixel(x, y);
                r_hist[(rgb >> 16) & 0xff] += 1;
                g_hist[(rgb >> 8) & 0xff] += 1;
                b_hist[rgb & 0xff] += 1;
            }
        }
        return new int[][] {
            r_hist,
                g_hist,
                    b_hist
        };
    }

    public DataPoint[] rgbToPlot(int[] arr){
        int n = arr.length;
        DataPoint[] values = new DataPoint[n];     //creating an object of type DataPoint[] of size 'n'
        for(int i=0;i<n;i++){
            DataPoint v = new DataPoint(i,arr[i]);
            values[i] = v;
        }
        return values;
    }

    public void setBarGraphSeries(GraphView gv, int[] vArr, int color){
        gv.removeAllSeries();
        LineGraphSeries<DataPoint> series;
        series= new LineGraphSeries<>(rgbToPlot(vArr));   //initializing/defining series
        series.setColor(color);
        gv.addSeries(series);
        gv.getViewport().setScalable(true);
        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(255);
    }
    public void setBarGraphSeries(GraphView gv, int[] vArr, int color, int lim){
        gv.removeAllSeries();
        LineGraphSeries<DataPoint> series;
        series= new LineGraphSeries<>(rgbToPlot(vArr));   //initializing/defining series
        series.setColor(color);
        gv.addSeries(series);
        gv.getViewport().setScalable(true);
        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(255);
        gv.getViewport().setMaxY(lim);
    }



}
