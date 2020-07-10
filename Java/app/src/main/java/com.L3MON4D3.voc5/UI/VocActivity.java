package com.L3MON4D3.voc5.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;


import com.L3MON4D3.voc5.Client.Voc5Client;
import com.L3MON4D3.voc5.R;

/**
 * This class serves as a superclass for Classes that require a client.
 * The client is saved over destruction and recreation of the Activity.
 * If the Intent starting this Activity contains a "client"-extra it will be set.
 * @author Simon Katz.
 */
public class VocActivity extends LoadingInfoActivity {
    protected Voc5Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Bundle ex = getIntent().getExtras();
        if (savedInstanceState == null) {
            if (ex != null)
                client = ex.getParcelable("client");
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
