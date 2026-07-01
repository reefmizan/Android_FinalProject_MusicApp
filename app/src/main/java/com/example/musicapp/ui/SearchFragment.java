package com.example.musicapp.ui;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapp.databinding.FragmentSearchBinding;
import com.example.musicapp.models.Song;

import java.io.IOException;

public class SearchFragment extends Fragment implements SongAdapter.OnSongInteractionListener {

    private FragmentSearchBinding binding;
    private SongAdapter songAdapter;
    private MediaPlayer mediaPlayer;
    private MusicViewModel musicViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        setupMediaPlayer();
        setupRecyclerView();
        observeViewModel();

        // Load user's favorites so the adapter knows which stars to paint yellow
        musicViewModel.loadFavoriteSongs();

        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearchQuery.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
                return;
            }
            musicViewModel.searchSongs(query);
        });
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter(this);
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSongs.setAdapter(songAdapter);
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    private void observeViewModel() {
        // Observe search results
        musicViewModel.getSearchResults().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                if (songs.isEmpty()) {
                    Toast.makeText(requireContext(), "No songs found", Toast.LENGTH_SHORT).show();
                }
                songAdapter.setSongs(songs);
            }
        });

        // Observe favorite songs to update the UI (Yellow Stars)
        musicViewModel.getFavoriteSongs().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites != null) {
                songAdapter.setFavoriteSongs(favorites);
            }
        });

        // Observe errors
        musicViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Implement Interface Methods ---

    @Override
    public void onPlayClick(Song song) {
        // Stop playback if song is null (pause toggle)
        if (song == null) {
            mediaPlayer.reset();
            return;
        }

        if (song.getPreviewUrl() == null || song.getPreviewUrl().isEmpty()) {
            Toast.makeText(requireContext(), "No audio preview available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getPreviewUrl());
            mediaPlayer.prepareAsync();

            Toast.makeText(requireContext(), "Loading audio...", Toast.LENGTH_SHORT).show();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(requireContext(), "Playing: " + song.getTrackName(), Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(Song song, boolean isFavorite) {
        if (isFavorite) {
            musicViewModel.removeSongFromFavorites(song);
            Toast.makeText(requireContext(), "Removing from favorites...", Toast.LENGTH_SHORT).show();
        } else {
            musicViewModel.addSongToFavorites(song);
            Toast.makeText(requireContext(), "Saving to favorites...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release media player resources to prevent memory leaks
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        binding = null;
    }
}