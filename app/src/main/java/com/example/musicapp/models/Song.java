package com.example.musicapp.models;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("trackName")
    private String trackName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("artworkUrl100")
    private String artworkUrl;

    // The 30-second audio preview link from iTunes
    @SerializedName("previewUrl")
    private String previewUrl;

    // IMPORTANT: Empty constructor is required for Firebase Firestore serialization!
    public Song() {}

    public Song(String trackName, String artistName, String artworkUrl, String previewUrl) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.artworkUrl = artworkUrl;
        this.previewUrl = previewUrl;
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

    public String getPreviewUrl() {
        return previewUrl;
    }
}