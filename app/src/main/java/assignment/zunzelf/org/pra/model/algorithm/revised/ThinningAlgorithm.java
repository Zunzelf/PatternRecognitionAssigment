package assignment.zunzelf.org.pra.model.algorithm.revised;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import assignment.zunzelf.org.pra.model.utils.image.Filters;

public class ThinningAlgorithm {

    static final int white = 0xFFFFFFFF;
    static final int black = 0xFF000000;
    final static int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};
    int obj = 0;
    public Bitmap zhangSuen(Bitmap image) {
        Bitmap img = new Filters().createBlackAndWhite(image);
        boolean firstStep = false;
        boolean hasChanged;
        List<Point> toWhite = new ArrayList<Point>();
        Bitmap res = img;
        do {
            hasChanged = false;
            firstStep = !firstStep;

            for (int y = 1; y < res.getHeight() - 1; y++) {
                for (int x = 1; x < res.getWidth() - 1; x++) {
                    if (res.getPixel(x, y) != black)
                        continue;
                    int nn = numNeighbors(x, y, res);
                    if (nn < 2 || nn > 6)
                        continue;
                    if (numTransitions(x, y, res) != 1)
                        continue;
                    if (!atLeastOneIsWhite(x, y, firstStep ? 0 : 1, res))
                        continue;
                    toWhite.add(new Point(x, y));
                    hasChanged = true;
                }
            }

            for (Point p : toWhite)
                res.setPixel(p.x, p.y, Color.WHITE);
            toWhite.clear();

        } while (firstStep || hasChanged);
        return res;
    }

    public int numNeighbors(int x, int y, Bitmap img) {
        int count = 0;
        for (int i = 0; i < 7; i++) {
            int[] nbrs = translate(x, y, i);
            if (img.getPixel(nbrs[0], nbrs[1]) == black)
                count++;
        }
        return count;
    }

    public int numTransitions(int x, int y, Bitmap img) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int[] nbrs = translate(x, y, i);
            if (img.getPixel(nbrs[0], nbrs[1]) == white) {
                nbrs = translate(x, y, i+1);
                if (img.getPixel(nbrs[0], nbrs[1]) == black)
                    count++;
            }
            if (count > 1) break;
        }
        return count;
    }

    public boolean atLeastOneIsWhite(int x, int y, int step, Bitmap img) {
        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = translate(x, y, group[i][j]);
                if (img.getPixel(nbr[0], nbr[1]) == white) {
                    count++;
                    break;
                }
            }
        return count > 1;
    }

    public int[] translate(int x, int y, int pos){
        if(pos > 7) pos = 0;
        switch (pos){
            case 1 : return new int[]{x+1, y-1};
            case 0 : return new int[]{x+1, y};
            case 7 : return new int[]{x+1, y+1};
            case 6 : return new int[]{x, y+1};
            case 5 : return new int[]{x-1, y+1};
            case 4 : return new int[]{x-1, y};
            case 3 : return new int[]{x-1, y-1};
            case 2 : return new int[]{x, y-1};
            default: return null;
        }
    }
}
