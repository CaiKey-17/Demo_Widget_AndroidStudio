package com.example.demo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.Model.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context context;
    private List<Music> musicList;
    private OnMusicClickListener listener;

    public MusicAdapter(Context context, List<Music> musicList, OnMusicClickListener listener) {
        this.context = context;
        this.musicList = musicList;
        this.listener = listener;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_mp3, parent, false);
        return new MusicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.title.setText(music.getName());
        holder.tvSinger.setText(music.getSinger());
        holder.itemView.setOnClickListener(v -> listener.onMusicClick(music));
        String imageUrl = music.getImage();
        if (imageUrl != null) {
            String fullImageUrl = "http://192.168.70.170:8080" + imageUrl;
            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public interface OnMusicClickListener {
        void onMusicClick(Music music);
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView title,tvSinger;
        ImageView imageView;

        MusicViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvFileName);
            tvSinger = itemView.findViewById(R.id.tvSinger);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
