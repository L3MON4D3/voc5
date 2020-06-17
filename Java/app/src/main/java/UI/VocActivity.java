package com.L3MON4D3.voc5;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class VocActivity extends AppCompatActivity {
    Voc5Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        client = getIntent().getExtras().getParcelable("client");
        if (client == null)
            client = new Voc5Client("https://api.voc5.org","test1@example.com","foo");
    }
}