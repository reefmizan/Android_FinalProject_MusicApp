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

import com.example.musicapp.databinding.FragmentFavoritesBinding;
import com.example.musicapp.models.Song;

import java.io.IOException;

public class FavoritesFragment extends Fragment implements SongAdapter.OnSongInteractionListener {

    private FragmentFavoritesBinding binding;
    private SongAdapter songAdapter;
    private MediaPlayer mediaPlayer;
    private MusicViewModel musicViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        setupMediaPlayer();
        setupRecyclerView();
        observeViewModel();

        musicViewModel.loadFavoriteSongs();
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter(this);
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavorites.setAdapter(songAdapter);
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
        // Listen for favorite songs updates
        musicViewModel.getFavoriteSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                if (songs.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                    binding.rvFavorites.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                    binding.rvFavorites.setVisibility(View.VISIBLE);

                    // Pass the list to display AND the list for the yellow star highlight
                    songAdapter.setSongs(songs);
                    songAdapter.setFavoriteSongs(songs);
                }
            }
        });

        // Listen for error messages
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
        // Always remove from favorites when clicked inside the Favorites screen
        musicViewModel.removeSongFromFavorites(song);
        Toast.makeText(requireContext(), "Removing from favorites...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        binding = null;
    }
}