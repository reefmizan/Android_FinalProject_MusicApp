package com.example.musicapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ITunesResponse {
    @SerializedName("results")
    private List<Song> results;

    public List<Song> getResults() {
        return results;
    }
}