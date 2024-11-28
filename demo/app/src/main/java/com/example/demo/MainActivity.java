package com.example.demo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.Model.Music;
import com.example.demo.Retrofit.APIUser;
import com.example.demo.Retrofit.RetrofitService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static MediaPlayer mediaPlayer;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private List<Music> musicList;
    private int currentPosition = -1;

    public static final String ACTION_PLAY_NEXT = "com.example.demo.ACTION_PLAY_NEXT";
    public static final String ACTION_PLAY_PREVIOUS = "com.example.demo.ACTION_PLAY_PREVIOUS";
    private final BroadcastReceiver musicControlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_PLAY_NEXT:
                        playNextSong();
                        break;

                    case ACTION_PLAY_PREVIOUS:
                        playPreviousSong();
                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkPermissionsAndLoadMusic();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY_NEXT);
        filter.addAction(ACTION_PLAY_PREVIOUS);
        registerReceiver(musicControlReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicControlReceiver);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void checkPermissionsAndLoadMusic() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO}, PERMISSION_REQUEST_CODE);
            } else {
                loadListMenu();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                loadListMenu();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadListMenu();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadListMenu() {
        RetrofitService retrofitService = new RetrofitService();
        APIUser api = retrofitService.getApiService();

        api.getAll().enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    musicList = response.body();
                    populateListView(musicList);
                } else {
                    Log.e("API Error", "Response was unsuccessful or body is null.");
                }
            }

            @Override
            public void onFailure(Call<List<Music>> call, Throwable throwable) {
                Log.e("API Error", "Failed to load music", throwable);
            }
        });
    }

    private void populateListView(List<Music> musics) {
        MusicAdapter adapter = new MusicAdapter(this, musics, this::onMusicSelected);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void onMusicSelected(Music music) {
        currentPosition = musicList.indexOf(music);
        playMusic(music);
    }

    private void playMusic(Music music) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        try {
            mediaPlayer = new MediaPlayer();
            String musicUrl = "http://192.168.70.170:8080/" + music.getFileMp3();
            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                Toast.makeText(this, "Đang phát: " + music.getName(), Toast.LENGTH_SHORT).show();
                updateWidget(music.getName(), music.getSinger(), "http://192.168.70.170:8080/" + music.getImage());
            });

            mediaPlayer.setOnCompletionListener(mp -> playNextSong());

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error occurred while playing the music.");
                Toast.makeText(this, "Error playing music", Toast.LENGTH_SHORT).show();
                return true;
            });

        } catch (IOException e) {
            Log.e("MediaPlayer", "Error setting data source", e);
        }
    }

    private void updateWidget(String musicTitle, String singer, String image) {
        Intent intent = new Intent(this, MusicAppWidget.class);
        intent.setAction(MusicAppWidget.ACTION_UPDATE_WIDGET);
        intent.putExtra("MUSIC_TITLE", musicTitle);
        intent.putExtra("SINGER", singer);
        intent.putExtra("MUSIC_IMAGE", image);
        sendBroadcast(intent);
    }

    private void playNextSong() {
        if (musicList != null && currentPosition < musicList.size() - 1) {
            currentPosition++;
            playMusic(musicList.get(currentPosition));
        } else {
            Toast.makeText(this, "Đây là bài cuối cùng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPreviousSong() {
        if (musicList != null && currentPosition > 0) {
            currentPosition--;
            playMusic(musicList.get(currentPosition));
        } else {
            Toast.makeText(this, "Đây là bài đầu tiên!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void continuePlaying(Context context) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(context, "Tiếp tục phát", Toast.LENGTH_SHORT).show();
        }
    }

    public static void pauseMusic(Context context) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(context, "Tạm dừng", Toast.LENGTH_SHORT).show();
        }
    }

    public static void seekForward(Context context, int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = mediaPlayer.getCurrentPosition() + milliseconds;
            mediaPlayer.seekTo(newPosition);
            Toast.makeText(context, "Tua nhanh 5s", Toast.LENGTH_SHORT).show();
        }
    }

    public static void seekBackward(Context context, int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = mediaPlayer.getCurrentPosition() - milliseconds;
            mediaPlayer.seekTo(newPosition);
            Toast.makeText(context, "Tua lùi 5s", Toast.LENGTH_SHORT).show();
        }
    }
    
}
