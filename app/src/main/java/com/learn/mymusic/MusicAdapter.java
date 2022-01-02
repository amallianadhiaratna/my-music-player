package com.learn.mymusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.mymusic.Activity.PlayerActivity;
import com.learn.mymusic.Model.SongModel;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {

    public static List<SongModel> songList;
    private Context mContext;
    public MusicAdapter(List<SongModel> songList, Context context) {
        this.songList = songList;
        this.mContext = context;
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
        if(songList.get(position).getCover_image()!=null){
            Glide.with(mContext).asBitmap()
                    .load(songList.get(position).getCover_image())
                    .into(holder.coverImage);
        }else{
            Glide.with(mContext).asBitmap()
                    .load(R.drawable.default_pic)
                    .into(holder.coverImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), PlayerActivity.class);
                i.putExtra("song",holder.nameTextView.getText().toString());
                i.putExtra("url",holder.urlTextView.getText().toString());
                i.putExtra("artist",holder.artistTextView.getText().toString());
                i.putExtra("cover_image",holder.coverImageTextView.getText().toString());

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
        ImageView coverImage;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.title_song);
            artistTextView = itemView.findViewById(R.id.artist);
            urlTextView = itemView.findViewById(R.id.url);
            coverImageTextView = itemView.findViewById(R.id.cover_image);
            coverImage = (ImageView) itemView.findViewById(R.id.music_img);
        }
    }

}
