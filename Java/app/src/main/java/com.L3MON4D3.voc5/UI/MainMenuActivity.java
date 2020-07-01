package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                startLearn();
            }
        });

        galleryBtn = findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGallery();
            }
        });

        editVocBtn = findViewById(R.id.editVocBtn);
        editVocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startEditVoc(2);}//todo change 2 to proper voc or move button to galleryActivity
        });

    }
}