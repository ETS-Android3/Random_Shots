package com.randomshots.randomshots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Videos extends AppCompatActivity implements VideosListRecyclerAdapter.OnItemListener {

    ImageView backBtn;
    String api="AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY";
    String channelId="UC3SPZgyqS_YHzBcbUpeVNog";
    public static ArrayList<String> videoThumbnail=new ArrayList<>();
    public static ArrayList<String> videoId=new ArrayList<>();
    public static ArrayList<String> videoTitle=new ArrayList<>();
    public static ArrayList<String> videoDescription=new ArrayList<>();
    private static boolean isNextPageAvailable;
    private RecyclerView recyclerView;
    VideosListRecyclerAdapter videosListRecyclerAdapter;
    RelativeLayout loadingRelative;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        loadingRelative=findViewById(R.id.loadingRelative);

        videoThumbnail.clear();
        videoId.clear();
        videoTitle.clear();
        videoDescription.clear();

        AudienceNetworkAds.initialize(this);
        AdView facebookAdView=new AdView(this,"737206096852710_737209603519026", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer=(LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(facebookAdView);
        facebookAdView.loadAd();

        backBtn=findViewById(R.id.headerBack);
        FetchVideos fetchVideos=new FetchVideos();
        fetchVideos.execute("https://www.googleapis.com/youtube/v3/activities?part=snippet,contentDetails&channelId="+channelId+"&key="+api);

        recyclerView=findViewById(R.id.videosRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(Videos.this,1));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);
        videosListRecyclerAdapter=new VideosListRecyclerAdapter(Videos.this,Videos.this);
        recyclerView.setAdapter(videosListRecyclerAdapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoThumbnail.clear();
        videoId.clear();
        videoTitle.clear();
        videoDescription.clear();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(Videos.this,VideoPlayer.class);
        intent.putExtra("videoId",videoId.get(position));
        intent.putExtra("videoTitle",videoTitle.get(position));
        intent.putExtra("videoDescription",videoDescription.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {

    }

    public class FetchVideos extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(strings[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1) {
                    char current=(char) data;
                    result+=current;
                    data=reader.read();
                }

                try {
                    JSONObject jsonObject=new JSONObject(result);
                    String items=jsonObject.getString("items");
                    JSONArray jsonArray=new JSONArray(items);
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonPart=jsonArray.getJSONObject(i).getJSONObject("contentDetails").getJSONObject("upload");
                        videoId.add(jsonPart.getString("videoId"));
                        JSONObject jsonPart1=jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high");
                        videoThumbnail.add(jsonPart1.getString("url"));
                        JSONObject jsonPart2=jsonArray.getJSONObject(i).getJSONObject("snippet");
                        videoTitle.add(jsonPart2.getString("title"));
                        videoDescription.add(jsonPart2.getString("description"));
                    }
                    if(jsonObject.has("nextPageToken")) {
                        isNextPageAvailable=true;
                        return jsonObject.getString("nextPageToken");
                    }
                    else {
                        isNextPageAvailable=false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("isNextPage:"+isNextPageAvailable);
            if(isNextPageAvailable) {
                FetchVideos fetchVideos=new FetchVideos();
                fetchVideos.execute("https://www.googleapis.com/youtube/v3/activities?part=snippet,contentDetails&channelId="+channelId+"&key="+api+"&pageToken="+s);
            }
            else {
                videosListRecyclerAdapter.notifyDataSetChanged();
                loadingRelative.setVisibility(View.GONE);
                System.out.println(videoId.size());
            }
        }
    }

    public void moreOptions(View view) {
            Intent intent = new Intent(this, MoreOptions.class);
            startActivity(intent);
    }

}