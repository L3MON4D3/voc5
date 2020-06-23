package com.L3MON4D3.voc5.UI;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import android.util.Log;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;

public class GalleryCardFactory {
    private LayoutInflater inf;
    private ViewGroup parent;
    private int cardWidth;

    /**
     * Creates new GalleryCardFactory.
     * @param inf LayoutInflater, get with getLayoutInflater().
     * @param parent ViewGroup the Card will be added to.
     */
    public GalleryCardFactory(LayoutInflater inf, ViewGroup parent, int cardWidth) {
        this.inf = inf;
        this.parent = parent;
        this.cardWidth = cardWidth;
    }

    /**
     * Creates new GalleryCard.
     * @param voc Vocab associated with Card.
     * @return GalleryCard-Object.
     */
    public GalleryCard newCard(Vocab voc) {
        GalleryCard gc = (GalleryCard) inf.inflate(R.layout.gallery_card, parent, false);
        gc.setVoc(voc);
        gc.getLayoutParams().width = cardWidth;
        ((TextView) gc.getChildAt(0)).setText(voc.getQuestion());
        ((TextView) gc.getChildAt(1)).setText(voc.getLanguage());
        return gc;
    }
}
