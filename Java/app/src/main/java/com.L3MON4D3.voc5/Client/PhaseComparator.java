package com.L3MON4D3.voc5.Client;

import java.util.Comparator;

public class PhaseComparator implements Comparator<Vocab> {
    private static boolean ascending = true;

    public void setAscending(boolean asc) { this.ascending = asc; }

    /**
     * compares either ascending or descending
     *
     */
    @Override
    public int compare(Vocab a, Vocab b) {
        return ascending ? 1:-1* a.getPhase() - b.getPhase();
    }
}
