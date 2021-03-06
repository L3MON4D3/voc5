package com.L3MON4D3.voc5.Client;

/**
 * Compares two Vocabulary according to their language
 *
 * @author Jan Rogge and Simon Katz
 */
public class LanguageComparator extends VocabComparator {
    /**
     *Compare Language.
     */
    @Override
    public int compare(Vocab a, Vocab b) {
        return (ascending?1:-1)*a.getLanguage().toLowerCase().compareTo(b.getLanguage().toLowerCase());
    }
}
