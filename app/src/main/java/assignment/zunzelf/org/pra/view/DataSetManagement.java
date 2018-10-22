package assignment.zunzelf.org.pra.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

import assignment.zunzelf.org.pra.R;
import assignment.zunzelf.org.pra.model.utils.FileUtil;
public class DataSetManagement extends AppCompatActivity {

    EditText save, load;
    Button saveBT, loadBT;
    String dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_set_management);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        saveBT = (Button) findViewById(R.id.create_bt);
        loadBT = (Button) findViewById(R.id.load_bt);
        save = (EditText) findViewById(R.id.save_input2);
        save.setText("filename.mdl");
        load = (EditText) findViewById(R.id.load_input);
        load.setText((new File(dir, "dataset_mdl")).getPath());
        dir = new FileUtil().DIRECTORY;
        saveLoadButton();
    }

    private void saveLoadButton(){
        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new FileUtil().createDataset(save.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        loadBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    new FileUtil().createDataset(save.getText().toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Log.d("experimental", "in-development");
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


//        // create model
//        try {
//            new FileUtil().createDataset("dataset_mdl");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}
