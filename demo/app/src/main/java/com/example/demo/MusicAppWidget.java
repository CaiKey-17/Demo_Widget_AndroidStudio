package com.example.demo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.RemoteViews;

public class MusicAppWidget extends AppWidgetProvider {

    public static final String ACTION_PLAY = "com.example.demo.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.demo.ACTION_PAUSE";
    public static final String ACTION_UPDATE_WIDGET = "com.example.demo.ACTION_UPDATE_WIDGET";
    public static final String ACTION_SEEK_FORWARD = "com.example.demo.ACTION_SEEK_FORWARD";
    public static final String ACTION_SEEK_BACKWARD = "com.example.demo.ACTION_SEEK_BACKWARD";


    private MediaPlayer mediaPlayer;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_app_widget);

            // Intent cho nút Play
            Intent playIntent = new Intent(context, MusicAppWidget.class);
            playIntent.setAction(ACTION_PLAY);
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_play, playPendingIntent);

            // Intent cho nút Pause
            Intent pauseIntent = new Intent(context, MusicAppWidget.class);
            pauseIntent.setAction(ACTION_PAUSE);
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_pause, pausePendingIntent);

            // Intent cho nút Seek Backward
            Intent backwardIntent = new Intent(context, MusicAppWidget.class);
            backwardIntent.setAction(ACTION_SEEK_BACKWARD);
            PendingIntent backwardPendingIntent = PendingIntent.getBroadcast(context, 2, backwardIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_seek_backward, backwardPendingIntent);

            // Intent cho nút Seek Forward
            Intent forwardIntent = new Intent(context, MusicAppWidget.class);
            forwardIntent.setAction(ACTION_SEEK_FORWARD);
            PendingIntent forwardPendingIntent = PendingIntent.getBroadcast(context, 3, forwardIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_btn_seek_forward, forwardPendingIntent);


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
                    MainActivity.seekForward(context, 5000); // Tua nhanh 5 giây
                    break;

                case ACTION_SEEK_BACKWARD:
                    MainActivity.seekBackward(context, 5000); // Tua lùi 5 giây
                    break;

                case ACTION_UPDATE_WIDGET:
                    String musicTitle = intent.getStringExtra("MUSIC_TITLE");
                    updateWidgetUI(context, musicTitle);
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

        // Cập nhật widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, MusicAppWidget.class);
        appWidgetManager.updateAppWidget(widget, views);
    }

    private void updateWidgetUI(Context context, String musicTitle) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_app_widget);
        views.setTextViewText(R.id.widget_text_title, "Playing: " + musicTitle);

        // Cập nhật widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, MusicAppWidget.class);
        appWidgetManager.updateAppWidget(widget, views);
    }


}
