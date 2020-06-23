package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.L3MON4D3.voc5.Client.Voc5Client;
import com.L3MON4D3.voc5.R;

public class VocActivity extends AppCompatActivity {
    protected Voc5Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Bundle ex = getIntent().getExtras();
        if (ex != null) {
            client = ex.getParcelable("client");
            if (client == null)
                client = new Voc5Client("https://api.voc5.org","l3mon@4d3.org","foo");
        } else
            client = new Voc5Client("https://api.voc5.org","l3mon@4d3.org","foo");
    }

    /**
     *
     */
    public void startLearn(){
        Intent startIntent = new Intent(getApplicationContext(), MainMenuActivity.class);//todo add learn activity
        //todo: add proper extra to Intent
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
     * Starts newVoc activity and passes proper Intent()
      */
    public void startNewVoc(){
        Intent startIntent = new Intent(getApplicationContext(),MainMenuActivity.class);//todo add activity for creating new Voc
        //todo: add proper extra to Intent
        startActivity(startIntent);
    }
}
