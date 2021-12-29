package com.learn.mymusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.mymusic.Model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {

    static List<SongModel> songList;
    ArrayList<String> arrayListUrl = new ArrayList<>();
    ArrayList<String> arrayListSong = new ArrayList<>();
    ArrayList<String> arrayListArtist = new ArrayList<>();
    ArrayList<String> arrayListImage = new ArrayList<>();
    public MyAdapter(List<SongModel> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_layout, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nameTextView.setText(songList.get(position).getSong());
        holder.artistTextView.setText(songList.get(position).getArtist());
        holder.urlTextView.setText(songList.get(position).getUrl());
        holder.coverImageTextView.setText(songList.get(position).getCover_image());

        if(!(arrayListUrl.contains(songList.get(position).getUrl())))
            arrayListUrl.add(songList.get(position).getUrl());
        if(!(arrayListArtist.contains(songList.get(position).getArtist())))
            arrayListArtist.add(songList.get(position).getArtist());
        if(!(arrayListSong.contains(songList.get(position).getSong())))
            arrayListSong.add(songList.get(position).getSong());
        if(!(arrayListImage.contains(songList.get(position).getCover_image())))
            arrayListImage.add(songList.get(position).getCover_image());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), PlayerActivity.class);
//                i.putExtra("position",holder.getBindingAdapterPosition());
                i.putExtra("song",holder.nameTextView.getText().toString());
                i.putExtra("url",holder.urlTextView.getText().toString());
                i.putExtra("artist",holder.artistTextView.getText().toString());
                i.putExtra("cover_image",holder.coverImageTextView.getText().toString());

                i.putExtra("arrayListUrl",arrayListUrl);
                i.putExtra("arrayListArtist",arrayListArtist);
                i.putExtra("arrayListSong",arrayListSong);
                i.putExtra("arrayListImage",arrayListImage);
                i.putExtra("position",String.valueOf(position));
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView, artistTextView, urlTextView, coverImageTextView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.title_song);
            artistTextView = itemView.findViewById(R.id.artist);
            urlTextView = itemView.findViewById(R.id.url);
            coverImageTextView = itemView.findViewById(R.id.cover_image);

        }
    }
}
