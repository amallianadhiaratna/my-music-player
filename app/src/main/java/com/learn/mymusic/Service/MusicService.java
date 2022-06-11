package com.learn.mymusic.Service;

import static com.learn.mymusic.Activity.PlayerActivity.musicList;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.learn.mymusic.ActionPlaying;
import com.learn.mymusic.Model.SongModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private IBinder mBinder = new MyBinder();
    private MediaPlayer mediaPlayer;
    private List<SongModel> allMusicList = new ArrayList<>();

    private int position = -1;
    ActionPlaying actionPlaying;
    @Override
    public void onCreate() {
        super.onCreate();
        allMusicList = musicList;
        System.out.println(allMusicList);
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
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
        public MusicService getService(){
            return  MusicService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind","Method");
        return mBinder;
    }

    public void start(){
        mediaPlayer.start();
    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void stop (){
        mediaPlayer.stop();
    }
    public void release(){
        mediaPlayer.release();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    public void createMediaPlayer(int positionInner) {
        position = positionInner;
        Log.i("Create media player", "is happening");
        Log.i("create media player",allMusicList.get(positionInner).getUrl());
        System.out.println(allMusicList);
//        uri = Uri.parse(allMusicList.get(positionInner).getUrl());
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(allMusicList.get(positionInner).getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            Log.d("SERVICE_ERROR", "Can't start media player");
            e.printStackTrace();
        }
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
    }
    public  void pause (){
        mediaPlayer.pause();
    }
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public  void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(actionPlaying !=null){
            actionPlaying.nextBtn();
            if(mediaPlayer!=null){
//                createMediaPlayer(position);
                mediaPlayer.start();
                OnCompleted();
            }
        }
//        createMediaPlayer(position);
        start();
        OnCompleted();
    }

    public  void initActionPlaying (ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
