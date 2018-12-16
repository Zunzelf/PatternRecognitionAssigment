package assignment.zunzelf.org.pra.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
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

import java.io.IOException;
import java.util.List;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.algorithm.recognition.Face;

public class FaceRegion extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView inp;
    Button get;
    Uri imageURI;
    List<Bitmap> res;
    Bitmap bitmap, bitmap2;
    List<List<Point>> pts;
    int scale = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_region);getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        inp = (ImageView) findViewById(R.id.frView);
        inp.setClickable(true);
        inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }
    private void getButton(){
        get = (Button) findViewById(R.id.button11);
        get.setClickable(true);
        final EditText inpText = (EditText) findViewById(R.id.editText);;
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int idx = Integer.parseInt(inpText.getText().toString());
                    if(idx < res.size() && idx >= 0){
                        String resp = "";
                        for(Point tmp : pts.get(idx)){
                            resp += "("+tmp.x + ", " + tmp.y+") ";
                        }
                        ((TextView) findViewById(R.id.eyesCoor)).setText("Eyes coordinates : " + resp);
                        ((ImageView) findViewById(R.id.resultView)).setImageBitmap(res.get(idx));
                    }
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
            ((ImageView) findViewById(R.id.resultView)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.eyesCoor)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.noseCoor)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.mouthCoor)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.button11)).setVisibility(View.VISIBLE);
            ((EditText) findViewById(R.id.editText)).setVisibility(View.VISIBLE);
            imageURI = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                inp.setImageBitmap(bitmap);
                Pair<List<Bitmap>, List<List<Point>>> contents = new Face().scanImg(bitmap);
                res = contents.first;
                pts = contents.second;
                ((TextView) findViewById(R.id.textView4)).setText(" detected : " + res.size() + " face(s)");
                if(res.size() > 0){
                    String resp = "";
                    for(Point tmp : pts.get(0)){
                        resp += "("+tmp.x + ", " + tmp.y+") ";
                    }
                    ((TextView) findViewById(R.id.eyesCoor)).setText("Eyes coordinates : " + resp);
                    ((ImageView) findViewById(R.id.resultView)).setImageBitmap(res.get(0));
                }
                else
                    ((ImageView) findViewById(R.id.resultView)).setImageBitmap(Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888));
                ((ImageView) findViewById(R.id.frView)).setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
