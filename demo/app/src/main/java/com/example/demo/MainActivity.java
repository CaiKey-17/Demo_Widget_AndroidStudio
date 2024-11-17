package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.*;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static MediaPlayer mediaPlayer; // MediaPlayer là static để có thể sử dụng trong widget
    private static File currentFile; // Lưu trữ tệp hiện tại đang phát
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadMp3Files();
        }
    }

    private void loadMp3Files() {
        List<File> mp3Files = getMp3Files();
        Mp3Adapter adapter = new Mp3Adapter(mp3Files, file -> {
            // Gọi phương thức playMusic khi người dùng chọn file
            playMusic(file);
        });
        recyclerView.setAdapter(adapter);
    }

    private List<File> getMp3Files() {
        List<File> mp3Files = new ArrayList<>();
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (downloadsDir != null && downloadsDir.exists()) {
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".mp3")) {
                        mp3Files.add(file);
                    }
                }
            }
        }
        return mp3Files;
    }

    private void playMusic(File file) {
        // Giải phóng MediaPlayer cũ nếu có
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Lưu tệp nhạc hiện tại
        currentFile = file;

        // Tạo và phát nhạc
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(file));
        mediaPlayer.start();
        Toast.makeText(this, "Playing: " + file.getName(), Toast.LENGTH_SHORT).show();

        // Cập nhật widget với tên bài nhạc
        updateWidget(file);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng MediaPlayer khi Activity bị hủy
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateWidget(File file) {
        Intent intent = new Intent(this, MusicAppWidget.class);
        intent.setAction(MusicAppWidget.ACTION_UPDATE_WIDGET);
        intent.putExtra("MUSIC_TITLE", file.getName());
        sendBroadcast(intent);
    }


    // Phương thức cho MusicAppWidget để tiếp tục phát nhạc
    public static void continuePlaying(Context context) {
        if (mediaPlayer != null && currentFile != null) {
            mediaPlayer.start();
        }
    }

    public static void pauseMusic(Context context) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(context, "Paused: " + currentFile.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void seekForward(Context context, int milliseconds) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int newPosition = mediaPlayer.getCurrentPosition() + milliseconds;
            if (newPosition < mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(newPosition);
                Toast.makeText(context, "Seeked Forward", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void seekBackward(Context context, int milliseconds) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int newPosition = mediaPlayer.getCurrentPosition() - milliseconds;
            if (newPosition > 0) {
                mediaPlayer.seekTo(newPosition);
                Toast.makeText(context, "Seeked Backward", Toast.LENGTH_SHORT).show();
            }
        }
    }



}