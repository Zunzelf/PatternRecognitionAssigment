package assignment.zunzelf.org.pra.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.utils.image.PreProModule;

public class EnT2 extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Uri imageURI;
    Bitmap bitmap;
    HorizontalScrollView hsv;

    int [][] rgb_hist = new int[3][256];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_t2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hsv = (HorizontalScrollView) findViewById(R.id.command_layout);
        hsv.setVisibility(View.INVISIBLE);
        imageViewButton();
        initiateAllBT();
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

    private void initiateAllBT(){
        Button w_bt = (Button) findViewById(R.id.apply_w_bt);
        w_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double value = Integer.parseInt(((EditText) findViewById(R.id.weight_inp)).getText().toString());
                Bitmap res = new PreProModule().eqTransform(bitmap, value/100.0);
                ((ImageView) findViewById(R.id.eq_result)).setImageBitmap(null);
                ((ImageView) findViewById(R.id.eq_result)).setImageBitmap(res);
            }
        });
        Button sm_bt = (Button) findViewById(R.id.apply_sm_bt);
        sm_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(((EditText) findViewById(R.id.conv_size)).getText().toString());
                Bitmap res = new PreProModule().smoothTransform(bitmap, value);
                ((ImageView) findViewById(R.id.sm_result)).setImageBitmap(null);
                ((ImageView) findViewById(R.id.sm_result)).setImageBitmap(res);
            }
        });
        Button spec_bt = (Button) findViewById(R.id.apply_spec_bt);
        spec_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value_a = ((SeekBar) findViewById(R.id.a_bar)).getProgress();
                int value_b = ((SeekBar) findViewById(R.id.b_bar)).getProgress();
                int value_c = ((SeekBar) findViewById(R.id.c_bar)).getProgress();
                int[] hist = new PreProModule().generateHistogram(value_a, value_b, value_c);
                Bitmap res = new PreProModule().specTransform(hist, bitmap);
                ((ImageView) findViewById(R.id.spec_result)).setImageBitmap(null);
                ((ImageView) findViewById(R.id.spec_result)).setImageBitmap(res);
            }
        });
    }

    // image view button
    private void imageViewButton(){
        inp = (ImageView) findViewById(R.id.imageInput3);
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
                hsv.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
