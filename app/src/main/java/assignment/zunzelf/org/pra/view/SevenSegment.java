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
import assignment.zunzelf.org.pra.model.ImageChainCode;
import assignment.zunzelf.org.pra.model.ImageClassification;

public class SevenSegment extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Uri imageURI;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven_segment);
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
        inp = (ImageView) findViewById(R.id.imageInput5);
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
                Bitmap bmbw = new ImageChainCode().createBlackAndWhite(bitmap);
                List<String> objs = new ImageChainCode().seekObjects(bmbw);
                String result = new ImageClassification().translate(objs, "seven");
                System.out.println(result);
                ((TextView) findViewById(R.id.resultView2)).setText(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
