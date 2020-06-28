package com.L3MON4D3.voc5.UI;

import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.L3MON4D3.voc5.Client.Vocab;

public class GalleryCard extends CardView {
    private Vocab voc;
    private boolean displaysAnswer = false;
    private boolean selected = false;
    private static int selectCount = 0;

    private float defaultElevation;
    private GestureDetector gestDect = new GestureDetector(
        getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent me) {
                return true;
            }

            public boolean onSingleTapUp(MotionEvent me) {
                //if a Card has been selected already, select on tap.
                if (selectCount > 0)
                    selectToggle();
                else
                   textToggle();
                return true;
            }

            public void onLongPress(MotionEvent me) {
                selectToggle();
            }
        }
    );

    public GalleryCard(Context con, AttributeSet attrs) {
        super(con, attrs);
        defaultElevation = getElevation();
    }

    public void setVoc(Vocab voc) { this.voc = voc ; }

    /**
     * Process motion Event.
     * @param me MotionEvent.
     * @return true if Motion processed.
     */
    public boolean onTouchEvent(MotionEvent me) {
        return gestDect.onTouchEvent(me) || super.onTouchEvent(me);
    }

    /**
     * Select/deselect Card, set/reset elevation.
     */
    public void selectToggle() {
        if (selected) {
            setElevation(defaultElevation);
            selectCount--;
        } else {
           setElevation(30f);
            selectCount++;
        }
        selected = !selected;
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
}
