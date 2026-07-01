package com.example.musicapp.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musicapp.databinding.ItemSongBinding;
import com.example.musicapp.models.Song;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songsList = new ArrayList<>();
    private List<Song> favoriteSongs = new ArrayList<>();
    private int currentlyPlayingPosition = -1;

    private final OnSongInteractionListener listener;

    public interface OnSongInteractionListener {
        void onPlayClick(Song song);
        void onFavoriteClick(Song song, boolean isFavorite);
    }

    public SongAdapter(OnSongInteractionListener listener) {
        this.listener = listener;
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
        notifyDataSetChanged();
    }

    public void setSongs(List<Song> songs) {
        this.songsList = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding binding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songsList.get(position);
        holder.bind(currentSong, position);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        public SongViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, int position) {
            binding.tvTrackName.setText(song.getTrackName());
            binding.tvArtistName.setText(song.getArtistName());

            Glide.with(binding.getRoot().getContext())
                    .load(song.getArtworkUrl())
                    .into(binding.ivAlbumCover);

            // Check if song is in favorites (checking BOTH track and artist name to prevent duplicate name bugs)
            boolean isFavorite = false;
            for (Song favSong : favoriteSongs) {
                if (favSong.getTrackName().equals(song.getTrackName()) &&
                        favSong.getArtistName().equals(song.getArtistName())) {
                    isFavorite = true;
                    break;
                }
            }

            final boolean currentIsFavorite = isFavorite;

            // Set initial favorite icon color
            if (currentIsFavorite) {
                binding.btnFavorite.setColorFilter(Color.parseColor("#FFC107"));
            } else {
                binding.btnFavorite.setColorFilter(Color.WHITE);
            }

            // Set initial play/pause icon
            if (position == currentlyPlayingPosition) {
                binding.btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                binding.btnPlay.setImageResource(android.R.drawable.ic_media_play);
            }

            // Handle play button click
            binding.btnPlay.setOnClickListener(v -> {
                if (currentlyPlayingPosition == position) {
                    currentlyPlayingPosition = -1;
                    listener.onPlayClick(null);
                } else {
                    currentlyPlayingPosition = position;
                    listener.onPlayClick(song);
                }
                notifyDataSetChanged();
            });

            // Handle favorite button click
            binding.btnFavorite.setOnClickListener(v -> {
                listener.onFavoriteClick(song, currentIsFavorite);

                // Optimistic UI update
                if (currentIsFavorite) {
                    binding.btnFavorite.setColorFilter(Color.WHITE);
                } else {
                    binding.btnFavorite.setColorFilter(Color.parseColor("#FFC107"));
                }
            });
        }
    }
}