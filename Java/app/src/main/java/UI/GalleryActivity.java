package com.L3MON4D3.voc5;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.GridLayout;

public class GalleryActivity extends VocActivity {
    GridLayout gl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        gl = findViewById(R.id.gall_lt);
        GalleryCardFactory gcf = new GalleryCardFactory(getLayoutInflater(), gl);
        gl.addView(gcf.newCard(new Vocab("Apple", "Apfel", "EN")), 0);
    }
}
