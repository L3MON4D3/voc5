package com.L3MON4D3.voc5.Client;

import java.util.Comparator;

/**
 * Allows comparison of vocabularies.
 * Subclasses implement compare() according to an attribute.
 *
 * @author Jan Rogge und Simon Katz
 */
public abstract class VocabComparator implements Comparator<Vocab> {
    protected static boolean ascending = true;

    public void setAscending(boolean asc) { this.ascending = asc; }
}
