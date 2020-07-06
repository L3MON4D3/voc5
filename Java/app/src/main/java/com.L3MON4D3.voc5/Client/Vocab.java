package com.L3MON4D3.voc5.Client;

import android.os.Parcelable;
import android.os.Parcel;


public class Vocab extends Object implements Parcelable {
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
        this.phase = 0;
    }

    public int getId() { return id; }
    public String getAnswer() { return answer; }
    public String getQuestion() { return question; }
    public String getLanguage() { return language; }
    public int getPhase() { return phase; }

    public void setQuestion(String newQuestion) { this.question = newQuestion; }
    public void setAnswer(String newAnswer) { this.answer = newAnswer; }
    public void setLanguage(String newLanguage) { this.language = newLanguage; }
    public void setPhase(int newPhase){ this.phase = newPhase; }
    public void setId(int newId){ this.id = newId; }

    /**
     * Get Object as String.
     * @return String as follows: "id:__answer:__question:__language:__"
     */
    public String toString() {
        return String.format("id: %sanswer: %squestion: %slanguage: %s",
            id, answer, question, language);
    }
    //Begin Parcelable implementation.
        /**
         * Dummy function.
         * @return 0, always.
         */
        public int describeContents() {
            return 0;   
        }

        /**
         * Write user, email and server to Parcel.
         * @param out Parcel.
         * @param Flags Flags, doesnt do anything right now.
         */
        public void writeToParcel(Parcel out, int Flags) {
            out.writeString(question);
            out.writeString(language);
            out.writeString(answer);
            out.writeInt(phase);
            out.writeInt(id);
        }

        public static final Parcelable.Creator<Vocab> CREATOR = 
            new Parcelable.Creator<Vocab>() {

                public Vocab createFromParcel(Parcel in) {
                    return new Vocab(in);  
                }

                public Vocab[] newArray(int size) {
                    return new Vocab[size];
                }
            };

        private Vocab(Parcel in) {
            this.question = in.readString();
            this.language = in.readString();
            this.answer = in.readString();
            this.phase = in.readInt();
            this.id = in.readInt();
        }
    //End Parcelable implementation.


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vocab vocab = (Vocab) o;
        return id == vocab.id;
    }

}
