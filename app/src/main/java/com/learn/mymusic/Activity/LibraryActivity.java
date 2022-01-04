package com.learn.mymusic.Activity;

import static com.learn.mymusic.Activity.PlayerActivity.SHOW_MINI_PLAYER;
import static com.learn.mymusic.MusicAdapter.songList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.learn.mymusic.Model.SongModel;
import com.learn.mymusic.MusicAdapter;
import com.learn.mymusic.MusicEvent;
import com.learn.mymusic.R;
import com.learn.mymusic.Retrofit.ApiClient;
import com.learn.mymusic.Retrofit.ApiInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends AppCompatActivity {

    private MusicAdapter myAdapter;
    private RecyclerView recyclerView;
    private MusicEvent event;
    private FrameLayout fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_library);
        getSupportActionBar().hide();
        fragment = (FrameLayout) findViewById(R.id.frag_mini_player);
        ApiInterface service = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<SongModel>> call = service.getStudio();
        event = new MusicEvent();

        call.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<SongModel>> call, Throwable t) {
                Log.e("Library Act", t.getMessage() );
            }
        });

    }

    private void loadDataList(List<SongModel> songsList) {
        recyclerView = findViewById(R.id.myRecycler);
        myAdapter = new MusicAdapter(songsList,getBaseContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LibraryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
//        Log.e("SOMETHING",songsList.get(0).getArtist());
//        event.setArtistName(songsList.get(0).getArtist());
//        event.setSongTitle(songsList.get(0).getSong());
//        EventBus.getDefault().post(event);
    }
    private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SHOW_MINI_PLAYER){
            fragment.setVisibility(View.VISIBLE);
        }
    }
}