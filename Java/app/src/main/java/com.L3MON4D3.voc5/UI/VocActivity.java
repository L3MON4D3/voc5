package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.L3MON4D3.voc5.Client.Voc5Client;
import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;

public class VocActivity extends AppCompatActivity {
    protected Voc5Client client;
    private static final int EDIT_RESULT = 21;
    private Vocab editVoc;
    private int editId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Bundle ex = getIntent().getExtras();
        if (savedInstanceState == null) {
            if (ex != null) {
                client = ex.getParcelable("client");
                if (client == null)
                    client = new Voc5Client("https://api.voc5.org","l3mon@4d3.org","foo");
            } else
                client = new Voc5Client("https://api.voc5.org","l3mon@4d3.org","foo");
        } else {
            client = savedInstanceState.getParcelable("client");
        }
    }

    /**
     *
     */
    public void startLearn(){
        Intent startIntent = new Intent(getApplicationContext(), LernActivity.class);
        startActivity(startIntent);
    }

    /**
     *Starts Gallery activity and passes proper Intent
     */
    public void startGallery(){
        Intent startIntent = new Intent(getApplicationContext(), GalleryActivity.class);
        startActivity(startIntent);
    }

    /**
     * Starts newVoc activity and passes proper Intent
      */
    public void startNewVoc(){
        Intent startIntent = new Intent(getApplicationContext(),MainMenuActivity.class);//todo add activity for creating new Voc
        //todo: add proper extra to Intent
        startActivity(startIntent);
    }

    /**
     * Starts editVoc activity and passes ID of Vocabulary meant to be edited
     */
    public void startEditVoc(Vocab voc){
        editVoc = voc;
        editId= client.getVocabs().indexOf(voc);
        int index = client.getVocabs().indexOf(voc);
        Intent startIntent = new Intent(getApplicationContext(),EditActivity.class);
        startIntent.putExtra("com.L3MON4D3.voc5.Voc", voc);
        startActivityForResult(startIntent,EDIT_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_RESULT)
            if (resultCode == RESULT_OK) {
                Vocab tmp = data.getExtras().getParcelable("com.L3MON4D3.voc5.newVoc");
                editVoc.setQuestion(tmp.getQuestion());
                editVoc.setAnswer(tmp.getAnswer());
                editVoc.setLanguage(tmp.getLanguage());
                editVoc.setPhase(tmp.getPhase());
                client.getVocabs().set(editId, editVoc);
                client.updateVocRqst(editVoc);
            }

    }

    /**
     * Save client for next Instance.
     */
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelable("client", client);
        super.onSaveInstanceState(sis);
    }
}
