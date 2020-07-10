package com.L3MON4D3.voc5.Client;

/**
 * Compares two vocabularies according to their question
 *
 * @author Jan Rogge and Simon Katz
 */
public class QuestionComparator extends VocabComparator {
    /**
     * compares either ascending or descending
     *
     */
    @Override
    public int compare(Vocab a, Vocab b) {
        return (ascending?1:-1)*a.getQuestion().toLowerCase().compareTo(b.getQuestion().toLowerCase());
    }
}
