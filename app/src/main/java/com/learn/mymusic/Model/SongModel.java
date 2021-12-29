package com.learn.mymusic.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongModel {
    @Expose
    @SerializedName("song")
    String song;

    @Expose
    @SerializedName("url")
    String url;

    @Expose
    @SerializedName("artist")
    String artist;

    @Expose
    @SerializedName("cover_image")
    String cover_image;

    public SongModel(String song, String url, String artist, String cover_image) {
        this.song = song;
        this.url = url;
        this.artist = artist;
        this.cover_image = cover_image;
    }

    public String getSong() {
        return song;
    }

    public String getUrl() {
        return url;
    }

    public String getArtist() {
        return artist;
    }

    public String getCover_image() {
        return cover_image;
    }
}
