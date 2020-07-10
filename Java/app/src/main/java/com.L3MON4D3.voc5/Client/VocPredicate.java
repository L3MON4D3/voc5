package com.L3MON4D3.voc5.Client;

import java.util.function.Predicate;
import java.util.function.Function;

/**
 * Checks if a Vocabulary fulfills a conditions
 *
 * @author Simon Katz
 */
public class VocPredicate implements Predicate<Vocab> {
    private Function func;
    private String searchString;

    public VocPredicate(Function<Vocab, String> func) {
        this.func = func;
        this.searchString = "";
    }

    public void setSearchString(String searchString) { this.searchString = searchString.toLowerCase(); }

    public boolean test(Vocab voc) {
        return ((String) func.apply(voc)).toLowerCase().contains(searchString);
    }
}
