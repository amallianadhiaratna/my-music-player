package com.learn.mymusic;

import static com.learn.mymusic.MyAdapter.songList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
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

public class PlayerActivity extends AppCompatActivity  implements ActionPlaying, ServiceConnection, MediaPlayer.OnCompletionListener {

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

    public static ArrayList<String> arrayListUrl;
    public static ArrayList<String> arrayListSong;
    public static ArrayList<String> arrayListArtist;
    public static ArrayList<String> arrayListImage;
    Integer position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        handler = new Handler();

        initPlayer(song, artist, url, coverImage);
        initializeSeekBar();
        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrayListUrl.size()==position+1){
                    position=0;
                    initPlayer(arrayListSong.get(position),
                            arrayListArtist.get(position),
                            arrayListUrl.get(position),
                            arrayListImage.get(position));
                }
                else{
                    position = position +1;
                    initPlayer(arrayListSong.get(position),
                            arrayListArtist.get(position),
                            arrayListUrl.get(position),
                            arrayListImage.get(position));
                }
            }
        });
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position==0){
                    position = arrayListUrl.size()-1;
                    initPlayer(arrayListSong.get(position),
                            arrayListArtist.get(position),
                            arrayListUrl.get(position),
                            arrayListImage.get(position));
                }else{
                    position = position-1;
                    initPlayer(arrayListSong.get(position),
                            arrayListArtist.get(position),
                            arrayListUrl.get(position),
                            arrayListImage.get(position));
                }
            }
        });
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
            Play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            musicService.pause();
            SeekBar.setMax(musicService.getDuration()/1000);

//            PlayerActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(musicService!=null){
//                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
//                        SeekBar.setProgress(mCurrentPosition);
//                    }
//                    handler.postDelayed(this,1000);
//                }
//            });
        }
        else{
            Play.setImageResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
//            PlayerActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(musicService!=null){
//                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
//                        SeekBar.setProgress(mCurrentPosition);
//                    }
//                    handler.postDelayed(this,1000);
//                }
//            });
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
            Play.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(getIntent().getStringExtra("url"));
        }
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("servicePosition",position);
        startService(serviceIntent);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder myBinder =(MusicService.MyBinder) service;
        musicService = myBinder.getService();
        Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
        SeekBar.setMax(musicService.getDuration()/1000);

        Intent i = getIntent();
        song = i.getStringExtra("song");
        url = i.getStringExtra("url");
        artist = i.getStringExtra("artist");
        coverImage = i.getStringExtra("cover_image");
        initPlayer(song, artist, url, coverImage);

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
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(musicService!=null){
            musicService.createMediaPlayer(position);
            musicService.start();
            musicService.OnCompleted();
        }
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
    private void nextThreadBtn(){
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                Play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(arrayListUrl.size()==position+1){
                            position=0;
                            initPlayer(arrayListSong.get(position),
                                    arrayListArtist.get(position),
                                    arrayListUrl.get(position),
                                    arrayListImage.get(position));
                        }
                        else{
                            position = position +1;
                            initPlayer(arrayListSong.get(position),
                                    arrayListArtist.get(position),
                                    arrayListUrl.get(position),
                                    arrayListImage.get(position));
                        }
                    }
//                        if(musicService.isPlaying()){
//                            musicService.stop();
//                            musicService.release();
//                            position=((position+1)%musicList.size());
//                            uri = Uri.parse(getIntent().getStringExtra("url"));
//                            musicService.createMediaPlayer(position);
//                            SongTitle.setText(musicList.get(position).getSong());
//                            SongArtist.setText(musicList.get(position).getArtist());
//                            SeekBar.setMax(musicService.getDuration()/1000);
//
//                            PlayerActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(musicService!=null){
//                                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
//                                        SeekBar.setProgress(mCurrentPosition);
//                                    }
//                                    handler.postDelayed(this,1000);
//                                }
//                            });
//                            Play.setImageResource(R.drawable.ic_baseline_pause_24);
//                            musicService.start();
//                        }else{
//                            musicService.stop();
//                            musicService.release();
//                            position=((position+1)%musicList.size());
//                            uri = Uri.parse(getIntent().getStringExtra("url"));
//                            musicService.createMediaPlayer(position);
//                            SongTitle.setText(musicList.get(position).getSong());
//                            SongArtist.setText(musicList.get(position).getArtist());
//                            SeekBar.setMax(musicService.getDuration()/1000);
//
//                            PlayerActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(musicService!=null){
//                                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
//                                        SeekBar.setProgress(mCurrentPosition);
//                                    }
//                                    handler.postDelayed(this,1000);
//                                }
//                            });
//                            Play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
////                            musicService.start();
//                        }
//                    }
                });
            }
        };
        nextThread.start();
    }
    private void prevThreadBtn(){
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                Play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position==0){
                            position = arrayListUrl.size()-1;
                            initPlayer(arrayListSong.get(position),
                                    arrayListArtist.get(position),
                                    arrayListUrl.get(position),
                                    arrayListImage.get(position));
                        }else{
                            position = position-1;
                            initPlayer(arrayListSong.get(position),
                                    arrayListArtist.get(position),
                                    arrayListUrl.get(position),
                                    arrayListImage.get(position));
                        }
                    }
//                        if(musicService.isPlaying()){
//                            musicService.stop();
//                            musicService.release();
//                            position=((position-1)<0 ? (musicList.size()-1) : (position-1));
//                            uri = Uri.parse(getIntent().getStringExtra("url"));
//                            musicService.createMediaPlayer(position);
//                            SongTitle.setText(musicList.get(position).getSong());
//                            SongArtist.setText(musicList.get(position).getArtist());
//                            SeekBar.setMax(musicService.getDuration()/1000);
//
//                            PlayerActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(musicService!=null){
//                                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
//                                        SeekBar.setProgress(mCurrentPosition);
//                                    }
//                                    handler.postDelayed(this,1000);
//                                }
//                            });
//                            Play.setImageResource(R.drawable.ic_baseline_pause_24);
//                            musicService.start();
//                        }else{
//                            musicService.stop();
//                            musicService.release();
//                            position=((position-1)<0 ? (musicList.size()-1) : (position-1));
//                            uri = Uri.parse(getIntent().getStringExtra("url"));
//                            musicService.createMediaPlayer(position);
//                            SongTitle.setText(musicList.get(position).getSong());
//                            SongArtist.setText(musicList.get(position).getArtist());
//                            SeekBar.setMax(musicService.getDuration()/1000);
//
//                            PlayerActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(musicService!=null){
//                                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
//                                        SeekBar.setProgress(mCurrentPosition);
//                                    }
//                                    handler.postDelayed(this,1000);
//                                }
//                            });
//                            Play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
////                            musicService.start();
//                        }
//                    }
                });
            }
        };
        prevThread.start();
    }
}