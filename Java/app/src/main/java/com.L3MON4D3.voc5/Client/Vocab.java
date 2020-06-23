package com.L3MON4D3.voc5.Client;

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

    /**
     * Create new Vocab, ONLY USE FOR TESTING.
     * @param answer 
     * @param question 
     * @param language 
     */
    public Vocab(String answer, String question, String language) {
        this.answer = answer;
        this.question = question;
        this.language = language;
    }

    public int getId() { return id; }
    public String getAnswer() { return answer; }
    public String getQuestion() { return question; }
    public String getLanguage() { return language; }

    public void setPhase(int newPhase){this.phase = newPhase;}//needed to edit Vocab
    public void setId(int newId){this.id = newId;}

    /**
     * Get Object as String.
     * @return String as follows: "id:__\nanswer:__\nquestion:__\nlanguage:__\n"
     */
    public String toString() {
        return String.format("id: %s%nanswer: %s%nquestion: %s%nlanguage: %s%n",
            id, answer, question, language);
    }
}
