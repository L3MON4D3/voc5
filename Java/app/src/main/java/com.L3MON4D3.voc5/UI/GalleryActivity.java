package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.content.Intent;

import android.util.DisplayMetrics;
import android.text.TextWatcher;
import android.text.Editable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.Collections;
import java.util.stream.Collectors;

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
    private static final VocabComparator[] sortComps = new VocabComparator[] {
        new AnswerComparator(),
        new QuestionComparator(),
        new LanguageComparator(),
        new PhaseComparator()
    };
    private static final VocPredicate[] srchPreds = new VocPredicate[] {
        new VocPredicate(Vocab::getAnswer),
        new VocPredicate(Vocab::getQuestion),
        new VocPredicate(Vocab::getLanguage),
        new VocPredicate(Vocab::getPhaseString)
    };
    private static final int EDIT_RESULT = 21;

    private GridLayout gallery;
    private GalleryCardFactory gcf;

    //Contains references to Vocab in Voc5Client.
    private ArrayList<GalleryCard> allCards;
    private ArrayList<GalleryCard> currentCards;
    private ArrayList<GalleryCard> selected;

    private Button galleryEditBtn;
    private Button galleryLearnBtn;
    private Button galleryDeleteBtn;
    private FloatingActionButton fab;
    private EditText searchET;

    private int srchSel;
    private int sortSel;
    private boolean sortAsc;

    VocabComparator crtSortComp;
    VocPredicate crtSrchPred;

    private int selCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        srchSel = 0;
        sortSel = 0;
        sortAsc = true;

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
        currentCards = new ArrayList<GalleryCard>();
        allCards = new ArrayList<GalleryCard>();
        if (savedInstanceState == null) {
            if (!client.hasVocabs()) {
                client.loadVocs(() -> {
                    runOnUiThread(() -> {
                        fillAllAndCurrent(client.getVocabs());
                        setGalleryContentFromCurrent();
                    });
                });
            } else {
                fillAllAndCurrent(client.getVocabs());
                setGalleryContentFromCurrent();
            }
        } else {
            fillAll(client.getVocabs());
            setGalleryContent(
                savedInstanceState.getIntArray("currentVocs"),
                savedInstanceState.getIntArray("selectedPos"));
        }

        galleryEditBtn = findViewById(R.id.galleryEditBtn);
        galleryLearnBtn = findViewById(R.id.galleryLearnBtn);
        galleryDeleteBtn = findViewById(R.id.galleryDeleteBtn);
        fab = findViewById(R.id.galleryFab);
        searchET = findViewById(R.id.search_et);

        searchET.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crtSrchPred.setSearchString(String.valueOf(s));
                //if Text was changed somewhere not at the end, search allCards
                if (start != s.length()-1)
                    searchGallery(allCards);
                //if a char was added, search current Cards (search got more specific)
                else if (before < count)
                    searchGallery(currentCards);
                else
                    searchGallery(allCards);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable e) { }
        });

        Button srchSelBtn = findViewById(R.id.srchSel_btn);
        srchSet(srchSelBtn);
        srchSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                srchSel = (srchSel + 1) % 4;
                srchSet(tv);
                searchGallery(allCards);
            }
        });

        Button sortBtn = findViewById(R.id.sortSel_btn);
        sortSet(sortBtn);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                sortSel = (sortSel + 1) % 4;
                sortSet(tv);
                sortGallery();
            }
        });

        Button sortDirBtn = findViewById(R.id.sortDir_btn);
        sortDirSet(sortDirBtn);
        sortDirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                sortAsc = !sortAsc;
                sortDirSet(tv);
                sortReverse();
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
                    currentCards.remove(tmp);
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

    /**
     * Create GalleryCard for each Vocab in vocs.
     * @param vocs
     */
    public void fillAll(ArrayList<Vocab> vocs) {
        for (Vocab voc : vocs) {
            //Pass parentPos of 0, will be set correctly later on.
            allCards.add(gcf.newCard(voc, 0));
        }
    }

    /**
     * Create GalleryCard for each Vocab in vocs.
     * @param vocs
     */
    public void fillAllAndCurrent(ArrayList<Vocab> vocs) {
        for (Vocab voc : vocs) {
            //Pass parentPos of 0, will be set correctly later on.
            GalleryCard gc = gcf.newCard(voc, 0);
            allCards.add(gc);
            currentCards.add(gc);
        }
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
     * Sets text and srchComp according to variable srchSel.
     * @param srchBtn Button that displays current selection.
     */
    public void srchSet(Button srchBtn) {
        switch(srchSel) {
            case(0) :
                srchBtn.setText("Ans");
                break;
            case(1) :
                srchBtn.setText("Qes");
                break;
            case(2) :
                srchBtn.setText("Lan");
                break;
            case(3) :
                srchBtn.setText("Phs");
                break;
        }
        crtSrchPred = srchPreds[srchSel];
        crtSrchPred.setSearchString(searchET.getText().toString());
    }

    /**
     * Sets text and sortComp according to variable sortSel.
     * @param sortBtn Button that displays current selection.
     */
    public void sortSet(Button sortBtn) {
        switch(sortSel) {
            case(0) :
                sortBtn.setText("Ans");
                break;
            case(1) :
                sortBtn.setText("Qes");
                break;
            case(2) :
                sortBtn.setText("Lan");
                break;
            case(3) :
                sortBtn.setText("Phs");
                break;
        }
        crtSortComp = sortComps[sortSel];
    }

    /**
     * Sets text and boolean ascending for VocabComparator.
     * @param sortDirBtn Button that displays current sort Direction(Asc/Dsc)
     */
    public void sortDirSet(Button sortDirBtn) {
        if (sortAsc)
            sortDirBtn.setText("Asc");
        else
           sortDirBtn.setText("Dsc");
        crtSortComp.setAscending(sortAsc);
    }

    /**
     * Sorts Gallery using crtSortComp.
     */
    public void sortGallery() {
        currentCards.sort(Comparator.comparing(GalleryCard::getVoc, crtSortComp));
        setGalleryContentFromCurrent();
    }

    /**
     * Sort Ascending insteaaad of Descending <=> reverse currentCards.
     */
    public void sortReverse() {
        Collections.reverse(currentCards);
        setGalleryContentFromCurrent();
    }

    /**
     * Filter Cards based on crtSrchPred.
     * @param cards search this ArrayList, useful for more/less specific searches.
     */
    public void searchGallery(ArrayList<GalleryCard> cards) {
        currentCards = (ArrayList<GalleryCard>) cards.stream()
            .filter(gc -> crtSrchPred.test(gc.getVoc()))
            .collect(Collectors.toList());
        setGalleryContentFromCurrent();
    }

    /**
     * Sets Gallery Content from currentCards.
     */
    public void setGalleryContentFromCurrent() {
        clearGallery();
        for (int i = 0; i != currentCards.size(); i++) {
            GalleryCard gc = currentCards.get(i);
            gallery.addView(gc, i);
            gc.parentPos = i;
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
        for (int i = 0; i != currentVocInd.length; i++) {
            GalleryCard gc = allCards.get(currentVocInd[i]);
            currentCards.add(gc);

            if (selectedArrInd < selectedPos.length && i == selectedPos[selectedArrInd]) {
                select(gc);
                selectedArrInd++;
            }
            gc.parentPos = i;
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
        int[] currentArr = new int[allCards.size()];
        ArrayList<Vocab> clientVocs = client.getVocabs();
        for (int i = 0; i != currentArr.length; i++)
            currentArr[i] = clientVocs.indexOf(allCards.get(i).getVoc());
        sis.putIntArray("currentVocs", currentArr);
                    
        //Pass indizes of selected Cards (in currentVocabs).
        int[] selectedArr = new int[selCount];
        selected.sort(null);
        for (int i = 0; i != selCount; i++)
            selectedArr[i] = selected.get(i).parentPos;
        sis.putIntArray("selectedPos", selectedArr);

        super.onSaveInstanceState(sis);
    }
}
