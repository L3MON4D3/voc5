package com.L3MON4D3.voc5.UI;

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
}
