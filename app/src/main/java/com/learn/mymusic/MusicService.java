package com.learn.mymusic;

import static com.learn.mymusic.PlayerActivity.arrayListArtist;
import static com.learn.mymusic.PlayerActivity.arrayListImage;
import static com.learn.mymusic.PlayerActivity.arrayListSong;
import static com.learn.mymusic.PlayerActivity.arrayListUrl;
import static com.learn.mymusic.PlayerActivity.musicList;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.learn.mymusic.Model.SongModel;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<String> listUrl = new ArrayList<>();
    ArrayList<String> listSong = new ArrayList<>();
    ArrayList<String> listArtist = new ArrayList<>();
    ArrayList<String> listImage = new ArrayList<>();
    static List<SongModel> allMusicList = new ArrayList<>();

    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    @Override
    public void onCreate() {
        super.onCreate();
        allMusicList = musicList;
        listSong = arrayListSong;
        listUrl = arrayListUrl;
        listArtist = arrayListArtist;
        listImage = arrayListImage;
        System.out.println(allMusicList);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition!=-1){
            playMedia(myPosition);
        }
        if(actionName!=null){
            switch (actionName){
                case "playPause":
                    Toast.makeText(this,"PlayPause",Toast.LENGTH_SHORT).show();
                    if(actionPlaying!=null){
                        Log.e("Inside","Action");
                        actionPlaying.playPause();
                    }
                    break;
                case "next":
                    Toast.makeText(this,"Next",Toast.LENGTH_SHORT).show();
                    if(actionPlaying!=null){
                        Log.e("Inside","Action");
                        actionPlaying.nextBtn();
                    }
                    break;
                case "previous":
                    Toast.makeText(this,"Previous",Toast.LENGTH_SHORT).show();
                    if(actionPlaying!=null){
                        Log.e("Inside","Action");
                        actionPlaying.prevBtn();
                    }
                    break;}
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition){
        allMusicList = musicList;
        position = startPosition;
        if(mediaPlayer!=null){
            stop();
            release();
            if(allMusicList!=null){
                createMediaPlayer(position);
                start();
            }
        }
        else {
            createMediaPlayer(position);
            start();
        }
    }
    public class MyBinder extends Binder {
        MusicService getService(){
            return  MusicService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind","Method");
        return mBinder;
    }

    void start(){
        mediaPlayer.start();
    }
    boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    void stop (){
        mediaPlayer.stop();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration() {
        return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int positionInner) {
        position = positionInner;
        System.out.println(allMusicList);
        uri = Uri.parse(allMusicList.get(positionInner).getUrl());
        System.out.println(uri);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

    }
    void pause (){
        mediaPlayer.pause();
    }
    int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(actionPlaying !=null){
            actionPlaying.nextBtn();
            if(mediaPlayer!=null){
                createMediaPlayer(position);
                mediaPlayer.start();
                OnCompleted();
            }
        }
        createMediaPlayer(position);
        start();
        OnCompleted();
    }

    void initActionPlaying (ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
