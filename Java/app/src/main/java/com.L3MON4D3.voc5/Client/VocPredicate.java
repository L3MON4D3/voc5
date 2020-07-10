package com.L3MON4D3.voc5.Client;

import java.util.function.Predicate;
import java.util.function.Function;

/**
 * Checks if a Vocabulary fulfills a condition.
 *
 * @author Simon Katz.
 */
public class VocPredicate implements Predicate<Vocab> {
    private Function func;
    private String searchString;

    /**
     * Create new VocPredicate.
     * @param func Function of Vocab that will be called in test. Should be a 'getter', eg Vocab::getAnswer.
     * The getter must return a String.
     */
    public VocPredicate(Function<Vocab, String> func) {
        this.func = func;
        this.searchString = "";
    }

    /**
     * Set the String that Vocabs should be filtered by.
     * @param searchString String, case doesnt matter.
     */
    public void setSearchString(String searchString) { this.searchString = searchString.toLowerCase(); }

    /**
     * Returns true if the getter func returns a String that contains the searchString (Case insensitive).
     * @return true if it matches, false otherwise.
     */
    public boolean test(Vocab voc) {
        return ((String) func.apply(voc)).toLowerCase().contains(searchString);
    }
}
