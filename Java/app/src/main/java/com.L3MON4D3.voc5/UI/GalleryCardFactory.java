package com.L3MON4D3.voc5.UI;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import android.util.Log;

import com.L3MON4D3.voc5.Client.Vocab;
import com.L3MON4D3.voc5.R;

/**
 * Used to instanciate GalleryCards either with or without Actions.
 * @author Simon Katz.
 */
public class GalleryCardFactory {
    private LayoutInflater inf;
    private ViewGroup parent;
    private int cardWidth;

    /**
     * Creates new GalleryCardFactory.
     * @param inf LayoutInflater, get with getLayoutInflater().
     * @param parent ViewGroup the Card will be added to.
     */
    public GalleryCardFactory(LayoutInflater inf, ViewGroup parent, int cardWidth, GalleryActivity ga) {
        this.inf = inf;
        this.parent = parent;
        this.cardWidth = cardWidth;
        if (ga != null)
            GalleryCard.setGa(ga);
    }

    /**
     * Creates new GalleryCard.
     * @param voc Vocab associated with Card.
     * @return GalleryCard-Object.
     */
    public GalleryCard newCard(Vocab voc, int pos) {
        GalleryCard gc = (GalleryCard) inf.inflate(R.layout.gallery_card, parent, false);
        gc.setVoc(voc);
        gc.getLayoutParams().width = cardWidth;
        gc.refresh();
        gc.parentPos = pos;
        return gc;
    }

    /**
     * Create new CardView with same Layout as GalleryCard.
     * @param voc Vocab associated with Card.
     * @return CardView that looks like a GalleryCard, but has no functionality.
     */
    public CardView newCardNoFunctionality(Vocab voc) {
        CardView cv = (CardView) inf.inflate(R.layout.gallery_card, parent, false);
        ((TextView) cv.getChildAt(0)).setText(voc.getQuestion());
        ((TextView) cv.getChildAt(1)).setText(voc.getLanguage().toUpperCase());
        ((TextView) cv.getChildAt(2)).setText(voc.getPhaseString());
        //Make cv ignore clicks.
        cv.setEnabled(false);
        return cv;
    }
}
