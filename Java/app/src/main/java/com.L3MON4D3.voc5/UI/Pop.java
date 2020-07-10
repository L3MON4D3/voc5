package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.L3MON4D3.voc5.R;
import java.util.ArrayList;
/**
 * Pop checks the user response. To do this, the errors are marked in the user response.
 * The user can decide whether the answer given is correct or incorrect.
 * The vocabulary phase is then adjusted.
 * @author Katharina Klein
 */

public class Pop extends AppCompatActivity {
    TextView textViewUserAnswer;
    TextView textViewRigthAnswer;
    Button rigthbtn;
    Button wrongbtn;
    String se = new String("");
    ArrayList<Boolean> b = new ArrayList<>();
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
        //checked if UserAnswer, RigthAnswer and the phase was passed
        if (getIntent().hasExtra("userAnswer")) {
            UserAnswer = getIntent().getExtras().getString("userAnswer");
        }
        if (getIntent().hasExtra("rightAnswer")) {
            RightAnswer = getIntent().getExtras().getString("rightAnswer");
        }
        if (getIntent().hasExtra("phase")) {
            currentp = getIntent().getExtras().getInt("phase");
        }
        textViewRigthAnswer.setText(RightAnswer);
        rekfindf(UserAnswer,RightAnswer,0);
        window(b,se);
        //phase is increased, is returned to LernActivity
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
        //phase is decreased, is returned to LernActivity
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
    /*
    recursive method to calculate the error of UserAnswer,
    overwriting string se and ArrayList b
    returns the costs k
     */
    public int rekfindf(String s1,String s2, int k) {
        if(Math.max(s1.length(),s2.length())==1){
            if(s1.length()==0){
                k++;
                se+=' ';
                b.add(false);
            }else if(s2.length()==0){
                k++;
                se+=s1.charAt(0);
                b.add(false);
            }else{
                if (s1.charAt(0) == s2.charAt(0)) {
                    b.add(true);
                    se+=s1.charAt(0);
                }else{
                    k++;
                    b.add(false);
                    se+=s1.charAt(0);
                }
            }
            return k;
        }else{
            if(s1.length()==0){
                se+=' ';
                b.add(false);
                return rekfindf(s1, s2.substring(1,s2.length()),k+1);
            }else if(s2.length()==0){
                b.add(false);
                se+=s1.charAt(0);
                return rekfindf(s1.substring(1,s1.length()), s2,k+1);
            }else{
                if(s1.charAt(0)==s2.charAt(0)){
                    b.add(true);
                    se+=s1.charAt(0);
                    return (rekfindf(s1.substring(1,s1.length()), s2.substring(1,s2.length()),k));
                }else{
                    b.add(false);
                    if((rekfindf2(s1, s2.substring(1,s2.length()),k+1))<(rekfindf2(s1.substring(1,s1.length()), s2,k+1))){
                        se+=' ';
                        return rekfindf(s1, s2.substring(1,s2.length()),k+1);
                    }else if((rekfindf2(s1.substring(1,s1.length()), s2,k+1))<(rekfindf2(s1, s2.substring(1,s2.length()),k+1))){
                        se+=s1.charAt(0);
                        return rekfindf(s1.substring(1,s1.length()), s2,k+1);
                    }else{
                        se+=s1.charAt(0);
                        return rekfindf(s1.substring(1,s1.length()), s2.substring(1,s2.length()),k+1);
                    }
                }
            }
        }
    }
    /*
    recursive method to calculate the error of the UserAnswer,
    does not overwrite string se and  ArrayList b
    returns only the costs k
   */
    public int rekfindf2(String s1,String s2, int k) {

        if(Math.max(s1.length(),s2.length())==1){
            if(s1.length()==0){
                k++;
            }else if(s2.length()==0){
                k++;
            }else{
                if (s1.charAt(0) != s2.charAt(0)) {
                    k++;
                }
            }
            return k;
        }else{
            if(s1.length()==0){
                return rekfindf2(s1, s2.substring(1,s2.length()),k+1);
            }else if(s2.length()==0){
                return rekfindf2(s1.substring(1,s1.length()), s2,k+1);
            }else{
                if(s1.charAt(0)==s2.charAt(0)){
                    return (rekfindf2(s1.substring(1,s1.length()), s2.substring(1,s2.length()),k));
                }else{
                    if((rekfindf2(s1, s2.substring(1,s2.length()),k))<(rekfindf2(s1.substring(1,s1.length()), s2,k))){
                        return rekfindf2(s1, s2.substring(1,s2.length()),k+1);
                    }else if((rekfindf2(s1.substring(1,s1.length()), s2,k))<(rekfindf2(s1, s2.substring(1,s2.length()),k))){
                        return rekfindf2(s1.substring(1,s1.length()), s2,k+1);
                    }else {
                        return rekfindf2(s1.substring(1,s1.length()), s2.substring(1,s2.length()),k+1);
                    }
                }

            }

        }

    }
    /*
    colors the string at position i, if bn at position i is "false"
     */
    public void window(ArrayList<Boolean> bn,String cAns) {
       SpannableString spaSt = new SpannableString(cAns);
        for(int i =0; i<cAns.length(); i++){
            if(!bn.get(i)){
                spaSt.setSpan(new BackgroundColorSpan(Color.argb(150,178,223,238)),i,i+1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textViewUserAnswer.setText(spaSt);
        se ="";
        b.clear();
    }
}
