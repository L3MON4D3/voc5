package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;

public class EditActivity extends VocActivity{
    EditText newQuestionEt;
    EditText newLanguageEt;
    EditText newAnswerEt;
    EditText newPhaseEt;
    Button commitChangesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);

        //retrieve the ID of voc that needs to be changed
        int vocId = getIntent().getExtras().getInt("com.L3MON4D3.voc5");

        commitChangesBtn = findViewById(R.id.commitChangeBtn);
        commitChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creates a new Voc that will be changed on server, ID must be set to change Voc
                Vocab newVoc = vocBuilder();
                newVoc.setId(vocId);
                client.updateVocRqst(newVoc);
                Intent startIntent = new Intent(getApplicationContext(),GalleryActivity.class);
                startIntent.putExtra("com.L3MON4D3.voc5", vocId);
                startActivity(startIntent);
            }
        });
    }

    /**
     * Creates a vocab out of parameters entered into EditTexts when Button was pressed
     * @return changed Vocabulary to be sent to server without id;
     */
    public Vocab vocBuilder(){
        newQuestionEt = findViewById(R.id.newQuestionEt);
        newLanguageEt = findViewById(R.id.newLanguageEt);
        newAnswerEt = findViewById(R.id.newAnswerEt);


        String newQuestion = newQuestionEt.getText().toString();
        String newLanguage = newLanguageEt.getText().toString();
        String newAnswer = newAnswerEt.getText().toString();

        Vocab newVoc = new Vocab(newAnswer, newQuestion, newLanguage);
        newVoc.setPhase(createPhase());
        return newVoc;

    }

    /**
     * Makes a phase with content of EditText, if not a String it displays a Toast
     * @return newPhase for the voc
     */
    public int createPhase(){
        newPhaseEt = findViewById(R.id.newPhaseEt);
        String tmp = newPhaseEt.getText().toString();
        int newPhase;
        //check if input in EditText is a number
        try {
            newPhase = Integer.parseInt(tmp);
        }catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Phase muss eine Zahl sein",
                    Toast.LENGTH_SHORT);

            toast.show();
            newPhase= 0;//todo checken ob 0 als ID verwendet wird und fehler ID einrichten
        }
        return newPhase;
    }
}
