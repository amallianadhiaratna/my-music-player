package com.learn.mymusic.Retrofit;

import com.learn.mymusic.Model.SongModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("list")
    Call<List<SongModel>> getStudio();
}
