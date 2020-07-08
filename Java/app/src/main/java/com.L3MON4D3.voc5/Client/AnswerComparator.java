package com.L3MON4D3.voc5.Client;

public class AnswerComparator extends VocabComparator {
    /**
     * compares either ascending or descending
     */
    @Override
    public int compare(Vocab a, Vocab b) {
        return (ascending?1:-1)*a.getAnswer().toLowerCase().compareTo(b.getAnswer().toLowerCase());
    }
}
