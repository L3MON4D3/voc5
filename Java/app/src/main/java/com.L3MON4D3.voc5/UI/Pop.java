package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RegionIterator;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


import com.L3MON4D3.voc5.R;

import java.util.ArrayList;

public class Pop extends AppCompatActivity {
    TextView textViewUserAnswer;
    TextView textViewRigthAnswer;
    Button rigthbtn;
    Button wrongbtn;

    int currentp;
    String UserAnswer;
    String RightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_popup2);
        textViewUserAnswer = (TextView) findViewById(R.id.textViewUserAnswer);
        textViewRigthAnswer = (TextView) findViewById(R.id.textViewRigthAnswer);
        rigthbtn = (Button) findViewById(R.id.rigthbtn);
        wrongbtn = (Button) findViewById(R.id.wrongbtn);

        if (getIntent().hasExtra("userAnswer")) {

            UserAnswer = getIntent().getExtras().getString("userAnswer");

        }
        if (getIntent().hasExtra("rightAnswer")) {
            RightAnswer = getIntent().getExtras().getString("rightAnswer");
        }
        if (getIntent().hasExtra("phase")) {
            currentp = getIntent().getExtras().getInt("phase");
        }
        textViewUserAnswer.setText(UserAnswer);
        textViewRigthAnswer.setText(RightAnswer);
        findf();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (heigth * 0.6));

        rigthbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentp >= 5) {
                    currentp = 5;
                } else {
                    currentp++;
                }
                Intent startIntent = new Intent();
                startIntent.putExtra("phase", currentp);
                setResult(RESULT_OK, startIntent);
                finish();
            }
        });

        wrongbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentp <= 1) {
                    currentp = 1;
                } else {
                    currentp--;
                }
                Intent startIntent = new Intent();
                startIntent.putExtra("phase", currentp);
                setResult(RESULT_OK, startIntent);
                finish();
            }
        });
    }

    public void findf() {
        ArrayList<Boolean> temp = new ArrayList<Boolean>();
        if(RightAnswer.length()<UserAnswer.length()){
            int l = (RightAnswer.length())-(UserAnswer.length());
            for(int k=0; k<=l; k++){
                temp.add(false);
            }
        }
        for (int i = 0; i <= UserAnswer.length() - 1; i++) {
            if (UserAnswer.charAt(i) == RightAnswer.charAt(i)) {
                temp.add(true);
            } else {
                temp.add(false);
            }
        }
        window(temp);
    }
    public void window(ArrayList<Boolean> b) {
        SpannableString ss = new SpannableString("");
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        BackgroundColorSpan bcsRed = new BackgroundColorSpan(Color.RED);
        for(int i =0; i<UserAnswer.length(); i++){
            ss = getSpannableString( String.valueOf(UserAnswer.charAt(i)));
            if(!b.get(i)){
                ss.setSpan(bcsRed,0, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            ssb.append(ss);
        }

        textViewUserAnswer.setText(ssb);
    }

    public SpannableString getSpannableString(String c){
        SpannableString ss = new SpannableString(c);
        return ss;
    }




}
