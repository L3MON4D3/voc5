package com.L3MON4D3.voc5.UI;

import android.content.Context;
import android.app.Activity;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

import com.L3MON4D3.voc5.Client.Vocab;

public class GalleryCard extends CardView {
    private Vocab voc;

    public GalleryCard(Context con, AttributeSet attrs) {
        super(con, attrs);
    }

    public void setVoc(Vocab voc) { this.voc = voc ; }
}
