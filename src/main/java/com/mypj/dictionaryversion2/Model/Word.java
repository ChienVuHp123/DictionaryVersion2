package com.mypj.dictionaryversion2.Model;

import java.security.PublicKey;

public class Word {
    private String term;
    private String definition;

    public Word() {}

    public Word(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String GetTerm() {
        return term;
    }

    public String GetDefinition() {
        return definition;
    }

    public void SetTerm(String term) {
        this.term = term;
    }

    public void SetDefinition(String definition) {
        this.definition = definition;
    }
}

