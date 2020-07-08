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
import com.L3MON4D3.voc5.Client.IntPair;
import com.L3MON4D3.voc5.R;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Arrays;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LernActivity extends VocActivity {
    ImageButton checkbtn;
    Button finishbtn;
    Random rad = new Random();
    Vocab currentVoc;
    private static final int POPREQ=11;

    ArrayList<Vocab> vocs;
    IntPair[] newPhases;
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
            newPhases=(IntPair[]) savedInstanceState.getParcelableArray("newPhases");
            tolern();
        }else {
                vocs = getIntent().getExtras().getParcelableArrayList("ArrayListLern");
                newPhases = new IntPair[vocs.size()];
                for(int i=0; i!=vocs.size();i++){
                    Vocab v = vocs.get(i);
                    newPhases[i] = new IntPair(v.getId(), v.getPhase());
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
        sis.putParcelableArray("newPhases", newPhases);

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
        client.getOkClient().newCall(client.updateVocRqst(v)).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("voc5:", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("voc5:", response.body().string());
            }
        });
        for(int i =0; i!=newPhases.length;i++){
            if(newPhases[i].first == v.getId()){
                newPhases[i].second = v.getPhase();
                return;
            }
        }
    }
}

