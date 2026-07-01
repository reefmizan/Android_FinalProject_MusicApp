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

    // The ViewModel instance
    private MusicViewModel musicViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize the ViewModel
        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        setupMediaPlayer();
        setupRecyclerView();

        // 2. Start observing data changes from the ViewModel
        observeViewModel();

        // 3. Trigger fetching favorites from Firestore when the screen opens
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

    /**
     * Observes the LiveData streams from the ViewModel.
     */
    private void observeViewModel() {
        // Listen for the favorite songs list
        musicViewModel.getFavoriteSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                if (songs.isEmpty()) {
                    // Show empty state text if there are no favorites
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                    binding.rvFavorites.setVisibility(View.GONE);
                } else {
                    // Show the RecyclerView and hide empty state
                    binding.tvEmptyState.setVisibility(View.GONE);
                    binding.rvFavorites.setVisibility(View.VISIBLE);
                    songAdapter.setSongs(songs);
                }
            }
        });

        // Listen for error messages (network/database failures)
        musicViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Implement Interface Methods ---

    @Override
    public void onPlayClick(Song song) {
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
    public void onFavoriteClick(Song song) {
        // Delegate the delete operation to the ViewModel
        musicViewModel.removeSongFromFavorites(song);
        Toast.makeText(requireContext(), "Removing from favorites...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the media player resources when leaving the screen
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        binding = null;
    }
}