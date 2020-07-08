package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;


import com.L3MON4D3.voc5.Client.Voc5Client;
import com.L3MON4D3.voc5.R;

public class VocActivity extends LoadingInfoActivity {
    protected Voc5Client client;

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
     * Save client for next Instance.
     */
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelable("client", client);
        super.onSaveInstanceState(sis);
    }
}
