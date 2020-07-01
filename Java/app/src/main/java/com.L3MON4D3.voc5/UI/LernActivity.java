package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Random;
import java.util.ArrayList;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;
import android.content.Intent;



import java.util.ArrayList;



public class LernActivity  extends VocActivity  {
    Button checkbtn = (Button) findViewById(R.id.checkbtn);
    Button finishbtn = (Button) findViewById(R.id.finishbtn);
    TextView textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);
    TextView textViewLanguage = (TextView) findViewById(R.id.textViewLanguage);
    EditText editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);

    Vocab currentVoc = new Vocab();
    Random rad = new Random();
    int currentP;
    private static final int g = 0;
    

    ArrayList<Vocab> Vocs = new ArrayList<Vocab>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_activity);

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(startIntent);
            }
        });
        if (savedInstanceState != null) {
            if (getIntent().hasExtra("ArrayListLern")) {
                Vocs = getIntent().getExtras().getParcelableArrayList("ArrayListLern");
            } else {
                Vocs = client.getVocabs();
            }
        }

            currentVoc = tolern();

           window();

        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentVoc = tolern();
                Intent startIntent = new Intent(getApplicationContext(), Pop.class);
                startIntent.putExtra("UserAnswer", editTextAnswer.getText().toString());
                startIntent.putExtra("rightAnswer", currentVoc.getAnswer());
                startIntent.putExtra("phase", currentVoc.getPhase());
                startActivityForResult(startIntent, g);
            }
        });

    }
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelableArrayList("ArrayListLern", Vocs);
        super.onSaveInstanceState(sis);
    }
    public Vocab  tolern(){
            int rn = rad.nextInt(Vocs.size());
            currentVoc = Vocs.get(rn);
            Vocs.remove(rn);
            return currentVoc;

    }
    public void onResult(int requestCode, int resultCode, Intent data){
        if(requestCode==g){
            if(resultCode==RESULT_OK){
                //Phase an den Server zurückgeben
                tolern();

            }
        }
    }
    public void window(){
        textViewQuestion.setText(currentVoc.getQuestion());
        textViewLanguage.setText(currentVoc.getLanguage());
        String currentAns = editTextAnswer.getText().toString();
    }
}

