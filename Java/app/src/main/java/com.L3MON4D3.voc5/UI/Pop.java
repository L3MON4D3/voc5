package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;



import com.L3MON4D3.voc5.R;

import java.util.ArrayList;

public class Pop extends VocActivity {
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
        findf(UserAnswer,RightAnswer);

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

    public void findf(String user,String answer) {
        ArrayList<Boolean> temp = new ArrayList<Boolean>();
        for (int i = 0; i <= user.length() - 1; i++) {
            if (user.charAt(i) == answer.charAt(i)) {
                temp.add(true);
            } else {
                temp.add(false);
            }
        }
        window(temp);
    }
    public void window(ArrayList<Boolean> b) {
        String mtext = UserAnswer;
        char[] f;
        f=UserAnswer.toCharArray();
       
        SpannableString ss = new SpannableString(mtext);
        BackgroundColorSpan bcsRed = new BackgroundColorSpan(Color.RED);
        for(int i= 0; i<=textViewUserAnswer.getText().length()-1; i++){
            if(b.get(i)) {
               ss.setSpan(bcsRed, i,i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        textViewUserAnswer.setText(ss);
    }



}
