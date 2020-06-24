package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.L3MON4D3.voc5.R;
import android.content.Intent;
public class LernActivity  extends VocActivity {
    TextView TextViewQuestion;
    ImageButton checkbtn;
    EditText EditTextAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_activity);

        checkbtn = (ImageButton) findViewById(R.id.checkbtn);
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(LernActivity.this,Pop.class));
            }
        });


    }

}

