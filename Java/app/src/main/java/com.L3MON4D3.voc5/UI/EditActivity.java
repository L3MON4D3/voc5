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

        newQuestionEt = findViewById(R.id.newQuestionEt);
        newAnswerEt = findViewById(R.id.newAnswerEt);
        newLanguageEt = findViewById(R.id.newLanguageEt);
        newPhaseEt = findViewById(R.id.newPhaseEt);



        commitChangesBtn = findViewById(R.id.commitChangeBtn);
        commitChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creates a new Voc that will be changed on server, ID must be set to change Voc
                String newQuestion = newQuestionEt.getText().toString();
                String newAnswer = newAnswerEt.getText().toString();
                String newLanguage = newLanguageEt.getText().toString();
                int newPhase = 0;
                if(!newPhaseEt.getText().toString().equals(""))
                    newPhase = Integer.parseInt(newPhaseEt.getText().toString());


                Vocab newVoc = new Vocab(newQuestion, newAnswer, newLanguage);
                newVoc.setPhase(newPhase);
                newVoc.setId(vocId);
                client.updateVocRqst(newVoc);

                Intent startIntent = new Intent(getApplicationContext(),GalleryActivity.class);
                startIntent.putExtra("com.L3MON4D3.voc5",newQuestion);//adds Question as Extra to Intent
                startIntent.putExtra("com.L3MON4D3.voc5", newAnswer);//adds Answer as Extra to Intent
                startIntent.putExtra("com.L3MON4D3.voc5", newLanguage);//adds Language as Extra to Intent
                startIntent.putExtra("com.L3MON4D3.voc5", newPhase);//adds Phase as extra to Intent
                startActivity(startIntent);
            }
        });
    }

}
