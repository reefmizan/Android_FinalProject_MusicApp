package com.example.musicapp.models;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("trackName")
    private String trackName;

    @SerializedName("artistName")
    private String artistName;

    // iTunes API returns the image URL under "artworkUrl100"
    @SerializedName("artworkUrl100")
    private String artworkUrl;

    public Song(String trackName, String artistName, String artworkUrl) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.artworkUrl = artworkUrl;
    }

    public String getTrackName() {
        return trackName != null ? trackName : "Unknown Track";
    }

    public String getArtistName() {
        return artistName != null ? artistName : "Unknown Artist";
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }
}