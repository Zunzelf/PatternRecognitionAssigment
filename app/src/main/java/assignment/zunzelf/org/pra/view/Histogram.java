package assignment.zunzelf.org.pra.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.HistogramUtils;

public class Histogram extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Uri imageURI;
    Bitmap bitmap;
    int [][] rgb_hist = new int[3][256];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageViewButton();
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
        inp = (ImageView) findViewById(R.id.imageInput);
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
                int lim = 80000;
                rgb_hist = new HistogramUtils().RGBHist(bitmap);
                new HistogramUtils().setBarGraphSeries((GraphView) findViewById(R.id.red_graph), rgb_hist[0], Color.RED, lim);
                new HistogramUtils().setBarGraphSeries((GraphView) findViewById(R.id.green_graph), rgb_hist[1], Color.GREEN, lim);
                new HistogramUtils().setBarGraphSeries((GraphView) findViewById(R.id.blue_graph), rgb_hist[2], Color.BLUE, lim);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
