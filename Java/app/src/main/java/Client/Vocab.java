package com.L3MON4D3.voc5;

public class Vocab {
    private int id;
    private String answer;
    private String question;
    private String language;
    private int phase;

    /**
     * Empty Constructor for Vocab. To actually Create a new Vocabulary use
     * newVocabRqst in Voc5Client, as id's are only assigned by the Server.
     */
    public Vocab() { }


    public int getId() { return id; }
}
