package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Arrays;

public class LernActivity  extends VocActivity {
    ImageButton checkbtn;
    Button finishbtn;
    Random rad = new Random();
    Vocab currentVoc;
    private static final int POPREQ=11;

    ArrayList<Vocab> vocs;
    int[] newPhases;
    int[] vocIds;
    TextView textViewQuestion;
    TextView textViewLanguage;
    EditText editTextAnswer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_activity2);
        textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);
        textViewLanguage = (TextView) findViewById(R.id.textViewLanguage);
        editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        finishbtn =(Button) findViewById(R.id.finishbtn);
        checkbtn = (ImageButton) findViewById(R.id.checkbtn);

        if (savedInstanceState != null) {
            vocs =savedInstanceState.getParcelableArrayList("ArrayListLern");
            vocIds=savedInstanceState.getIntArray("vocIds");
            newPhases=savedInstanceState.getIntArray("newPhases");
            tolern();
        }else {
                vocs = getIntent().getExtras().getParcelableArrayList("ArrayListLern");
                newPhases = new int[vocs.size()];
                vocIds = new int[vocs.size()];
                for(int i=0; i!=vocs.size();i++){
                    newPhases[i]= vocs.get(i).getPhase();
                    vocIds[i]= vocs.get(i).getId();
                }

                tolern();
        }

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent();
                startIntent.putExtra("newPhases", newPhases);
                setResult(RESULT_OK, startIntent);
                finish();
            }
        });
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentAns = editTextAnswer.getText().toString();
                if(currentAns.equals(currentVoc.getAnswer())){
                    currentVoc.incPhase();
                    saveChanges(currentVoc);
                    tolern();
                }else {
                    Intent startIntent = new Intent(getApplicationContext(), Pop.class);
                    startIntent.putExtra("userAnswer", editTextAnswer.getText().toString());
                    startIntent.putExtra("rightAnswer", currentVoc.getAnswer());
                    startIntent.putExtra("phase", currentVoc.getPhase());
                    startActivityForResult(startIntent, POPREQ);
                }

            }
        });

    }
    public void tolern() {
        if (!vocs.isEmpty()) {
            int rn = rad.nextInt(vocs.size());
            currentVoc = vocs.get(rn);
            vocs.remove(rn);
            window(currentVoc);

        } else {
            Intent startIntent = new Intent();
            startIntent.putExtra("newPhases", newPhases);
            setResult(RESULT_OK, startIntent);
            finish();
        }

    }
    public void window(Vocab currentVoc){
        textViewQuestion.setText(currentVoc.getQuestion());
        textViewLanguage.setText(currentVoc.getLanguage());
        editTextAnswer.setText("");

    }
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelableArrayList("ArrayListLern",vocs);
        sis.putIntArray("newPhases", newPhases);
        sis.putIntArray("vocIds",vocIds);
        super.onSaveInstanceState(sis);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POPREQ) {
            if (resultCode == RESULT_OK) {
                int newP=data.getExtras().getInt("phase");
                currentVoc.setPhase(newP);
                saveChanges(currentVoc);
                tolern();
            }
        }
    }
    public void saveChanges(Vocab v){
        client.updateVocRqst(currentVoc);
        for(int i =0; i<=vocIds.length;i++){
            if(vocIds[i]==currentVoc.getId()){
                newPhases[i] = v.getPhase();
                return;
            }
        }

    }

}

