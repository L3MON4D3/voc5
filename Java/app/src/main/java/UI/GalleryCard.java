package com.L3MON4D3.voc5;

import android.content.Context;
import android.app.Activity;
import android.util.AttributeSet;

import android.view.LayoutInflater;
import androidx.cardview.widget.CardView;

public class GalleryCard extends CardView {
    public static LayoutInflater inf;

    private Vocab voc;

    public GalleryCard(Context con, AttributeSet attrs) {
        super(con, attrs);
    }

    /**
     * Creates new GalleryCard.
     * @param voc Vocab associated with Card.
     * @return GalleryCard-Object.
     */
    public static GalleryCard newCard(Vocab voc) {
        return (GalleryCard) inf.inflate(R.layout.gallery_card, null);
    }
}
