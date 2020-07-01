package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;



import com.L3MON4D3.voc5.R;



public class Pop extends VocActivity  {

    TextView textViewUserAnswer = (TextView) findViewById(R.id.textViewUserAnswer);
    TextView textViewRigthAnswer =  (TextView) findViewById(R.id.textViewRigthAnswer);
    Button rigthbtn = (Button) findViewById(R.id.rigthbtn);
    Button wrongbtn= (Button) findViewById(R.id.wrongbtn);
    int currentp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_popup);

        if(getIntent().hasExtra("UserAnswer")) {
            textViewUserAnswer.setText(getIntent().getExtras().getString("UserAnswer"));
        }
        if(getIntent().hasExtra("rightAnswer")) {
            textViewRigthAnswer.setText(getIntent().getExtras().getString("rightAnswer"));
        }
        if(getIntent().hasExtra("rightAnswer")) {
            textViewRigthAnswer.setText(getIntent().getExtras().getString("rightAnswer"));
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(heigth*0.6));

        rigthbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent();
                startIntent.putExtra("Phase", currentp);
                setResult(RESULT_OK,startIntent);
                finish();
            }
        });
        wrongbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent();
                startIntent.putExtra("Phase", currentp);
                setResult(RESULT_OK,startIntent);
                finish();
            }
        });
    }
}
