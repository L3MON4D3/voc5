package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.util.ArrayList;

import com.L3MON4D3.voc5.Client.*;
import com.L3MON4D3.voc5.R;


public class GalleryActivity extends VocActivity {
    private GridLayout gallery;
    private GalleryCardFactory gcf;
    private ArrayList<Vocab> vocs;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        gallery = findViewById(R.id.gall_lt);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;

        //margin of GridLayout gallery * 2 in pixels.
        int ltBorderPx = (int)(40 * density);

        int width = displayMetrics.widthPixels - ltBorderPx;

        //approximate number of Cards that fit beside each other(cardWidth+marginLeft+marginRight).
        int colCount = (int)(width/(185*density));

        gallery.setColumnCount(colCount);
        gcf = new GalleryCardFactory(getLayoutInflater(), gallery, width/colCount-(int)(10*density));

        client.loadVocs(() -> {
            ArrayList<Vocab> vocs = client.getVocabs();
            runOnUiThread(() -> populateGallery(vocs));
        });
    }

    /**
     * Adds Vocabs to gallery.
     * @param vocs Vocabulary to be added to Gallery.
     */
    public void populateGallery(ArrayList<Vocab> vocs) {
        this.vocs = vocs;
        int i = 0;
        for (Vocab v : vocs) {
            gallery.addView(gcf.newCard(v), i++);
        }
    }
}