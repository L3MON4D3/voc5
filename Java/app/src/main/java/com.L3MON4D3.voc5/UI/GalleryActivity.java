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
    //Contains references to Vocab in Voc5Client.
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
        currentVocs = new ArrayList<Vocab>();
        if (savedInstanceState == null) {
            if (client.hasVocabs()) {
                client.loadVocs(() -> {
                    runOnUiThread(() -> setGalleryContent(client.getVocabs()));
                });
            } else {
                setGalleryContent(client.getVocabs());
            }
        } else {
            setGalleryContent(
                savedInstanceState.getIntArray("currentVocs"),
                savedInstanceState.getIntArray("selectedPos"));
        }
        galleryEditBtn = findViewById(R.id.galleryEditBtn);
        galleryLearnBtn = findViewById(R.id.galleryLearnBtn);
        galleryDeleteBtn = findViewById(R.id.galleryDeleteBtn);

        galleryEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditVoc(selected.get(selCount - 1).getVoc());
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
     * Adds all Vocabs to gallery, selects none.
     * @param vocs Vocabulary to be added to Gallery.
     */
    public void setGalleryContent(ArrayList<Vocab> vocs) {
        for (int i = 0; i != vocs.size(); i++) {
            Vocab tmp = vocs.get(i);
            currentVocs.add(tmp);
            gallery.addView(gcf.newCard(tmp, i), i);
        }
    }

    /**
     * Adds Vocabs to gallery.
     * @param currentVocInd Vocabulary to be added to Gallery.
     */
    public void setGalleryContent(int[] currentVocInd) {
        //no Cards are selected <-> Card with index -1 is selected.
        setGalleryContent(currentVocInd, new int[] {-1});
    }

    /**
     * Adds Vocabs to gallery.
     * @param currentVocInd Vocabulary to be added to Gallery.
     * @param selectedPos indizes of Cards that are selected.
     */
    public void setGalleryContent(int[] currentVocInd, int[] selectedPos) {
        if (selectedPos.length == 0)
            selectedPos = new int[] {-1};

        int selectedArrInd = 0;
        ArrayList<Vocab> clientVocs = client.getVocabs();

        for (int i = 0; i != currentVocInd.length; i++) {
            Vocab tmp = clientVocs.get(currentVocInd[i]);
            currentVocs.add(tmp);
            GalleryCard gc = gcf.newCard(tmp, i);

            if (selectedArrInd < selectedPos.length && i == selectedPos[selectedArrInd]) {
                select(gc);
                selectedArrInd++;
            }
            gallery.addView(gc, i);
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
     * Selects from last selected Card to Card gc (inclusive).
     * if there is no last selected, simply selects gc.
     * @param gc GaleryCard.
     */
    public void selectFromLast(GalleryCard gc) {
        if (selCount == 0)
            select(gc);
        else
           selectRange(selected.get(selCount - 1), gc);
    }

    /**
     * Save currentVocs for next Instance.
     */
    public void onSaveInstanceState(Bundle sis) {
        //Pass indizes of current Vocs in Voc5Client's list.
        int[] currentArr = new int[currentVocs.size()];
        ArrayList<Vocab> clientVocs = client.getVocabs();
        for (int i = 0; i != currentArr.length; i++)
            currentArr[i] = clientVocs.indexOf(currentVocs.get(i));
                    
        sis.putIntArray("currentVocs", currentArr);

        //Pass indizes of selected Cards (in currentVocabs).
        int[] selectedArr = new int[selCount];
        for (int i = 0; i != selCount; i++)
            selectedArr[i] = selected.get(i).parentPos;
        sis.putIntArray("selectedPos", selectedArr);

        super.onSaveInstanceState(sis);
    }
}
