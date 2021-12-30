package com.learn.mymusic;

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
import com.learn.mymusic.Model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity  implements ActionPlaying, ServiceConnection {

    ImageView CoverImage;
    TextView SongTitle, SongArtist;
    String song, artist,coverImage,url;
    static List<SongModel> musicList;
    static Uri uri;
    ImageButton Play, Next, Prev;
//    MediaPlayer mediaPlayer;
    private Thread playThread, nextThread, prevThread;
    SeekBar SeekBar;
    TextView Pass, Due;
    MusicService musicService;
    Animation fade,upToDown;

    Handler handler;
    String out, out2;
    Integer difference;
    private boolean isPlaying = false;
    public static ArrayList<String> arrayListUrl;
    public static ArrayList<String> arrayListSong;
    public static ArrayList<String> arrayListArtist;
    public static ArrayList<String> arrayListImage;
    Integer position;
    MediaSessionCompat mediaSessionCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        initViews();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
        getIntentMethod();

        handler = new Handler();
//        initPlayer(song, artist, url, coverImage);
        initializeSeekBar();
        SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if(musicService!= null && fromUser){
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
//        musicService.createMediaPlayer(position);
//        musicService.start();
//        musicService.OnCompleted();
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
        if(musicService.isPlaying()){
            showNotification(R.drawable.ic_baseline_play_arrow_24);
            Play.setImageResource(R.drawable.ic_baseline_play_circle_24);
            Log.e("PlayPause","Clicked");//
            musicService.pause();
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
            isPlaying = false;
        }
        else{
            showNotification(R.drawable.ic_baseline_pause_24);
            Play.setImageResource(R.drawable.ic_baseline_pause_circle_24);
            musicService.start();
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
            isPlaying = true;
        }
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
            uri = Uri.parse(getIntent().getStringExtra("url"));
            isPlaying = true;
        }
        showNotification(R.drawable.ic_baseline_pause_24);
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

        Intent i = getIntent();
        song = i.getStringExtra("song");
        url = i.getStringExtra("url");
        artist = i.getStringExtra("artist");
        coverImage = i.getStringExtra("cover_image");
        initPlayer(song, artist, url, coverImage);
        isPlaying = true;
        arrayListUrl = i.getStringArrayListExtra("arrayListUrl");
        arrayListSong = i.getStringArrayListExtra("arrayListSong");
        arrayListArtist = i.getStringArrayListExtra("arrayListArtist");
        arrayListImage = i.getStringArrayListExtra("arrayListImage");
        position = Integer.parseInt(i.getStringExtra("position"));
        musicService.OnCompleted();
        System.out.println(position);
        System.out.println(arrayListUrl);

        Toast.makeText(this, song, Toast.LENGTH_SHORT).show();
        System.out.println("size :           " + arrayListUrl.size());
        System.out.println("arrayListSong :           " + arrayListSong.size());
        System.out.println("arrayListArtist :           " + arrayListArtist.size());
        System.out.println("arrayListImage :           " + arrayListImage.size());
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent,this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playThreadBtn(){
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                Play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPause();
                    }
                });
            }
        };
        playThread.start();
    }
    public void nextBtn(){
//        if(arrayListUrl.size()==position+1){
//            position=0;
//            initPlayer(arrayListSong.get(position),
//                    arrayListArtist.get(position),
//                    arrayListUrl.get(position),
//                    arrayListImage.get(position));
//        }
//        else{
//            position = position +1;
//            initPlayer(arrayListSong.get(position),
//                    arrayListArtist.get(position),
//                    arrayListUrl.get(position),
//                    arrayListImage.get(position));
//        }
//        if(musicService.isPlaying()){
//            musicService.stop();
//        }
//        musicService.start();
        if(musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            position=((position+1)%musicList.size());
            uri = Uri.parse(getIntent().getStringExtra("url"));
            musicService.createMediaPlayer(position);
//            metaData(uri);
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
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_pause_24);
            Play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_24);
            musicService.start();
        }else{
            musicService.stop();
            musicService.release();
            position=((position+1)%musicList.size());
            uri = Uri.parse(getIntent().getStringExtra("url"));
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
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_play_arrow_24);
            Play.setBackgroundResource(R.drawable.ic_baseline_play_circle_24);
//                            musicService.start();
        }
    }
    private void nextThreadBtn(){
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                Play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtn();
                    }
                });
            }
        };
        nextThread.start();
    }
    public void prevBtn(){
//                        if(position==0){
//                            position = arrayListUrl.size()-1;
//                            initPlayer(arrayListSong.get(position),
//                                    arrayListArtist.get(position),
//                                    arrayListUrl.get(position),
//                                    arrayListImage.get(position));
//                        }else{
//                            position = position-1;
//                            initPlayer(arrayListSong.get(position),
//                                    arrayListArtist.get(position),
//                                    arrayListUrl.get(position),
//                                    arrayListImage.get(position));
//
//                        }
//                        musicService.createMediaPlayer(position);
//                        if(!musicService.isPlaying()) {
//                            musicService.start();
//                        }
//                    }
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (musicList.size() - 1) : (position - 1));
            uri = Uri.parse(getIntent().getStringExtra("url"));
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
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_pause_24);
            Play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_24);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (musicList.size() - 1) : (position - 1));
            uri = Uri.parse(getIntent().getStringExtra("url"));
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
            musicService.OnCompleted();
            showNotification(R.drawable.ic_baseline_play_arrow_24);
            Play.setBackgroundResource(R.drawable.ic_baseline_play_circle_24);
//                            musicService.start();
        }
    }
    private void prevThreadBtn(){
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                Play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    prevBtn();
                    }
                });
            }
        };
        prevThread.start();
    }
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
//        int durationTotal = musicService.getDuration();
//        durationLength.setText(String.format("%02d:%02d",durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        if(art!=null){
            Glide.with(this)
                    .asBitmap()
                    .load(coverImage)
                    .override(300,200)
                    .into(CoverImage);
        }else{
            Glide.with(this).asBitmap().load(R.drawable.default_pic).into(CoverImage);
        }
    }

    void showNotification(int playPauseBtn){
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
    private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}