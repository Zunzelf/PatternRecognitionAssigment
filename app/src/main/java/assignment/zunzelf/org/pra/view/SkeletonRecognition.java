package assignment.zunzelf.org.pra.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.HistogramUtils;
import assignment.zunzelf.org.pra.model.utils.FileUtil;
import assignment.zunzelf.org.pra.model.utils.NewImageUtil;

public class SkeletonRecognition extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Uri imageURI;
    Bitmap bitmap;
    String[][] models;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skeleton_recognition);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String dir = FileUtil.DIRECTORY;
//        // create model
//        try {
//            new FileUtil().createDataset("dataset_mdl");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // load models
        File mdl_file = new File(dir, "dataset_mdl");
        try {
            models = new FileUtil().loadDataset(mdl_file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageViewButton();
        Button proceed = (Button) findViewById(R.id.processData);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Dataset", "Processing...");
                try {
                    processData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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

    private void processData() throws IOException {
        String res = new NewImageUtil().inferences(models, bitmap);
        ((TextView) findViewById(R.id.textResult)).setText(res);
    }

    // image view button
    private void imageViewButton(){
        inp = (ImageView) findViewById(R.id.inputRecog);
        inp.setClickable(true);
        inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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
            imageURI = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                inp.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
