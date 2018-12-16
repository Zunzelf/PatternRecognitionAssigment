package assignment.zunzelf.org.pra.model.algorithm.revised;

import android.graphics.Point;

import assignment.zunzelf.org.pra.model.utils.image.Filters;

public class ChainCode {
    int w = 0;
    int h = 0;
    Filters f = new Filters();

    public String getChainCode(int[] img, int initX, int initY, int width){
        String chaincode = "";
        w = width;
        h = img.length/w;
        int dir = 0;
        int x = initX;
        int y = initY;
        while(true){
            System.out.println(dir);
            int[] right = checkSide(img, x, y, rightSide(dir));
            int[] left = checkSide(img, x, y, leftSide(dir));
            Point up = translate(x, y, dir);
            boolean forward = false;
            if(up.x < w && up.y < h && up.x >= 0 && up.y >= 0){
                int idx = (up.y * w) + x;
                int px = f.getGray(img[idx]);
                if(px < 0.7 * 255)
                    forward = true;
            }

            if(left.length > 0){
                chaincode += "" + dir;
                dir = left[left.length - 1];
            }
            else if(forward){
                chaincode += + dir;
            }
            else if(right.length> 0){
                chaincode += "" + dir;
                dir = right[right.length - 1];
            }
            else{
                System.out.println("nooooooooooooooooooo");
                break;
            }
            Point incr = translate(x, y, dir);

            x = incr.x;
            y = incr.y;

            if(x == initX && y == initY) break;
        }
        return chaincode;
    }
    public int[] checkSide(int[] bm, int x, int y, int[] side){
        String gets = "";
        for (int dir : side) {
            Point temp = translate(x, y, dir);
            if(temp.x < w && temp.y < h && temp.x >= 0 && temp.y >= 0) {
                int index = (temp.y * w) + temp.x;
                if (f.getGray(bm[index]) < 0.7 * 255) gets += "" + dir;
            }
        }
        int[] res = stringToInts(gets);
        return res;
    }
    public Point translate(int x, int y, int pos){
        if(pos > 7) pos = 0;
        switch (pos){
            case 1 : return new Point(x+1, y-1);
            case 0 : return new Point(x+1, y);
            case 7 : return new Point(x+1, y+1);
            case 6 : return new Point(x, y+1);
            case 5 : return new Point(x-1, y+1);
            case 4 : return new Point(x-1, y);
            case 3 : return new Point(x-1, y-1);
            case 2 : return new Point(x, y-1);
            default: return null;
        }
    }
    public int[] leftSide(int pos){
        switch (pos){
            case 0 : return new int[]{1,2};
            case 1 : return new int[]{2,3};
            case 2 : return new int[]{3,4};
            case 3 : return new int[]{4,5};
            case 4 : return new int[]{5,6};
            case 5 : return new int[]{6,7};
            case 6 : return new int[]{7,0};
            default : return new int[]{0,1};
        }
    }
    public int[] rightSide(int pos){
        switch (pos){
            case 0 : return new int[]{7,6,5};
            case 1 : return new int[]{0,7,6};
            case 2 : return new int[]{1,0,7};
            case 3 : return new int[]{2,1,0};
            case 4 : return new int[]{3,2,1};
            case 5 : return new int[]{4,3,2};
            case 6 : return new int[]{5,4,3};
            default : return new int[]{6,5,4};
        }
    }
    public int[] stringToInts(String s){
        int[] res = new int[s.length()];
        char[] temp = s.toCharArray();
        for(int i = 0; i < s.length();i++)
            res[i] = Character.getNumericValue(temp[i]);
        return res;
    }
}
