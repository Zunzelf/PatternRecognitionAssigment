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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.PreProModule;

public class EnT1 extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Uri imageURI;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_t1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageViewButton();
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

    // image view button
    private void imageViewButton(){
        inp = (ImageView) findViewById(R.id.inputImage2);
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

                int[] specHist = new PreProModule().generateHistogram(200, 125, 300);
                Bitmap result = new PreProModule().eqTransform(bitmap, 1);
                ((ImageView) findViewById(R.id.histeq)).setImageBitmap(result);
                result = new PreProModule().smoothTransform(bitmap, 3);
                ((ImageView) findViewById(R.id.smoothing)).setImageBitmap(result);
                result = new PreProModule().specTransform(specHist, bitmap); //
                ((ImageView) findViewById(R.id.spec)).setImageBitmap(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
