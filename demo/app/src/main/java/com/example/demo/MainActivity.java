package com.example.demo;

import android.content.Context;
import android.content.Intent;
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
import android.Manifest;

import com.example.demo.Model.Music;
import com.example.demo.Retrofit.APIUser;
import com.example.demo.Retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static MediaPlayer mediaPlayer; // Static MediaPlayer for playing audio
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check for permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadListMenu(); // Load music data from the API
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadListMenu(); // Load music data if permission is granted
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Load music list from API
    private void loadListMenu() {
        RetrofitService retrofitService = new RetrofitService();
        APIUser api = retrofitService.getApiService();

        api.getAll().enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Music> musics = response.body();
                    populateListView(musics); // Populate RecyclerView with music data
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

    // Populate RecyclerView with music items
    private void populateListView(List<Music> musics) {
        MusicAdapter adapter = new MusicAdapter(MainActivity.this, musics, this::playMusic);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    // Play the selected music file
    private void playMusic(Music music) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release any previous instance
        }

        try {
            mediaPlayer = new MediaPlayer();

            // Use the IP address of your machine instead of "localhost"
            String musicUrl = "http://192.168.70.170:8080/"+music.getFileMp3();  // Example: "http://192.168.1.100:8080/images/saoem.mp3"
            Log.d("C",musicUrl);
            mediaPlayer.setDataSource(musicUrl); // Set the server URL
            mediaPlayer.prepareAsync();  // Prepare asynchronously
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();  // Start playback when media is prepared
                Toast.makeText(MainActivity.this, "Playing: " + music.getName(), Toast.LENGTH_SHORT).show();
                updateWidget(music.getName());  // Update widget with music title
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(MainActivity.this, "Finished: " + music.getName(), Toast.LENGTH_SHORT).show();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error occurred while playing the music.");
                Toast.makeText(MainActivity.this, "Error playing music", Toast.LENGTH_SHORT).show();
                return true;  // Return true if the error was handled
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error playing music", Toast.LENGTH_SHORT).show();
        }
    }

    // Update widget with current music info
    private void updateWidget(String musicTitle) {
        Intent intent = new Intent(this, MusicAppWidget.class);
        intent.setAction(MusicAppWidget.ACTION_UPDATE_WIDGET);
        intent.putExtra("MUSIC_TITLE", musicTitle);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer on destroy to prevent memory leaks
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void continuePlaying(Context context) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(context, "Music Resumed", Toast.LENGTH_SHORT).show();
        }
    }

    public static void pauseMusic(Context context) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();
        }
    }

    public static void seekForward(Context context, int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = mediaPlayer.getCurrentPosition() + milliseconds;
            mediaPlayer.seekTo(newPosition);
            Toast.makeText(context, "Seeked Forward", Toast.LENGTH_SHORT).show();
        }
    }

    public static void seekBackward(Context context, int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = mediaPlayer.getCurrentPosition() - milliseconds;
            mediaPlayer.seekTo(newPosition);
            Toast.makeText(context, "Seeked Backward", Toast.LENGTH_SHORT).show();
        }
    }
}
