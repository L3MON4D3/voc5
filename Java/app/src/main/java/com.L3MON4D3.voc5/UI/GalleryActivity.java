package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.content.Intent;

import android.util.DisplayMetrics;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.io.IOException;
import java.util.ArrayList;

import com.L3MON4D3.voc5.Client.*;
import com.L3MON4D3.voc5.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;

public class GalleryActivity extends VocActivity {
    private static final int EDIT_RESULT = 21;

    private GridLayout gallery;
    private GalleryCardFactory gcf;

    //Contains references to Vocab in Voc5Client.
    private ArrayList<Vocab> currentVocs;
    private ArrayList<GalleryCard> selected;

    private Button galleryEditBtn;
    private Button galleryLearnBtn;
    private Button galleryDeleteBtn;
    private int srchSel;
    private int sortSel;
    private boolean sortAsc;
    private FloatingActionButton fab;
    private int selCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        gallery = findViewById(R.id.gall_lt);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        
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
        fab = findViewById(R.id.galleryFab);

        srchSel = 0;
        findViewById(R.id.srchSel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                switch(srchSel) {
                    case(0) :
                        srchSel = 1;
                        tv.setText("Qes");
                        break;
                    case(1) :
                        srchSel = 2;
                        tv.setText("Lan");
                        break;
                    case(2) :
                        srchSel = 0;
                        tv.setText("Ans");
                        break;
                }
            }
        });

        sortSel = 0;
        findViewById(R.id.sortSel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                switch(sortSel) {
                    case(0) :
                        sortSel = 1;
                        tv.setText("Qes");
                        break;
                    case(1) :
                        sortSel = 2;
                        tv.setText("Lan");
                        break;
                    case(2) :
                        sortSel = 0;
                        tv.setText("Ans");
                        break;
                }
            }
        });

        sortAsc = true;
        findViewById(R.id.sortDir_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                if (sortAsc)
                    tv.setText("Dsc");
                else
                   tv.setText("Asc");
                sortAsc = !sortAsc;
            }
        });

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vocab newVoc = new Vocab();
                client.getOkClient().newCall(client.newVocabRqst(newVoc)).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("voc5:", e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.e("voc5:", response.body().string());
                    }
                });

                startEditVoc(newVoc);
            }
        });

        galleryDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected.sort(null);

                int size = selected.size();
                for(int i = 0; i != size; i++){
                    GalleryCard tmp = selected.get(0);
                    Vocab tmpVoc = tmp.getVoc();

                    client.getVocabs().remove(tmpVoc);
                    currentVocs.remove(tmpVoc);
                    deselect(tmp);
                    //Gallery cards replace deleted galley cards therefore shift index from parents.
                    gallery.removeViewAt(tmp.parentPos-i);

                    client.getOkClient().newCall(client.deleteVocRqst(tmpVoc)).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("voc5:", e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.e("voc5:", response.body().string());
                        }
                    });
                }

                for(int i = 0; i != gallery.getChildCount();i++) {
                    ((GalleryCard)gallery.getChildAt(i)).parentPos = i;
                }
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

    public void clearGallery(){
        gallery.removeAllViews();
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
     * Starts editVoc activity and passes Vocabulary meant to be edited.
     */
    public void startEditVoc(Vocab voc){
        Intent startIntent = new Intent(getApplicationContext(), EditActivity.class);
        startIntent.putExtra("com.L3MON4D3.voc5.Voc", voc);
        startActivityForResult(startIntent, EDIT_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_RESULT)
            if (resultCode == RESULT_OK) {
                Vocab newVoc = data.getExtras().getParcelable("com.L3MON4D3.voc5.newVoc");

                GalleryCard oldVocCard = selected.get(selCount - 1);
                Vocab oldVoc = oldVocCard.getVoc();
                oldVoc.setAnswer(newVoc.getAnswer());
                oldVoc.setQuestion(newVoc.getQuestion());
                oldVoc.setLanguage(newVoc.getLanguage());
                oldVoc.setPhase(newVoc.getPhase());

                oldVocCard.refresh();

                client.getOkClient().newCall(client.updateVocRqst(oldVoc)).enqueue(new Callback() {
                    public void onFailure(Call call, IOException e) {
                    }

                    public void onResponse(Call call, Response res) throws IOException {
                    }
                });
            }
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
