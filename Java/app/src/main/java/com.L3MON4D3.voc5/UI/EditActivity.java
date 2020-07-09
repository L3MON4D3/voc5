package com.L3MON4D3.voc5.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import android.util.Log;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;
import com.google.android.material.textfield.TextInputLayout;

public class EditActivity extends AppCompatActivity {
    EditText newQuestionEt;
    EditText newLanguageEt;
    EditText newAnswerEt;
    EditText newPhaseEt;
    TextInputLayout phaseInputLayout;
    Button commitChangesBtn;
    Button abortChangesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);

        commitChangesBtn = findViewById(R.id.commitChangeBtn);
        abortChangesBtn = findViewById(R.id.abortChangeBtn);

        newQuestionEt = findViewById(R.id.newQuestionEt);
        newAnswerEt = findViewById(R.id.newAnswerEt);
        newLanguageEt = findViewById(R.id.newLanguageEt);
        newPhaseEt = findViewById(R.id.newPhaseEt);
        phaseInputLayout = findViewById(R.id.phaseInputLayout);

        //Check if a legal Phase is already set to not allow empty Phase or Phase 0
        if(newPhaseEt.getText().toString().equals("") || newPhaseEt.getText().toString().equals("0")) {
            phaseInputLayout.setError("Phase muss zwischen 1 und 5 sein");
            commitChangesBtn.setEnabled(false);
        }

        newPhaseEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(newPhaseEt.getText().toString().equals("") || newPhaseEt.getText().toString().equals("0")) {
                    phaseInputLayout.setError("Phase muss zwischen 1 und 5 sein");
                    commitChangesBtn.setEnabled(false);
                } else if(Integer.parseInt(newPhaseEt.getText().toString()) > 5) {
                    phaseInputLayout.setError("Phase muss zwischen 1 und 5 liegen");
                    commitChangesBtn.setEnabled(false);
                } else {
                    phaseInputLayout.setError("");
                    commitChangesBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //set Edit Texts to  content that should be edited
        if(getIntent().hasExtra("com.L3MON4D3.voc5.Voc")) {
            Vocab tmp = getIntent().getExtras().getParcelable("com.L3MON4D3.voc5.Voc");
            newQuestionEt.setText(tmp.getQuestion());
            newAnswerEt.setText(tmp.getAnswer());
            newLanguageEt.setText(tmp.getLanguage());
            newPhaseEt.setText(String.valueOf(tmp.getPhase()));
        }

        newPhaseEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                Log.e("voc5", String.valueOf(actionId));
                //Found out key with log.
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    commitChanges();
                    handled = true;
                }
                return handled;
            }
        });


        commitChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitChanges();
            }
        });

        abortChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });
    }

    /**
     * Send changes back to GalleryActivity.
     */
    public void commitChanges() {
        //Creates a new Voc that will be changed on server, ID must be set to change Voc
        String newQuestion = newQuestionEt.getText().toString();
        String newAnswer = newAnswerEt.getText().toString();
        String newLanguage = newLanguageEt.getText().toString().toUpperCase();
        int newPhase = Integer.parseInt(newPhaseEt.getText().toString());

        Vocab newVoc = new Vocab(newAnswer, newQuestion, newLanguage);
        Intent resultIntent = new Intent(getApplicationContext(),GalleryActivity.class);
        newVoc.setPhase(newPhase);
        resultIntent.putExtra("com.L3MON4D3.voc5.newVoc", newVoc);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
