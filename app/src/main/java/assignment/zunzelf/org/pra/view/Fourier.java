package assignment.zunzelf.org.pra.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.utils.image.Transform;

public class Fourier extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Button get;
    Uri imageURI;
    List<Bitmap> res;
    Bitmap bitmap, bitmap2;
    int scale = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourier);
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
        inp = (ImageView) findViewById(R.id.ftView);
        inp.setClickable(true);
        inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void getButton(){
        get = (Button) findViewById(R.id.trf_btn);
        get.setClickable(true);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap resb = new Transform().DFT(bitmap, true);
                ((ImageView) findViewById(R.id.resTrans)).setImageBitmap(resb);
            }
        });
    }
    private void openGallery(){
        Intent gallery =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ((ImageView) findViewById(R.id.ftView)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.trf_btn)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.resTrans)).setVisibility(View.VISIBLE);
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
