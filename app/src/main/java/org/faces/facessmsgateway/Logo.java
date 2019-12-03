package org.faces.facessmsgateway;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Logo extends AppCompatActivity {
    ImageView IMG;
    CustomProgress customProgress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        IMG = findViewById(R.id.ecosurvey);

        IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customProgress = CustomProgress.getInstance();
                customProgress.showProgress(Logo.this,"Loading please wait ...",false);
                loading(); // load inter ad
            }
        });
    }

    public void loading(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                load_next();
            }
        }, 1300);

    }

    public void load_next(){
        // for hiding the ProgressBar
        customProgress.hideProgress();

                Intent intent = new Intent(this, SMS_to_Excel.class);
                startActivity(intent);

    }
}
