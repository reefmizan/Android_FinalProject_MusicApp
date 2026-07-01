package com.example.musicapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.models.ITunesResponse;
import com.example.musicapp.models.Song;
import com.example.musicapp.network.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicViewModel extends ViewModel {

    // LiveData holding the search results
    private final MutableLiveData<List<Song>> searchResults = new MutableLiveData<>();

    // LiveData holding the favorite songs list
    private final MutableLiveData<List<Song>> favoriteSongs = new MutableLiveData<>();

    // LiveData holding network/database error messages
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // --- Getters for UI Components to Observe ---

    public LiveData<List<Song>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<Song>> getFavoriteSongs() {
        return favoriteSongs;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // --- Business Logic Operations ---

    /**
     * Fetches songs from iTunes API and updates searchResults LiveData.
     */
    public void searchSongs(String query) {
        ApiClient.getInstance().getApi().searchSongs(query, "music")
                .enqueue(new Callback<ITunesResponse>() {
                    @Override
                    public void onResponse(Call<ITunesResponse> call, Response<ITunesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            searchResults.setValue(response.body().getResults());
                        } else {
                            errorMessage.setValue("Server error received from iTunes");
                        }
                    }

                    @Override
                    public void onFailure(Call<ITunesResponse> call, Throwable t) {
                        errorMessage.setValue("Network Connection Failed: " + t.getMessage());
                    }
                });
    }

    /**
     * Fetches the user's saved favorite songs from Firestore.
     */
    public void loadFavoriteSongs() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Song> localList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Song song = document.toObject(Song.class);
                        if (song != null) {
                            localList.add(song);
                        }
                    }
                    favoriteSongs.setValue(localList);
                })
                .addOnFailureListener(e -> errorMessage.setValue("Failed to load favorites: " + e.getMessage()));
    }

    /**
     * Saves a song to the user's Firestore favorites collection.
     */
    public void addSongToFavorites(Song song) {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        String docId = (song.getTrackName() + "_" + song.getArtistName()).replaceAll("[^a-zA-Z0-9]", "");

        db.collection("users").document(uid).collection("favorites").document(docId)
                .set(song)
                .addOnSuccessListener(aVoid -> {
                    // Refresh the local favorites list after adding
                    loadFavoriteSongs();
                })
                .addOnFailureListener(e -> errorMessage.setValue("Failed to save favorite: " + e.getMessage()));
    }

    /**
     * Deletes a song from the user's Firestore favorites collection.
     */
    public void removeSongFromFavorites(Song song) {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        String docId = (song.getTrackName() + "_" + song.getArtistName()).replaceAll("[^a-zA-Z0-9]", "");

        db.collection("users").document(uid).collection("favorites").document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Refresh the local favorites list after removing
                    loadFavoriteSongs();
                })
                .addOnFailureListener(e -> errorMessage.setValue("Failed to remove favorite: " + e.getMessage()));
    }
}