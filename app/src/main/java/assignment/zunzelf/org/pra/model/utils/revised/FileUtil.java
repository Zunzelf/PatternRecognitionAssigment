package assignment.zunzelf.org.pra.model.utils.revised;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {

    public static final String DIRECTORY = "/storage/emulated/0/documents/";
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

    public String[][] loadDataset(String path) throws IOException, ClassNotFoundException {
        String[] line = load(path);
        String[][] res = new String[line.length][2];
        for(int i = 0; i < line.length; i++){
            res[i] = line[i].split(",");
        }
        return res;
    }

}

