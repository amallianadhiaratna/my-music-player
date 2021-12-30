package com.learn.mymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.learn.mymusic.Model.SongModel;
import com.learn.mymusic.Retrofit.ApiClient;
import com.learn.mymusic.Retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends AppCompatActivity {

    private MusicAdapter myAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_library);
        getSupportActionBar().hide();

        ApiInterface service = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<SongModel>> call = service.getStudio();

        call.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<SongModel>> call, Throwable t) {
                Toast.makeText(LibraryActivity.this, ""+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadDataList(List<SongModel> songsList) {
        recyclerView = findViewById(R.id.myRecycler);
        myAdapter = new MusicAdapter(songsList,getBaseContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LibraryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
    }
    private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}