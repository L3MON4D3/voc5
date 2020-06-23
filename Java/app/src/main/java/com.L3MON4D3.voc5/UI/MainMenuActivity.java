package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.widget.TextView;

import com.L3MON4D3.voc5.R;

public class MainMenuActivity extends VocActivity {
    TextView intro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if(getIntent().hasExtra("com.L3MON4D3.voc.UI.USER")){
            intro.findViewById(R.id.introText);
            intro.setText(getIntent().getExtras().getString("com.L3MON4D3.voc.UI.USER"));
        }
    }
}