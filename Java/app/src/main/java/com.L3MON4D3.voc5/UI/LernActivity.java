package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.Random;
import java.util.ArrayList;
import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.Client.IntPair;
import com.L3MON4D3.voc5.R;
import android.content.Intent;
import android.util.DisplayMetrics;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class LernActivity extends VocActivity {
    Button checkbtn;
    Button finishbtn;
    Random rad = new Random();
    Vocab currentVoc;
    private static final int POPREQ=11;
    ArrayList<Vocab> vocs;
    IntPair[] newPhases;
    FrameLayout cardParent;
    GalleryCardFactory gcf;
    EditText editTextAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_activity2);

        cardParent = findViewById(R.id.cardParent_lt);
        editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        finishbtn =(Button) findViewById(R.id.finishbtn);
        checkbtn = findViewById(R.id.checkbtn);

        //Code from GalleryActivity to determine width of cards.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        
        //margin of GridLayout gallery * 2 in pixels.
        int ltBorderPx = (int)(40 * density);
        
        int width = displayMetrics.widthPixels - ltBorderPx;
        
        //approximate number of Cards that fit beside each other(cardWidth+marginLeft+marginRight).
        int colCount = (int)(width/(185*density));
        
        gcf = new GalleryCardFactory(getLayoutInflater(), cardParent, width/colCount-(int)(10*density), null);
        //gets vocs passed
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
                //first currentVoc is selected
                tolern();
        }
        //button to quit the activity
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
                //correct answer was given, popup does not start, new currentVoc is selected
                if (currentAns.equals(currentVoc.getAnswer())) {
                        currentVoc.incPhase();
                        saveChanges(currentVoc);
                        tolern();
                } else {
                    //Popup is started, userAnswer,rigthAnswer, and the phase of currentVoc is passed
                        Intent startIntent = new Intent(getApplicationContext(), Pop.class);
                        startIntent.putExtra("userAnswer", editTextAnswer.getText().toString());
                        startIntent.putExtra("rightAnswer", currentVoc.getAnswer());
                        startIntent.putExtra("phase", currentVoc.getPhase());
                        startActivityForResult(startIntent, POPREQ);
                }
            }
        });
    }
    /**
     * tolern() chooses a currentVoc and calls window()
     * if ArrayList vocs is empty, the GalleryActivity will start
     */
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
    /**
     * window() shows the current Question on Screen
     * and delete old answers
     */
    public void window(Vocab currentVoc){
        cardParent.addView(gcf.newCardNoFunctionality(currentVoc));
        editTextAnswer.setText("");
    }
    /**
     * onSaveInstanceState(Bundle sis) saves important ArrayLists
     * in case information get lost by starting Pop
     */
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelableArrayList("ArrayListLern",vocs);
        sis.putParcelableArray("newPhases", newPhases);
        super.onSaveInstanceState(sis);
    }
    /**
     * after Pop finish, onActivityResult refresh the currentVoc
     * and call tolern()
     */
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
     // saves the new phases, hands them over to GalleryActivity
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

