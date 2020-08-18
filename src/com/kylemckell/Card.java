package com.kylemckell;

// card class that holds the term, definition, and the amount of mistakes a user has made on a flashcard
class Card {
    private final String term;
    private String definition;
    private int mistakes;

    public Card(String term, String definition, int mistakes) {
        this.term = term;
        this.definition = definition;
        this.mistakes = mistakes;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    // increments mistake if the user made a mistake, just easier than making a set call with an incrementation
    public void addMistake() {
        mistakes++;
    }
}
