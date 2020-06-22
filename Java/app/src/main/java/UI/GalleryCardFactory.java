package com.L3MON4D3.voc5;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class GalleryCardFactory {
    private LayoutInflater inf;
    private ViewGroup parent;

    /**
     * Creates new GalleryCardFactory.
     * @param inf LayoutInflater, get with getLayoutInflater().
     * @param parent ViewGroup the Card will be added to.
     */
    public GalleryCardFactory(LayoutInflater inf, ViewGroup parent) {
        this.inf = inf;
        this.parent = parent;
    }

    /**
     * Creates new GalleryCard.
     * @param voc Vocab associated with Card.
     * @return GalleryCard-Object.
     */
    public GalleryCard newCard(Vocab voc) {
        GalleryCard gc = (GalleryCard) inf.inflate(R.layout.gallery_card, parent, false);
        ((TextView) gc.getChildAt(0)).setText("meintext");
        return gc;
    }
}
