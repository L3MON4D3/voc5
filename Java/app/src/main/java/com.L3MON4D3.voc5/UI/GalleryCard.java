package com.L3MON4D3.voc5.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.L3MON4D3.voc5.Client.Vocab;

import org.jetbrains.annotations.NotNull;

/**
 * A Card that is used in GalleryActivity and displays Information on one Vocab.
 * Do not use Constructor in class, use GalleryCardFactory for obtaining an instance of GalleryCard.
 * @author Simon Katz.
 */
public class GalleryCard extends CardView implements Comparable<GalleryCard> {
    private Vocab voc;
    private boolean displaysAnswer = false;
    private boolean selected = false;
    private static GalleryActivity ga;
    //Position in Parent Layout, needed for saving selected Cards.
    //DO NOT CHANGE.
    public int parentPos;

    private float defaultElevation;
    private GestureDetector gestDect = new GestureDetector(
        getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent me) {
                return true;
            }

            public boolean onSingleTapUp(MotionEvent me) {
                //if a Card has been selected already, select on tap.
                if (ga.getSelCount() > 0)
                    selectToggle();
                else
                    textToggle();
                return true;
            }

            public void onLongPress(MotionEvent me) {
                if (ga.getSelCount() > 0)
                    selectToThis();
                else
                    selectToggle();
            }
        }
    );

    public GalleryCard(Context con, AttributeSet attrs) {
        super(con, attrs);
        defaultElevation = getElevation();
    }

    public static void setGa(GalleryActivity ga) { GalleryCard.ga = ga; }
    public void setVoc(Vocab voc) { this.voc = voc ; }
    public Vocab getVoc() { return voc; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public boolean getSelected() { return selected; }

    /**
     * Process motion Event.
     * @param me MotionEvent.
     * @return true if Motion processed.
     */
    public boolean onTouchEvent(MotionEvent me) {
        if (isEnabled())
            return gestDect.onTouchEvent(me) || super.onTouchEvent(me);
        return false;
    }

    /**
     * Select/deselect Card, set/reset elevation.
     */
    public void selectToggle() {
        if (selected) {
            ga.deselect(this);
        } else {
            ga.select(this);
        }
    }

    /**
     * Select Cards between this and last selected Card.
     */
    public void selectToThis() {
        ga.selectActionRepFromLast(this);
    }

    /**
     * Toggle displayed Text, either Answer or Question.
     */
    public void textToggle() {
        if (displaysAnswer)
            ((TextView) getChildAt(0)).setText(voc.getQuestion());
        else
            ((TextView) getChildAt(0)).setText(voc.getAnswer());
        displaysAnswer = !displaysAnswer;
    }

    /**
     * Raise Card to highlight it.
     */
    public void elevate() {
        setElevation(30f);
    }

    /**
     * Lower Card to un-highlight.
     */
    public void resetElevation() {
        setElevation(defaultElevation);
    }

    /**
     * Update Textfields as voc might have changed.
     */
    public void refresh() {
        if (displaysAnswer) {
            ((TextView) getChildAt(0)).setText(voc.getAnswer());
        } else {
            ((TextView) getChildAt(0)).setText(voc.getQuestion());
        }
        ((TextView) getChildAt(1)).setText(voc.getLanguage().toUpperCase());
        ((TextView) getChildAt(2)).setText(voc.getPhaseString());
    }

    /**
     * Compares Cards based on parentPosition.
     * @param other Other GalleryCard.
     * @return int, negative if this is less than other, positive if it is greater,
     * 0 if equal.
     */
    public int compareTo(GalleryCard other) {
        return this.parentPos - other.parentPos;
    }

    /**
     * Get String describing this.
     * @return String, "parentPos:__, Voc:__"
     */
    public String toString() {
        return "pos: "+parentPos+", Voc:"+voc.toString();
    }
}
