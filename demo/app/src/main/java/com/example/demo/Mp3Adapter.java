package com.example.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.*;
public class Mp3Adapter extends RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder> {

    private final List<File> mp3Files;
    private final OnFileClickListener listener;

    public interface OnFileClickListener {
        void onFileClick(File file);
    }

    public Mp3Adapter(List<File> mp3Files, OnFileClickListener listener) {
        this.mp3Files = mp3Files;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mp3, parent, false);
        return new Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mp3ViewHolder holder, int position) {
        File file = mp3Files.get(position);
        holder.tvFileName.setText(file.getName());
        holder.itemView.setOnClickListener(v -> listener.onFileClick(file));
    }

    @Override
    public int getItemCount() {
        return mp3Files.size();
    }

    static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
        }
    }
}

