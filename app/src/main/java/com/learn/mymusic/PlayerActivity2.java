//package com.learn.mymusic;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.learn.mymusic.Model.SongModel;
//
//import java.util.ArrayList;
//
//public class PlayerActivity2 extends AppCompatActivity implements {
//
//    ImageView CoverImage;
//    TextView SongTitle, SongArtist;
//
//    ImageButton Play;
//    MediaPlayer mediaPlayer;
//
//    SeekBar SeekBar;
//    TextView Pass, Due;
//    int position = -1;
//    static ArrayList<SongModel> listSong = new ArrayList<>();
//    Uri uri;
//    private void initViews(){
//        CoverImage = (ImageView) findViewById(R.id.player_cover_image);
//        SongTitle = (TextView) findViewById(R.id.player_song_title);
//        SongArtist = (TextView) findViewById(R.id.player_song_artist);
//
//        Play = (ImageButton)findViewById(R.id.play_btn);
//        SeekBar = (SeekBar)findViewById(R.id.seek_bar);
//        Pass = (TextView)findViewById(R.id.tv_pass);
//        Due = (TextView)findViewById(R.id.tv_due);
//    }
//    private void getIntentMethod(){
//        position = getIntent().getIntExtra("position", -1);
//        if(mediaPlayer!=null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
//        }
//        else{
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//            mediaPlayer.start();
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_player);
//        initViews();
//        getIntentMethod();
//        Animation upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
//        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
//
//
////        mediaPlayer = new MediaPlayer();
//
//        Intent i = getIntent();
//        String song = i.getStringExtra("song");
//        String url = i.getStringExtra("url");
//        String artist = i.getStringExtra("artist");
//        String coverImage = i.getStringExtra("cover_image");
//
//        Toast.makeText(this, song, Toast.LENGTH_SHORT).show();
//
//        SongTitle.setText(song);
//        SongArtist.setText(artist);
//        SongTitle.setAnimation(fade);
//        SongArtist.setAnimation(fade);
//        Glide.with(this)
//                .load(coverImage)
//                .override(300,200)
//                .into(CoverImage);
//
//        CoverImage.setAnimation(upToDown);
//
//        mediaPlayer.start();
//
//        Play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                play();
//            }
//        });
//
//        Intent serviceIntent = new Intent(this, MusicService.class);
////        serviceIntent.putExtra("servicePosition",position);
//        startService(serviceIntent);
//    }
//    public void play () {
//        if(mediaPlayer.isPlaying()){
//            Play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
//            mediaPlayer.pause();
//        }
//        else{
//            Play.setBackgroundResource(R.drawable.ic_baseline_pause_24);
//            mediaPlayer.start();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent, this, BIND_AUTO_CREATE);
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unbindService(this);
//    }
//
//    @Override
//    public void onServiceConnected(ComponentName componentName, IBinder service) {
//        MusicService.MyBinder myBinder =(MusicService.MyBinder) service;
//        musicService = myBinder.getService();
//        Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onServiceDisconnected(ComponentName componentName) {
//        musicService = null;
//    }
//}
//
//package com.learn.mymusic;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//
//import java.io.IOException;
//
//public class PlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {
//
//    ImageView CoverImage;
//    TextView SongTitle, SongArtist;
//
//    ImageButton Play;
////    MediaPlayer mediaPlayer;
//
//    SeekBar SeekBar;
//    TextView Pass, Due;
//    MusicService musicService;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_player);
//        Animation upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
//        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
//
//        CoverImage = (ImageView) findViewById(R.id.player_cover_image);
//        SongTitle = (TextView) findViewById(R.id.player_song_title);
//        SongArtist = (TextView) findViewById(R.id.player_song_artist);
//
//        Play = (ImageButton)findViewById(R.id.play_btn);
//        SeekBar = (SeekBar)findViewById(R.id.seek_bar);
//        Pass = (TextView)findViewById(R.id.tv_pass);
//        Due = (TextView)findViewById(R.id.tv_due);
////        mediaPlayer = new MediaPlayer();
//
//        Intent i = getIntent();
//        String song = i.getStringExtra("song");
//        String url = i.getStringExtra("url");
//        String artist = i.getStringExtra("artist");
//        String coverImage = i.getStringExtra("cover_image");
//        System.out.println(url);
//        Toast.makeText(this, song, Toast.LENGTH_SHORT).show();
//
//        SongTitle.setText(song);
//        SongArtist.setText(artist);
//        SongTitle.setAnimation(fade);
//        SongArtist.setAnimation(fade);
//        Glide.with(this)
//                .load(coverImage)
//                .override(300,200)
//                .into(CoverImage);
//
//        CoverImage.setAnimation(upToDown);
//        Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
////        serviceIntent.putExtra("position");
//        startService(serviceIntent);
//        musicService.setDataAndPrepare(url);
//        getIntentMethod();
//
////        musicService.start();
////
////        Play.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                play();
////            }
////        });
////
//
//
//    }
//    public void play () {
//        if(musicService.isPlaying()){
//            Play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
//            musicService.pause();
//        }
//        else{
//            Play.setBackgroundResource(R.drawable.ic_baseline_pause_24);
//            musicService.start();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent, this, BIND_AUTO_CREATE);
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unbindService(this);
//    }
//
//    @Override
//    public void onServiceConnected(ComponentName componentName, IBinder service) {
//        MusicService.MyBinder myBinder =(MusicService.MyBinder) service;
//        musicService = myBinder.getService();
//        Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onServiceDisconnected(ComponentName componentName) {
//        musicService = null;
//    }
//    private void getIntentMethod(){
//        if(musicService!=null){
//            musicService.stop();
//            musicService.release();
////            musicService = MediaPlayer.create(getApplicationContext(),uri);
//        }
//        else{
////            musicService = MediaPlayer.create(getApplicationContext(), uri);
//            musicService.start();
//        }
//    }
//}