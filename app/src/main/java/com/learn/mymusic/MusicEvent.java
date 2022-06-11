package com.learn.mymusic;

public class MusicEvent {
    private static boolean isPlay;
    private static String songTitle;
    private static String artistName;
    private static String coverImage;
    private int playPauseBtn;

    public int getPlayPauseBtn() {
        return playPauseBtn;
    }

    public void setPlayPauseBtn(int playPauseBtn) {
        this.playPauseBtn = playPauseBtn;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        MusicEvent.songTitle = songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        MusicEvent.artistName = artistName;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        MusicEvent.coverImage = coverImage;
    }
}
