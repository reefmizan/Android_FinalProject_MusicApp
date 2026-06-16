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

    // Method to update the list when new search results arrive
    public void setSongs(List<Song> songs) {
        this.songsList = songs;
        notifyDataSetChanged(); // Refreshes the UI
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout using ViewBinding
        ItemSongBinding binding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songsList.get(position);
        holder.bind(currentSong);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    // ViewHolder class holds the views for a single item row
    static class SongViewHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        public SongViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song) {
            binding.tvTrackName.setText(song.getTrackName());
            binding.tvArtistName.setText(song.getArtistName());

            // Use Glide library to load the image from the URL into the ImageView
            Glide.with(binding.getRoot().getContext())
                    .load(song.getArtworkUrl())
                    .into(binding.ivAlbumCover);
        }
    }
}