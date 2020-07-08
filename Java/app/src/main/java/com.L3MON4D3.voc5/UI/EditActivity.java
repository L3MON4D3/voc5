package com.L3MON4D3.voc5.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;

public class EditActivity extends AppCompatActivity {
    EditText newQuestionEt;
    EditText newLanguageEt;
    EditText newAnswerEt;
    EditText newPhaseEt;
    Button commitChangesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);

        newQuestionEt = findViewById(R.id.newQuestionEt);
        newAnswerEt = findViewById(R.id.newAnswerEt);
        newLanguageEt = findViewById(R.id.newLanguageEt);
        newPhaseEt = findViewById(R.id.newPhaseEt);

        //set Edit Texts to  content that should be edited
        if(getIntent().hasExtra("com.L3MON4D3.voc5.Voc")) {
            Vocab tmp = getIntent().getExtras().getParcelable("com.L3MON4D3.voc5.Voc");
            newQuestionEt.setText(tmp.getQuestion());
            newAnswerEt.setText(tmp.getAnswer());
            newLanguageEt.setText(tmp.getLanguage());
            newPhaseEt.setText(String.valueOf(tmp.getPhase()));
        }

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

                Vocab newVoc = new Vocab(newAnswer, newQuestion, newLanguage);
                Intent resultIntent = new Intent(getApplicationContext(),GalleryActivity.class);
                newVoc.setPhase(newPhase);
                resultIntent.putExtra("com.L3MON4D3.voc5.newVoc", newVoc);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
