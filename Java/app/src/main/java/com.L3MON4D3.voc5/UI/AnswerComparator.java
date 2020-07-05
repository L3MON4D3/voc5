package com.L3MON4D3.voc5.UI;

import com.L3MON4D3.voc5.Client.Vocab;

import java.util.Comparator;

public class AnswerComparator implements Comparator<Vocab> {
    private static boolean ascending;

    public AnswerComparator(boolean asc) { this.ascending = asc; }

    @Override
    public int compare(Vocab a, Vocab b) {
        return a.getAnswer().compareTo(b.getAnswer());
    }
}
