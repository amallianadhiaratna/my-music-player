package com.learn.mymusic.Activity;

import static com.learn.mymusic.ApplicationClass.ACTION_NEXT;
import static com.learn.mymusic.ApplicationClass.ACTION_PLAY;
import static com.learn.mymusic.ApplicationClass.ACTION_PREVIOUS;
import static com.learn.mymusic.ApplicationClass.CHANNEL_ID_2;
import static com.learn.mymusic.MusicAdapter.songList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.learn.mymusic.ActionPlaying;
import com.learn.mymusic.Model.SongModel;
import com.learn.mymusic.MusicEvent;
import com.learn.mymusic.MusicService;
import com.learn.mymusic.NotificationReceiver;
import com.learn.mymusic.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity  implements ActionPlaying, ServiceConnection {

    private ImageView CoverImage;
    private TextView SongTitle, SongArtist;
    private String song, artist, coverImage, url;
    public static List<SongModel> musicList;
    private ImageButton Play, Next, Prev;

    private SeekBar SeekBar;
    private TextView Pass, Due;
    private MusicService musicService;
    private Animation fade,upToDown;

    private Handler handler;
    private String out, out2;
    private Integer difference;
    private boolean isPlaying = false;
    private Integer position;
    private MediaSessionCompat mediaSessionCompat;
    private MusicEvent event;
    public static boolean SHOW_MINI_PLAYER = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        initViews();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
        getIntentMethod();
        isPlaying = true;
        handler = new Handler();
        initializeSeekBar();
//        playBtnClick();
//        prevBtnClick();
//        nextBtnClick();
        SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if( musicService!= null && fromUser){
                    musicService.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {

            }
        });
    }
    private void initPlayer(String song, String artist, String url, String coverImage){
        SongTitle.setText(song);
        SongArtist.setText(artist);
        SongTitle.setAnimation(fade);
        SongArtist.setAnimation(fade);
        Glide.with(this)
                .load(coverImage)
                .override(300,200)
                .into(CoverImage);

        CoverImage.setAnimation(upToDown);

        Log.d("MusicEvent","Event fired!");
        event.setArtistName(artist);
        event.setSongTitle(song);
        event.setCoverImage(coverImage);
        event.setPlay(true);
        EventBus.getDefault().post(event);
    }
    private void initViews(){
        upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);

        CoverImage = (ImageView) findViewById(R.id.player_cover_image);
        SongTitle = (TextView) findViewById(R.id.player_song_title);
        SongArtist = (TextView) findViewById(R.id.player_song_artist);

        Play = (ImageButton)findViewById(R.id.play_btn);
        SeekBar = (SeekBar)findViewById(R.id.seek_bar);
        Pass = (TextView)findViewById(R.id.tv_pass);
        Due = (TextView)findViewById(R.id.tv_due);

        Next = (ImageButton)findViewById(R.id.next);
        Prev = (ImageButton)findViewById(R.id.prev);
    }
    public void playPause() {
        Toast.makeText(this,"PlayPause",Toast.LENGTH_SHORT).show();
        event.setPlay(musicService.isPlaying());
        EventBus.getDefault().post(event);

        if(musicService.isPlaying()){
            showNotification(true);
            Play.setBackgroundResource(0);
            Play.setBackgroundResource(R.drawable.ic_baseline_play_circle_24);
            Log.d("PlayPause","Click to pause");//
            musicService.pause();
            isPlaying = false;
        }
        else{
            Log.d("PlayPause","Clicked to play");//
            showNotification(false);
            Play.setBackgroundResource(0);
            Play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_24);
            musicService.start();
            isPlaying = true;
        }
        SeekBar.setMax(musicService.getDuration()/1000);
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService!=null){
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    SeekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this,1000);
            }
        });
    }
    private void initializeSeekBar(){
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService != null){
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    SeekBar.setProgress(mCurrentPosition);
                    out = String.format("%02d:%02d", SeekBar.getProgress()/60, SeekBar.getProgress()%60);
                    Pass.setText(out);
                    difference = musicService.getDuration()/1000 - musicService.getCurrentPosition()/1000;
                    out2 = String.format("%02d:%02d", difference/60, difference%60);
                    Due.setText(out2);
                }
                handler.postDelayed(this,1000);
            }
        });
    }
    private void getIntentMethod(){
        position = Integer.parseInt(getIntent().getStringExtra("position"));
        musicList = songList;
        System.out.println(position);
        if(musicList!=null){
            Play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_24);
            isPlaying = true;
        }
        showNotification(false);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("servicePosition",position);
        startService(serviceIntent);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder myBinder =(MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.initActionPlaying(this);
        Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
        SeekBar.setMax(musicService.getDuration()/1000);
        event = new MusicEvent();

        Intent i = getIntent();
        song = i.getStringExtra("song");
        url = i.getStringExtra("url");
        artist = i.getStringExtra("artist");
        coverImage = i.getStringExtra("cover_image");
        initPlayer(song, artist, url, coverImage);
        position = Integer.parseInt(i.getStringExtra("position"));
        musicService.OnCompleted();
        Toast.makeText(this, song, Toast.LENGTH_SHORT).show();

        SHOW_MINI_PLAYER = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent,this, BIND_AUTO_CREATE);
        playBtnClick();
        nextBtnClick();
        prevBtnClick();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playBtnClick(){
        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
    }
    public void nextBtn(){
        musicService.stop();
        musicService.release();
        position=((position+1)%musicList.size());
        musicService.createMediaPlayer(position);
        SongTitle.setText(musicList.get(position).getSong());
        SongArtist.setText(musicList.get(position).getArtist());
        SeekBar.setMax(musicService.getDuration()/1000);
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService!=null){
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    SeekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this,1000);
            }
        });
        Glide.with(getApplicationContext())
                .load(musicList.get(position).getCover_image())
                .override(300,200)
                .into(CoverImage);
        musicService.OnCompleted();

        Log.d("MusicEvent","Event fired!");
        event.setArtistName(artist);
        event.setSongTitle(song);
        event.setCoverImage(coverImage);
        event.setPlay(musicService.isPlaying());
        EventBus.getDefault().post(event);
        if(musicService.isPlaying()){
            showNotification(false);
            Play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_24);
