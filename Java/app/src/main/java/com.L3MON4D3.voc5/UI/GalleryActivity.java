package com.L3MON4D3.voc5.UI;

import android.os.Bundle;

import android.util.DisplayMetrics;

import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.ArrayList;

import com.L3MON4D3.voc5.Client.*;
import com.L3MON4D3.voc5.R;


public class GalleryActivity extends VocActivity {
    private GridLayout gallery;
    private GalleryCardFactory gcf;
    private ArrayList<Vocab> currentVocs;
    private ArrayList<GalleryCard> selected;
    private Button galleryEditBtn;
    private Button galleryLearnBtn;
    private Button galleryDeleteBtn;
    private float density;
    private int selCount = 0;

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
        gcf = new GalleryCardFactory(getLayoutInflater(), gallery, width/colCount-(int)(10*density), this);

        selected = new ArrayList<GalleryCard>();
        if (savedInstanceState == null) {
            if (client.hasVocabs()) {
                client.loadVocs(() -> {
                    ArrayList<Vocab> currentVocs = client.getVocabs();
                    runOnUiThread(() -> setGalleryContent(currentVocs));
                });
            } else {
                setGalleryContent(client.getVocabs());
            }
        } else {
            setGalleryContent(
                savedInstanceState.getParcelableArrayList("currentVocs"),
                savedInstanceState.getIntArray("selectedPos"));
        }
        galleryEditBtn = findViewById(R.id.galleryEditBtn);
        galleryLearnBtn = findViewById(R.id.galleryLearnBtn);
        galleryDeleteBtn = findViewById(R.id.galleryDeleteBtn);

        galleryEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditVoc(selected.get(0).getVoc());
            }
        });

        galleryLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLearn();
            }
        });
    }

    public int getSelCount() { return selCount; }

    /**
     * Add Card to selected, increase counter.
     * @param gc Card to select.
     */
    public void select(GalleryCard gc) {
        if (gc.getSelected())
            return;
        selected.add(gc);
        gc.elevate();
        gc.setSelected(true);
        selCount++;
    }

    /**
     * Remove Card from selected, decrease counter.
     * @param gc Card to select.
     */
    public void deselect(GalleryCard gc) {
        if (!gc.getSelected())
            return;
        selected.remove(gc);
        gc.resetElevation();
        gc.setSelected(false);
        selCount--;
    }

    /**
     * Adds Vocabs to gallery.
     * @param currentVocs Vocabulary to be added to Gallery.
     */
    public void setGalleryContent(ArrayList<Vocab> currentVocs) {
        //no Cards are selected <-> Card with index -1 is selected.
        setGalleryContent(currentVocs, new int[] {-1});
    }

    /**
     * Adds Vocabs to gallery.
     * @param currentVocs Vocabulary to be added to Gallery.
     * @param selectedPos indizes of Cards that are selected.
     */
    public void setGalleryContent(ArrayList<Vocab> currentVocs, int[] selectedPos) {
        if (selectedPos.length == 0) {
            selectedPos = new int[] {-1};
        } else {
            this.currentVocs = currentVocs;
            int selectedArrInd = 0;
            for (int i = 0; i != currentVocs.size(); i++) {
                GalleryCard gc = gcf.newCard(currentVocs.get(i), i);
                if (selectedArrInd < selectedPos.length && i == selectedPos[selectedArrInd]) {
                    select(gc);
                    selectedArrInd++;
                }
                gallery.addView(gc, i);
            }
        }
    }

    /**
     * Select Cards between first and second.
     * @param first GalleryCard, may be before or after second.
     * @param second GalleryCard, may be before or after first.
     */
    public void selectRange(GalleryCard first, GalleryCard second) {
        int pos1 = first.parentPos;
        int pos2 = second.parentPos;
        boolean goUp = pos1 < pos2;
        select(first);
        while (pos1 != pos2) {
            select((GalleryCard) gallery.getChildAt(pos1));
            if (goUp)
                pos1++;
            else
               pos1--;
        }
        select(second);
    }

    /**
     * Save currentVocs for next Instance.
     */
    public void onSaveInstanceState(Bundle sis) {
        sis.putParcelableArrayList("currentVocs", currentVocs);
        int[] selectedArr = new int[selCount];
        for (int i = 0; i != selCount; i++)
            selectedArr[i] = selected.get(i).parentPos;
        sis.putIntArray("selectedPos", selectedArr);
        super.onSaveInstanceState(sis);
    }
}
