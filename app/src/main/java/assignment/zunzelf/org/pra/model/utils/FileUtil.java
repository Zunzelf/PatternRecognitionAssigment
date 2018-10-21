package assignment.zunzelf.org.pra.model.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileUtil {

    public static final String DIRECTORY = "/storage/emulated/0/documents/";

    public static void write(String filename, String[] text) {
        File file = new File(DIRECTORY, filename);

        try {

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            for(String s : text) {
                bufferedWriter.write(s);
                bufferedWriter.write("\r\n");
            }

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void create_dataset(String path, String[][] data) {
        File file = new File(path);

    }

    public static String[] load(String filename) {
        String line;

        File file = new File(DIRECTORY, filename);
        ArrayList<String> text = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            while ((line = bufferedReader.readLine()) != null) {
                text.add(line);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return text.toArray(new String[text.size()]);
    }

    public void createDataset(String filename) throws IOException {
        File file = new File(DIRECTORY, "dataset"); // will change this to flexible path(using parameter input)
        String[][] res = new String[file.listFiles().length][2];
        int pnt = 0;
        NewImageUtil niu = new NewImageUtil();
        FileOutputStream fos = new FileOutputStream(new File(DIRECTORY, filename));
        ObjectOutputStream os = new ObjectOutputStream(fos);

        for (File i : file.listFiles()){
            String path = i.getPath();
            String name = i.getName();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            String feature = niu.getSkeletonFeature(bitmap);
            System.out.println(feature);
            res[pnt] = new String[] {feature, name.split("\\.(?=[^\\.]+$)")[0]};
            pnt ++;
        }

        os.writeObject(res);
        os.close();
        fos.close();
    }

    public String[][] loadDataset(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream input;
        input = new ObjectInputStream(new FileInputStream(new File(path)));
        String[][] res = (String[][]) input.readObject();
        input.close();
        return res;
    }

}