//            musicService.start();
        }else{
            showNotification(true);
            Play.setBackgroundResource(R.drawable.ic_baseline_play_circle_24);
//                            musicService.start();
        }
    }
    private void nextBtnClick(){
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtn();
            }
        });
    }
    public void prevBtn(){
        musicService.stop();
        musicService.release();
        position = ((position - 1) < 0 ? (musicList.size() - 1) : (position - 1));
        musicService.createMediaPlayer(position);
        SongTitle.setText(musicList.get(position).getSong());
        SongArtist.setText(musicList.get(position).getArtist());
        SeekBar.setMax(musicService.getDuration() / 1000);

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    SeekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
        Glide.with(this)
                .load(musicList.get(position).getCover_image())
                .override(300,200)
                .into(CoverImage);
        musicService.OnCompleted();

        Log.d("MusicEvent","Event fired!");
        event.setArtistName(artist);
        event.setSongTitle(song);
        event.setCoverImage(coverImage);
        event.setPlay(musicService.isPlaying());
        EventBus.getDefault().post(event);
        if (musicService.isPlaying()) {
            showNotification(false);
            Play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_24);
//            musicService.start();
        } else {
            showNotification(true);
            Play.setBackgroundResource(R.drawable.ic_baseline_play_circle_24);
//                            musicService.start();
        }
    }
    private void prevBtnClick(){
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevBtn();
            }
        });
    }

    void showNotification(boolean isCurrentlyPlay){
        int playPauseBtn = isCurrentlyPlay ? R.drawable.ic_baseline_play_arrow_24:R.drawable.ic_baseline_pause_24 ;
        Intent intent = new Intent(
                this,
                PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,intent,0);
        Intent prevIntent = new Intent(
                this,
                NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(
                this,
                0,
                prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(
                this,
                NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(
                this,
                0,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseIntent = new Intent(
                this,
                NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(
                this,
                0,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = ((BitmapDrawable)getResources().getDrawable(R.drawable.default_pic)).getBitmap();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(icon)
                .setContentTitle(musicList.get(position).getSong())
                .setContentText(musicList.get(position).getArtist())
                .addAction(R.drawable.skip_previous,"Previous",prevPending)
                .addAction(playPauseBtn, "pause", pausePending)
                .addAction(R.drawable.skip_next,"Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        if(event==null){
            event = new MusicEvent();
        }
        event.setArtistName(musicList.get(position).getArtist());
        event.setSongTitle(musicList.get(position).getSong());
        event.setCoverImage(musicList.get(position).getCover_image());
        event.setPlay(isCurrentlyPlay);
        EventBus.getDefault().post(event);

    }
    private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}