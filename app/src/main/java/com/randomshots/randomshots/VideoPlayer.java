package com.randomshots.randomshots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoPlayer extends AppCompatActivity {
    String videoId;
    String videoTitle;
    String videoDescription;
//    YouTubePlayerView youTubePlayerView;
    TextView videoTitleTextView;
    TextView videoDescriptionTextView;
    boolean isFullscreen=false;
    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer youTubePlayer;
//    YouTubePlayer youTubePlayerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent=getIntent();
        videoId=intent.getStringExtra("videoId");
        videoTitle=intent.getStringExtra("videoTitle");
        videoDescription=intent.getStringExtra("videoDescription");
        videoTitleTextView=findViewById(R.id.videoTitle);
        videoDescriptionTextView=findViewById(R.id.videoDescription);

        videoTitleTextView.append(videoTitle);
        videoDescriptionTextView.append(videoDescription);
        initializeYoutubePlayer();

        AudienceNetworkAds.initialize(this);
        AdView facebookAdView=new AdView(this,"737206096852710_737209603519026", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer=(LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(facebookAdView);
        facebookAdView.loadAd();


//        youTubePlayerView=findViewById(R.id.youtubePlayer);
//        youTubePlayerView.initialize(YouTubeConfig.getApiKey(), new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//
//                youTubePlayerMain=youTubePlayer;
//                youTubePlayer.loadVideo(videoId);
//                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
//                    @Override
//                    public void onFullscreen(boolean b) {
//                        isFullscreen=b;
//                    }
//                });
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
    }

    private void initializeYoutubePlayer() {
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtubePlayer);
        youTubePlayerFragment.initialize(YouTubeConfig.getApiKey(), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
                if(!b) {
                    youTubePlayer=player;
                    youTubePlayer.loadVideo(videoId);
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            isFullscreen=b;
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }
    public void moreOptions(View view) {
        Intent intent = new Intent(this, MoreOptions.class);
        startActivity(intent);
    }
    public void close(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if(isFullscreen) {
            youTubePlayer.setFullscreen(false);
        }
        else {
            finish();
        }
    }
}