package com.example.musicapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapp.databinding.FragmentSearchBinding;
import com.example.musicapp.models.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SongAdapter songAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearchQuery.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
                return;
            }

            // Execute real network call instead of mock
            searchSongsFromApi(query);
        });
    }

    /**
     * Fetches songs from the iTunes API using Retrofit
     */
    private void searchSongsFromApi(String query) {
        // Enqueue puts the network request on a background thread
        com.example.musicapp.network.ApiClient.getInstance().getApi().searchSongs(query, "music")
                .enqueue(new retrofit2.Callback<com.example.musicapp.models.ITunesResponse>() {

                    @Override
                    public void onResponse(retrofit2.Call<com.example.musicapp.models.ITunesResponse> call,
                                           retrofit2.Response<com.example.musicapp.models.ITunesResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            // Extract the list of songs from the JSON wrapper
                            List<com.example.musicapp.models.Song> realResults = response.body().getResults();

                            if (realResults.isEmpty()) {
                                Toast.makeText(requireContext(), "No songs found", Toast.LENGTH_SHORT).show();
                            }

                            // Send the data to the adapter to update the RecyclerView
                            songAdapter.setSongs(realResults);
                        } else {
                            Toast.makeText(requireContext(), "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.musicapp.models.ITunesResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Initializes the RecyclerView and its Adapter
     */
    private void setupRecyclerView() {
        songAdapter = new SongAdapter();
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSongs.setAdapter(songAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}