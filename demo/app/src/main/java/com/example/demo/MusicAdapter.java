package com.example.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.itemView.setOnClickListener(v -> listener.onMusicClick(music));
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public interface OnMusicClickListener {
        void onMusicClick(Music music);
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        MusicViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvFileName);
        }
    }
}
