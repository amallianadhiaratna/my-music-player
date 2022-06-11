package com.learn.mymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Random;

public class NowPlayingFragment extends Fragment implements ServiceConnection {

    ImageView nextBtnMinPlayer, iconMiniPlayer;
    TextView artistMiniPlayer, songMiniPlayer;
    boolean isPlaying;
    FloatingActionButton playPauseBtn;
    View view;
    MusicService musicService;
    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(MusicEvent event){
        Log.d("MusicEvent","Get event !");
        artistMiniPlayer.setText(event.getArtistName());
        songMiniPlayer.setText(event.getSongTitle());
        isPlaying = event.isPlay();
        playPauseBtn.setImageResource(event.getPlayPauseBtn());
        if(getActivity()!=null) {
            Glide.with(getContext())
                    .load(event.getCoverImage())
                    .into(iconMiniPlayer);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        artistMiniPlayer = view.findViewById(R.id.artist_mini_player);
        songMiniPlayer = view.findViewById(R.id.title_song_mini_player);
        nextBtnMinPlayer = view.findViewById(R.id.skip_next_mini_player);
        iconMiniPlayer = view.findViewById(R.id.icon_mini_player);
        playPauseBtn = view.findViewById(R.id.play_pause_mini_player);
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService!=null){
                    musicService.playPauseButtonClick();
                    if(isPlaying){
                        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_24);
                    } else {
                        playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_circle_24);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder myBinder =(MusicService.MyBinder) service;
        musicService = myBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), MusicService.class);
        if(getContext()!=null){
            getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext()!=null){
            getContext().unbindService(this);
        }
    }
}