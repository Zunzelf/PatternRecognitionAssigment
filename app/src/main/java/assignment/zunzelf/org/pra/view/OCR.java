package assignment.zunzelf.org.pra.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.io.IOException;
import java.util.List;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.algorithm.recognition.OCRecognition;
import assignment.zunzelf.org.pra.model.utils.revised.FileUtil;

public class OCR extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Button get;
    Uri imageURI;
    List<Bitmap> res;
    OCRecognition ocr;
    Bitmap bitmap, bitmap2;
    int scale = 50;
    String[][] model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        ocr = new OCRecognition();
        try {
            model = new FileUtil().loadDataset("ocr_model.mdl");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageViewButton();
        getButton();
    }
    // back to home button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // image view button
    private void imageViewButton(){
        inp = (ImageView) findViewById(R.id.imageOCR);
        inp.setClickable(true);
        inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void getButton(){
        get = (Button) findViewById(R.id.getBT);
        get.setClickable(true);
        final EditText inpText = (EditText) findViewById(R.id.getText);;
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int idx = Integer.parseInt(inpText.getText().toString());
                    if(idx < res.size() && idx >= 0)
                        ((ImageView) findViewById(R.id.detectedOCR)).setImageBitmap(res.get(idx));
                }catch (Exception e){
                    Log.d("Error", "value isn't int");
                }
            }
        });
    }
    private void openGallery(){
        Intent gallery =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK && requestCode == PICK_IMAGE){
            // Load Image File
            ((ImageView) findViewById(R.id.detectedOCR)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.getBT)).setVisibility(View.VISIBLE);
            ((EditText) findViewById(R.id.getText)).setVisibility(View.VISIBLE);
            imageURI = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                inp.setImageBitmap(bitmap);

                Pair<List<Bitmap>, String> contents = ocr.readImg(bitmap, model);
                res = contents.first;
                String text = contents.second;
                ((TextView) findViewById(R.id.outputOCR)).setText(res.size()+" detected : " + text);
                if(res.size() > 0)
                    ((ImageView) findViewById(R.id.detectedOCR)).setImageBitmap(res.get(0));
                else
                    ((ImageView) findViewById(R.id.detectedOCR)).setImageBitmap(Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
