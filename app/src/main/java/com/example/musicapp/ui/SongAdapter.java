package com.example.musicapp.ui;

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
    private final OnSongInteractionListener listener;

    // Interface to handle button clicks inside the row
    public interface OnSongInteractionListener {
        void onPlayClick(Song song);
        void onFavoriteClick(Song song);
    }

    // Constructor now requires the listener
    public SongAdapter(OnSongInteractionListener listener) {
        this.listener = listener;
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
        holder.bind(currentSong, listener);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        public SongViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song, OnSongInteractionListener listener) {
            binding.tvTrackName.setText(song.getTrackName());
            binding.tvArtistName.setText(song.getArtistName());

            Glide.with(binding.getRoot().getContext())
                    .load(song.getArtworkUrl())
                    .into(binding.ivAlbumCover);

            // Handle Clicks
            binding.btnPlay.setOnClickListener(v -> listener.onPlayClick(song));
            binding.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(song));
        }
    }
}