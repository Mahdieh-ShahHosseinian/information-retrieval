package com.example.informationretrieval.practice_1.inverted_index;

import java.util.ArrayList;
import java.util.List;

public class TermInfo {
    private int frequency;
    private List<Integer> postingsList;

    public TermInfo(Integer documentNo) {
        this.frequency = 1;
        this.postingsList = new ArrayList<>();
        this.postingsList.add(documentNo);
    }

    public void addFrequency() {
        this.frequency++;
    }

    public void addPostingsList(Integer documentNo) {
        this.postingsList.add(documentNo);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public List<Integer> getPostingsList() {
        return postingsList;
    }

    public void setPostingsList(List<Integer> postingsList) {
        this.postingsList = postingsList;
    }
}
