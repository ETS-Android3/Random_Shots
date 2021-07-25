package com.randomshots.randomshots;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class VideosListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public OnItemListener mOnItemListener;
    public RecyclerView.ViewHolder mholder;

    VideosListRecyclerAdapter(Context mContext,OnItemListener onItemListener){
        this.mContext = mContext;
        this.mOnItemListener=onItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videolist,parent,false);
        return new VideosListRecyclerAdapter.FileLayoutHolder(view,mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        System.out.println(Videos.videoThumbnail.get(position));
        try {
            mholder=(FileLayoutHolder) holder;
            ((FileLayoutHolder) holder).thumbnailImageView.setColorFilter(Color.argb(100,0,0,0));
            ((FileLayoutHolder) holder).textView.setText(Videos.videoTitle.get(position));

            final Bitmap[] bitmap = new Bitmap[1];
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("In thread");
                    ImageLoader imageLoader=new ImageLoader();
                    try {
                        bitmap[0] =(Bitmap)imageLoader.execute(Videos.videoThumbnail.get(position)).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
            System.out.println("After thread");
            Glide.with(mContext)
                    .load(bitmap[0]).thumbnail(0.1f).into(((FileLayoutHolder) holder).thumbnailImageView);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return Videos.videoId.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        TextView textView;
        OnItemListener onItemListener;
        ImageView thumbnailImageView;

        public FileLayoutHolder(@NonNull View itemView,OnItemListener onItemListener) {
            super(itemView);

            this.onItemListener=onItemListener;
            textView=itemView.findViewById(R.id.title);
            thumbnailImageView=itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onItemListener.onItemLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnItemListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
    public class ImageLoader extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                //Below line converts data that has been downloaded to an image
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            Glide.with(mContext)
//                    .load(bitmap).thumbnail(0.1f).into(((FileLayoutHolder) mholder).thumbnailImageView);
        }
    }
}
