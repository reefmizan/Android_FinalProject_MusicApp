package com.example.musicapp.network;

import com.example.musicapp.models.ITunesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITunesApiService {

    // Defines the GET request to the 'search' endpoint
    @GET("search")
    Call<ITunesResponse> searchSongs(
            @Query("term") String searchTerm,
            @Query("media") String mediaType
    );
}