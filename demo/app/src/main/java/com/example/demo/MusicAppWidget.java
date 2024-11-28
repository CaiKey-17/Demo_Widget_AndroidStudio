package com.example.demo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;

public class MusicAppWidget extends AppWidgetProvider {

    public static final String ACTION_PLAY = "com.example.demo.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.demo.ACTION_PAUSE";
    public static final String ACTION_UPDATE_WIDGET = "com.example.demo.ACTION_UPDATE_WIDGET";
    public static final String ACTION_SEEK_FORWARD = "com.example.demo.ACTION_SEEK_FORWARD";
    public static final String ACTION_SEEK_BACKWARD = "com.example.demo.ACTION_SEEK_BACKWARD";
    public static final String ACTION_SEEK_NEXT = "com.example.demo.ACTION_SEEK_NEXT";
    public static final String ACTION_SEEK_PREVIOUS = "com.example.demo.ACTION_SEEK_PREVIOUS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_app_widget);

            Intent playIntent = new Intent(context, MusicAppWidget.class);
            playIntent.setAction(ACTION_PLAY);
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_play, playPendingIntent);

            Intent pauseIntent = new Intent(context, MusicAppWidget.class);
            pauseIntent.setAction(ACTION_PAUSE);
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_pause, pausePendingIntent);

            Intent backwardIntent = new Intent(context, MusicAppWidget.class);
            backwardIntent.setAction(ACTION_SEEK_BACKWARD);
            PendingIntent backwardPendingIntent = PendingIntent.getBroadcast(context, 2, backwardIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_seek_backward, backwardPendingIntent);

            Intent forwardIntent = new Intent(context, MusicAppWidget.class);
            forwardIntent.setAction(ACTION_SEEK_FORWARD);
            PendingIntent forwardPendingIntent = PendingIntent.getBroadcast(context, 3, forwardIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_seek_forward, forwardPendingIntent);

            Intent nextIntent = new Intent(context, MusicAppWidget.class);
            nextIntent.setAction(ACTION_SEEK_NEXT);
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 4, nextIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_seek_next, nextPendingIntent);

            Intent previousIntent = new Intent(context, MusicAppWidget.class);
            previousIntent.setAction(ACTION_SEEK_PREVIOUS);
            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 5, previousIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_seek_prev, previousPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    MainActivity.continuePlaying(context);
                    updatePlayPauseState(context, true);
                    break;

                case ACTION_PAUSE:
                    MainActivity.pauseMusic(context);
                    updatePlayPauseState(context, false);
                    break;

                case ACTION_SEEK_FORWARD:
                    MainActivity.seekForward(context, 5000);
                    break;

                case ACTION_SEEK_BACKWARD:
                    MainActivity.seekBackward(context, 5000);
                    break;

                case ACTION_SEEK_NEXT:
                    Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                    Intent nextIntent = new Intent(MainActivity.ACTION_PLAY_NEXT);
                    context.sendBroadcast(nextIntent);
                    break;

                case ACTION_SEEK_PREVIOUS:
                    Intent previousIntent = new Intent(MainActivity.ACTION_PLAY_PREVIOUS);
                    context.sendBroadcast(previousIntent);
                    break;

                case ACTION_UPDATE_WIDGET:
                    String musicTitle = intent.getStringExtra("MUSIC_TITLE");
                    String singer = intent.getStringExtra("SINGER");
                    String musicImageUrl = intent.getStringExtra("MUSIC_IMAGE");
                    updateWidgetUI(context, musicTitle, singer, musicImageUrl);
                    break;
            }
        }
    }



    private void updatePlayPauseState(Context context, boolean isPlaying) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_app_widget);

        if (isPlaying) {
            views.setViewVisibility(R.id.widget_btn_play, View.GONE);
            views.setViewVisibility(R.id.widget_btn_pause, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.widget_btn_play, View.VISIBLE);
            views.setViewVisibility(R.id.widget_btn_pause, View.GONE);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, MusicAppWidget.class);
        appWidgetManager.updateAppWidget(widget, views);
    }

    private void updateWidgetUI(Context context, String musicTitle, String singer, String imageUrl) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_app_widget);
        views.setTextViewText(R.id.widget_text_title, musicTitle);
        views.setTextViewText(R.id.widget_text_singer, "- " + singer + " -");

        views.setViewVisibility(R.id.widget_btn_play, View.GONE);
        views.setViewVisibility(R.id.widget_btn_pause, View.VISIBLE);
        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_song_image, views,
                new ComponentName(context, MusicAppWidget.class));

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(appWidgetTarget);


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, MusicAppWidget.class);
        appWidgetManager.updateAppWidget(widget, views);
    }

    private void broadcastToMainActivity(Context context, String action) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(action);
        context.sendBroadcast(intent);
    }
}
