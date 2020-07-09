package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.os.Parcelable;
import android.content.Intent;

import android.util.DisplayMetrics;
import android.text.TextWatcher;
import android.text.Editable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.function.Consumer;

import com.L3MON4D3.voc5.Client.*;
import com.L3MON4D3.voc5.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import okhttp3.Call;
import okhttp3.Callback;
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
    private static final int NEW_RESULT = 22;
    private static final int LEARN_RESULT = 45;

    private Thread updateThread = new Thread(() -> {
        while(true) {
            try {
                //sleep for 2 minutes
                Thread.sleep(120000);
            }catch (Exception e) {
                break;
            }

            //search for new vocabulary to add to Gallery
            ArrayList oldVocs = new ArrayList(client.getVocabs());
            client.loadVocs(() -> {
                for (Vocab voc : client.getVocabs()) {
                    if(oldVocs.contains(voc))
                        oldVocs.remove(voc);
                    else
                        oldVocs.add(voc);
                }
                //add new vocabulary to gallery
                runOnUiThread(() -> {
                    addVocsToGallery(oldVocs);
                });
            });
        }
    });

    private GridLayout gallery;
    private GalleryCardFactory gcf;

    //Contains references to Vocab in Voc5Client.
    private ArrayList<GalleryCard> allCards;
    private ArrayList<GalleryCard> currentCards;
    private ArrayList<GalleryCard> selected;
    private ArrayList<GalleryCard> deSelected;
    private boolean lastActionSelect;

    private FloatingActionButton galleryEditBtn;
    private FloatingActionButton galleryLearnBtn;
    private FloatingActionButton galleryDeleteBtn;
    private FloatingActionButton galleryAddBtn;
    private EditText searchET;

    private Button srchSelBtn;
    private Button sortSelBtn;
    private Button sortDirBtn;

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

        setLoadingInfoParentLayout((ConstraintLayout) findViewById(R.id.galleryOuter_lt));

        galleryEditBtn = findViewById(R.id.galleryEditBtn);
        galleryLearnBtn = findViewById(R.id.galleryLearnBtn);
        galleryDeleteBtn = findViewById(R.id.galleryDeleteBtn);
        galleryAddBtn = findViewById(R.id.galleryAddBtn);
        searchET = findViewById(R.id.search_et);

        srchSelBtn = findViewById(R.id.srchSel_btn);
        sortSelBtn = findViewById(R.id.sortSel_btn);
        sortDirBtn = findViewById(R.id.sortDir_btn);

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
        deSelected = new ArrayList<GalleryCard>();
        currentCards = new ArrayList<GalleryCard>();
        allCards = new ArrayList<GalleryCard>();
        if (savedInstanceState == null) {
            if (!client.hasVocabs()) {
                setLoadingInfoText("Downloading Vocab");
                startLoading();
                client.loadVocs(() -> {
                    runOnUiThread(() -> {
                        fillAllAndCurrent(client.getVocabs());
                        setGalleryContentFromCurrent();

                        sortSearchSet();
                        searchSortGallery(allCards);
                        stopLoading();
                    });
                });
            } else {
                fillAllAndCurrent(client.getVocabs());
                setGalleryContentFromCurrent();

                sortSearchSet();
            }
        } else {
            fillAll(client.getVocabs());
            setGalleryContent(
                savedInstanceState.getIntArray("currentVocs"),
                savedInstanceState.getIntArray("selectedPos"));

            srchSel = savedInstanceState.getInt("srchSel");
            sortSel = savedInstanceState.getInt("sortSel");
            sortAsc = savedInstanceState.getBoolean("sortAsc");

            sortSearchSet();
            searchSortGallery(allCards);
        }

        searchET.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crtSrchPred.setSearchString(String.valueOf(s));
                //if Text was changed somewhere not at the end, search allCards
                if (start != s.length()-1)
                    searchSortGallery(allCards);
                //if a char was added, search current Cards (search got more specific)
                else if (before < count)
                    searchSortGallery(currentCards);
                else
                    searchSortGallery(allCards);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable e) { }
        });

        srchSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button tv = (Button) view;
                srchSel = (srchSel + 1) % 4;
                srchSet();
                //if there is no input, do nothing.
                if (!searchET.getText().toString().equals(""))
                    searchSortGallery(allCards);
            }
        });

        sortSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortSel = (sortSel + 1) % 4;
                sortSet();
                sortGallery();
            }
        });

        sortDirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortAsc = !sortAsc;
                sortDirSet();
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

        galleryAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), EditActivity.class);
                startActivityForResult(startIntent,NEW_RESULT);
            }
        });

        galleryDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBtn();
            }
        });
        updateThread.start();
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
     * Sets srchComp and sortComp according to srchSel and sortSel.
     */
    public void sortSearchSet() {
        srchSet();
        sortSet();
        sortDirSet();
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
        deSelected.remove(gc);
        gc.elevate();
        gc.setSelected(true);
        selCount++;
        lastActionSelect = true;

        activateButtons();
    }

    /**
     * Makes edit,learn and delete button appear
     * or disappear depending on amount of selected vocabulary.
     */
    public void activateButtons(){
        if(selCount == 1) {
            galleryEditBtn.setVisibility(View.VISIBLE);
            galleryLearnBtn.setVisibility(View.VISIBLE);
            galleryDeleteBtn.setVisibility(View.VISIBLE);
        }//only one vocab can be edited at a time
        else if(selCount > 1) {
            galleryEditBtn.setVisibility(View.GONE);
        } else {
            galleryEditBtn.setVisibility(View.GONE);
            galleryLearnBtn.setVisibility(View.GONE);
            galleryDeleteBtn.setVisibility(View.GONE);
        }
    }

    /**
     * Remove Card from selected, decrease counter.
     * @param gc Card to select.
     */
    public void deselect(GalleryCard gc) {
        if (!gc.getSelected())
            return;
        selected.remove(gc);
        deSelected.add(gc);
        gc.resetElevation();
        gc.setSelected(false);
        selCount--;
        lastActionSelect = false;

        activateButtons();
    }

    /**
     * Sets text and srchComp according to variable srchSel.
     */
    public void srchSet() {
        switch(srchSel) {
            case(0) :
                srchSelBtn.setText(R.string.voc_answerShort);
                break;
            case(1) :
                srchSelBtn.setText(R.string.voc_questionShort);
                break;
            case(2) :
                srchSelBtn.setText(R.string.voc_languageShort);
                break;
            case(3) :
                srchSelBtn.setText(R.string.voc_phaseShort);
                break;
        }
        crtSrchPred = srchPreds[srchSel];
        crtSrchPred.setSearchString(searchET.getText().toString());
    }

    /**
     * Sets text and sortComp according to variable sortSel.
     * @param sortBtn Button that displays current selection.
     */
    public void sortSet() {
        switch(sortSel) {
            case(0) :
                sortSelBtn.setText(R.string.voc_answerShort);
                break;
            case(1) :
                sortSelBtn.setText(R.string.voc_questionShort);
                break;
            case(2) :
                sortSelBtn.setText(R.string.voc_languageShort);
                break;
            case(3) :
                sortSelBtn.setText(R.string.voc_phaseShort);
                break;
        }
        crtSortComp = sortComps[sortSel];
    }

    /**
     * Sets text and boolean ascending for VocabComparator.
     * @param sortDirBtn Button that displays current sort Direction(Asc/Dsc)
     */
    public void sortDirSet() {
        if (sortAsc)
            ((MaterialButton) sortDirBtn).setIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24));
        else
            ((MaterialButton) sortDirBtn).setIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_upward_24));
        crtSortComp.setAscending(sortAsc);
    }

    /**
     * Sorts Gallery using crtSortComp.
     */
    public void sortGallery() {
        currentCards.sort(Comparator.comparing(GalleryCard::getVoc, crtSortComp));
        if (!sortAsc)
            Collections.reverse(currentCards);
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
    public void searchSortGallery(ArrayList<GalleryCard> cards) {
        currentCards = (ArrayList<GalleryCard>) cards.stream()
            .filter(gc -> crtSrchPred.test(gc.getVoc()))
            .collect(Collectors.toList());
        sortGallery();
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
     * Repeats Last select-Action on Cards between first and second.
     * @param first GalleryCard, may be before or after second.
     * @param second GalleryCard, may be before or after first.
     */
    public void repSelActionOnRange(GalleryCard first, GalleryCard second) {
        int pos1 = first.parentPos;
        int pos2 = second.parentPos;
        boolean goUp = pos1 < pos2;
        repLastSelAction(first);
        while (pos1 != pos2) {
            repLastSelAction((GalleryCard) gallery.getChildAt(pos1));
            if (goUp)
                pos1++;
            else
               pos1--;
        }
        repLastSelAction(second);
    }

    /**
     * Repeats Last select-Action, eg deselect if !lastActionSelect, else select.
     * @param gc GalleryCard that will be selected.
     */
    public void repLastSelAction(GalleryCard gc) {
        if (lastActionSelect)
            select(gc);
        else
           deselect(gc);
    }

    /**
     * Repeats last Select Action from last selected Card to Card gc (inclusive).
     * if there is no last selected, simply selects gc.
     * @param gc GalleryCard.
     */
    public void selectActionRepFromLast(GalleryCard gc) {
        if (selCount == 0)
            select(gc);
        else
            if (lastActionSelect)
                repSelActionOnRange(selected.get(selCount - 1), gc);
            else
                repSelActionOnRange(deSelected.get(deSelected.size() - 1), gc);
    }

    /**
     * Deletes all vocabulary that is selected and updates gallery
     */
    public void deleteBtn() {
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

    /**
     * Starts editVoc activity and passes Vocabulary meant to be edited.
     */
    public void startEditVoc(Vocab voc){
        Intent startIntent = new Intent(getApplicationContext(), EditActivity.class);
        startIntent.putExtra("com.L3MON4D3.voc5.Voc", voc);
        startActivityForResult(startIntent, EDIT_RESULT);
    }

    /**
     * Start LernActivity with selected Vocs.
     */
    public void startLearn(){
        Intent startIntent = new Intent(getApplicationContext(), LernActivity.class);
        ArrayList<Vocab> learnVocs = (ArrayList) selected.stream().map(GalleryCard::getVoc).collect(Collectors.toList());

        startIntent.putParcelableArrayListExtra("ArrayListLern", learnVocs);
        startActivityForResult(startIntent, LEARN_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_RESULT) {
            if (resultCode == RESULT_OK) {
                Vocab newVoc = data.getExtras().getParcelable("com.L3MON4D3.voc5.newVoc");

                //set vocab attributes to new attributes
                GalleryCard oldVocCard = selected.get(selCount - 1);
                Vocab oldVoc = oldVocCard.getVoc();
                oldVoc.setAnswer(newVoc.getAnswer());
                oldVoc.setQuestion(newVoc.getQuestion());
                oldVoc.setLanguage(newVoc.getLanguage());
                oldVoc.setPhase(newVoc.getPhase());

                oldVocCard.refresh();

                //update vocab on server
                client.getOkClient().newCall(client.updateVocRqst(oldVoc)).enqueue(new Callback() {
                    public void onFailure(Call call, IOException e) {
                    }

                    public void onResponse(Call call, Response res) throws IOException {
                    }
                });
            }
        }//check if new vocab was added
        else if (requestCode == NEW_RESULT) {
            if(resultCode == RESULT_OK){
                setLoadingInfoText("Adding Vocab");
                startLoading();

                //load voc from edit Intent
                Vocab voc = data.getExtras().getParcelable("com.L3MON4D3.voc5.newVoc");
                String newQuestion = voc.getQuestion();
                String newAnswer = voc.getAnswer();
                String newLang = voc.getLanguage();
                int newphase = voc.getPhase();
                //upload voc to server
                client.getOkClient().newCall(client.newVocabRqst(voc)).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //once uploaded, download vocabs and find new vocab
                        client.loadVocs(() -> {
                            runOnUiThread(() -> {
                                ArrayList<Vocab> vocs = client.getVocabs();
                                int i = vocs.size() - 1;
                                Vocab v = null;
                                for (; i >= 0; i--) {
                                    v = vocs.get(i);
                                    if(v.getQuestion().equals(newQuestion)
                                            && v.getAnswer().equals(newAnswer)
                                            && v.getLanguage().equals(newLang)) {
                                        break;
                                    }
                                }
                                //set the phase as server sets it to 0
                                v.setPhase(newphase);
                                client.getOkClient().newCall(client.updateVocRqst(v)).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    }
                                });
                                //create gallery card for new vocab
                                GalleryCard newCard = gcf.newCard(v, 0);
                                allCards.add(i, newCard);
                                searchSortGallery(allCards);
                                stopLoading();
                            });
                        });
                    }
                });

            }
        } else if(requestCode == LEARN_RESULT) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Parcelable[] newPhases = extras.getParcelableArray("newPhases");

                //sort newPhases by Vocab id, makes finding Vocabs in client-List faster.
                Arrays.sort(newPhases, (a, b) -> {return ((IntPair) a).first - ((IntPair) b).first;});

                //Assign new Phases to Vocabs in Voc5Client.
                int nPInd = 0;
                IntPair next = (IntPair) newPhases[0];
                for (GalleryCard gc : allCards) {
                    Vocab v = gc.getVoc();
                    if (v.getId() == next.first) {
                        v.setPhase(next.second);
                        gc.refresh();
                        if (++nPInd == newPhases.length)
                            break;
                        next = (IntPair) newPhases[nPInd];
                    }
                }
            }
        }
    }

    /**
     * Create new GalleryCard for each Vocab in vocs and add it to the Gallery.
     * @param vocs ArrayList of Vocabs, preferably some that have not yet been added to the Gallery.
     */
    public void addVocsToGallery(ArrayList<Vocab> vocs){
        if (vocs.size() != 0) {
            ArrayList<Vocab> cVocs = client.getVocabs();
            cVocs.sort(null);
            for (Vocab v : vocs) {
                int ind = cVocs.indexOf(v);
                allCards.add(ind, gcf.newCard(v, 0));
            }
            searchSortGallery(allCards);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        updateThread.interrupt();
        super.onDestroy();
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

        sis.putInt("srchSel", srchSel);
        sis.putInt("sortSel", sortSel);
        sis.putBoolean("sortAsc", sortAsc);

        super.onSaveInstanceState(sis);
    }
}
