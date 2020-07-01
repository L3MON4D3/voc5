package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;

public class MainMenuActivity extends VocActivity {
    TextView intro;
    Button learnBtn;
    Button galleryBtn;
    Button newVocBtn;
    Button editVocBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        intro = findViewById(R.id.introText);
        intro.setText("hello " + client.getUser());


        learnBtn = findViewById(R.id.learnBtn);
        learnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(),LernActivity.class);
                startActivity(startIntent);
            }
        });

        galleryBtn = findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGallery();
            }
        });

    }
}