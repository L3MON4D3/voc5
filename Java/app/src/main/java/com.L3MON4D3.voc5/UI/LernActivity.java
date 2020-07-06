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

public class LernActivity  extends VocActivity {
    ImageButton checkbtn;
    Button finishbtn;
    Random rad = new Random();
    int currentP;
    Vocab currentVoc;

    ArrayList<Vocab> Vocs;
    TextView textViewQuestion;
    TextView textViewLanguage;
    EditText editTextAnswer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Vocs =savedInstanceState.getParcelableArrayList("ArrayListLern");
        }else if (getIntent().hasExtra("ArrayListLern")) {
            Vocs = getIntent().getExtras().getParcelableArrayList("ArrayListLern");
        }

        if(Vocs==null){
            if (!client.hasVocabs()) {
                client.loadVocs(() -> {
                    Vocs=client.getVocabs();
                    tolern();
                });
            } else {
                Vocs=client.getVocabs();
                currentVoc= tolern();
            }

        }



        setContentView(R.layout.lern_activity2);
        textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);
        textViewLanguage = (TextView) findViewById(R.id.textViewLanguage);
        editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        finishbtn =(Button) findViewById(R.id.finishbtn);
        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LernActivity.this,GalleryActivity.class));
            }
        });
        checkbtn = (ImageButton) findViewById(R.id.checkbtn);
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentAns = editTextAnswer.getText().toString();
                if(currentAns.equals(editTextAnswer)){
                    currentP= currentVoc.getPhase()+1;
                    currentVoc.setPhase(currentP);
                    client.updateVocRqst(currentVoc);
                    currentVoc=tolern();
                }
                Intent startIntent = new Intent(getApplicationContext(), Pop.class);
                startIntent.putExtra("UserAnswer", editTextAnswer.getText().toString());
                startIntent.putExtra("rightAnswer", currentVoc.getAnswer());
                startIntent.putExtra("phase", currentVoc.getPhase());
                startActivityForResult(startIntent, currentP);

            }
        });



    }
    public Vocab  tolern(){
        int rn = rad.nextInt(Vocs.size());
        currentVoc = Vocs.get(rn);
        Vocs.remove(rn);
        window(currentVoc);
        return currentVoc;

    }
    public void window(Vocab currentVoc){
        textViewQuestion.setText(currentVoc.getQuestion());
        textViewLanguage.setText(currentVoc.getLanguage());

    }
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelableArrayList("ArrayListLern", Vocs);
        super.onSaveInstanceState(sis);
    }
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == currentP) {
            if (resultCode == RESULT_OK) {
                client.updateVocRqst(currentVoc);
                currentVoc=tolern();

            }
        }
    }

}

