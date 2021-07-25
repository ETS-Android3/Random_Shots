package com.randomshots.randomshots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.LottieAnimationView;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkActivity;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity{


    ImageView headerLogo;
    Animation rotate;
    Animation rotateForward;
    Animation rotateBackward;
    Animation showSlide;
    Animation hideSlide;
    TextView headerName;
    ImageButton plusIcon;
    LinearLayout plusMenu;
    Button videoButton;
    Button aboutButton;
    ImageView logoImageView;
    TextView channelNameTextView;
    TextView subscribersCount;
    TextView channelDescriptionTextView;
    RelativeLayout ratingRelativeLayout;
    ImageButton ratingClose;
    Button rateButton;
    RelativeLayout dynamicContent;
    RelativeLayout bottomRelativeLayout;
    LottieAnimationView loading;
    ImageButton toggleButton;
    MediaPlayer music=new MediaPlayer();
    Intent intent;
    int count;
    boolean isInternetAvailable=true;
    boolean isRatingOverlayed=false;
    SharedPreferences sharedPreferences;
    boolean isDoubleTap=false;
    boolean isSubsribersCountAlreadyAnimated=false;
    boolean isInterstitalAdDisplayed=false;

    String channelName;
    String channelDescription;
    public static String senderEmail="xxxxxxxxxxx";
    public static String senderPassword="xxxxxxxxxxx";
    public static String receiverEmail="xxxxxxxxxxx";

    com.facebook.ads.AdView facebookAdView;
    com.facebook.ads.InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_main);

        AudienceNetworkAds.initialize(this);

        sharedPreferences = this.getSharedPreferences("com.randomshots.randomshots", MODE_PRIVATE);
        count = sharedPreferences.getInt("count", -1);


        int resourceId = getResources().getIdentifier("tap", "raw", "com.randomshots.randomshots");
        music = MediaPlayer.create(this, resourceId);

//        FetchChannelLogo task=new FetchChannelLogo();
//        task.execute("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=UC3SPZgyqS_YHzBcbUpeVNog&key=AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY");
//
//        FetchChannelSubscribers fetchChannelSubscribers=new FetchChannelSubscribers();
//        fetchChannelSubscribers.execute("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC3SPZgyqS_YHzBcbUpeVNog&key=AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY");

        plusIcon = findViewById(R.id.plusMenu);
        plusMenu = findViewById(R.id.plusMenuLinearLayout);
        headerName = (TextView) findViewById(R.id.headerName);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        showSlide = AnimationUtils.loadAnimation(this, R.anim.slide_show);
        hideSlide = AnimationUtils.loadAnimation(this, R.anim.slide_hide);
        headerLogo = findViewById(R.id.headerLogo);
        videoButton = findViewById(R.id.videosButton);
        aboutButton = findViewById(R.id.aboutMeButton);
        logoImageView = findViewById(R.id.logoImageView);
        channelNameTextView = findViewById(R.id.channelName);
        subscribersCount = findViewById(R.id.subscribersCount);
        channelDescriptionTextView = findViewById(R.id.channelDescription);
        ratingRelativeLayout = findViewById(R.id.ratingRelativeLayout);
        bottomRelativeLayout = findViewById(R.id.bottomRelativeLayout);
        ratingClose = findViewById(R.id.ratingClose);
        rateButton = findViewById(R.id.rateButton);
        dynamicContent = findViewById(R.id.dynamicContentMain);
        loading = findViewById(R.id.loading);
        toggleButton=findViewById(R.id.toggleButton);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                    }
                });

            facebookAdView=new com.facebook.ads.AdView(this,"737206096852710_737209603519026", AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer=(LinearLayout) findViewById(R.id.banner_container);
            adContainer.addView(facebookAdView);

            interstitialAd=new InterstitialAd(this,"737206096852710_737320656841254");
            interstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {
                        interstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });

        if(SplashScreen.isDarkModeOn) {
            toggleButton.setImageResource(R.drawable.ic_toggle_on);
        }
        else {
            toggleButton.setImageResource(R.drawable.ic_toggle_off);
        }

//        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        switch (currentNightMode) {
//            case Configuration.UI_MODE_NIGHT_NO:
//                // Night mode is not active, we're using the light theme
//                Toast.makeText(this, "Night mode off", Toast.LENGTH_SHORT).show();
//                break;
//            case Configuration.UI_MODE_NIGHT_YES:
//                // Night mode is active, we're using dark theme
//                Toast.makeText(this, "Night mode on", Toast.LENGTH_SHORT).show();
//                break;
//        }

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                if(isConnected) {
                    isInternetAvailable=true;
                    bottomRelativeLayout.setVisibility(View.GONE);
                    FetchChannelLogo task=new FetchChannelLogo();
                    task.execute("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=UC3SPZgyqS_YHzBcbUpeVNog&key=AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY");

                    FetchChannelSubscribers fetchChannelSubscribers=new FetchChannelSubscribers();
                    fetchChannelSubscribers.execute("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC3SPZgyqS_YHzBcbUpeVNog&key=AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY");
                    facebookAdView.loadAd();
                }
                else {
                    getLayoutInflater().inflate(R.layout.internet_connection, bottomRelativeLayout, true);
                    isInternetAvailable=false;
                    bottomRelativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        if (count==-1) {
            sharedPreferences.edit().putInt("count",0).apply();
        }
        else {
            sharedPreferences.edit().putInt("count",count+1).apply();
            System.out.println("Count is:"+count);
            if(count%10==0) {
                ratingRelativeLayout.setVisibility(View.VISIBLE);
                isRatingOverlayed=true;
            }
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRatingOverlayed) {
                    if (SplashScreen.isDarkModeOn) {
                        SplashScreen.sharedPreferences.edit().putBoolean("darkMode", false).apply();
                        SplashScreen.isDarkModeOn = false;
                        toggleButton.setImageResource(R.drawable.ic_toggle_off);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        SplashScreen.sharedPreferences.edit().putBoolean("darkMode", true).apply();
                        SplashScreen.isDarkModeOn = true;
                        toggleButton.setImageResource(R.drawable.ic_toggle_on);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                }
            }
        });

        logoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInterstitalAdDisplayed) {
                    interstitialAd.loadAd();
                    isInterstitalAdDisplayed=true;
                }
            }
        });

//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if(checked) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                }
//                else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                }
//            }
//        });

        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRatingOverlayed) {
                    headerLogo.startAnimation(rotate);
                }
            }
        });
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRatingOverlayed) {
                    MediaPlayer mediaPlayer;
                    int resourceId = getResources().getIdentifier("pooltap", "raw", "com.randomshots.randomshots");
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resourceId);
                    mediaPlayer.start();

                    if (plusMenu.getVisibility() == View.VISIBLE) {
                        plusIcon.startAnimation(rotateBackward);
                        plusMenu.startAnimation(hideSlide);
                        plusMenu.setVisibility(View.INVISIBLE);
                    } else {
                        plusIcon.startAnimation(rotateForward);
                        plusMenu.setVisibility(View.VISIBLE);
                        plusMenu.startAnimation(showSlide);
                    }
                }
            }
        });
        headerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRatingOverlayed) {
                    Random random = new Random();
                    int randomNumber = random.nextInt(0xffffff + 1);
                    String colorCode = String.format("#%06x", randomNumber);
                    headerName.setTextColor(Color.parseColor(colorCode));
                }
            }
        });
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music.start();
                if(isInternetAvailable) {
                    intent = new Intent(MainActivity.this, Videos.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Please turn on your internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music.start();
                intent=new Intent(MainActivity.this,About_us.class);
                startActivity(intent);
            }
        });
        ratingClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRatingOverlayed=false;
                ratingRelativeLayout.setVisibility(View.GONE);
            }
        });
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRatingOverlayed=false;
                ratingRelativeLayout.setVisibility(View.GONE);
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.randomshots.randomshots"));
                startActivity(i);
            }
        });

        FetchChannelLogo task=new FetchChannelLogo();
        task.execute("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=UC3SPZgyqS_YHzBcbUpeVNog&key=AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY");

        FetchChannelSubscribers fetchChannelSubscribers=new FetchChannelSubscribers();
        fetchChannelSubscribers.execute("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC3SPZgyqS_YHzBcbUpeVNog&key=AIzaSyCEBc1sRH3d5kBTgPiWXm985dP0kXvOAiY");

    }


    public void openChannel(View view) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.youtube.com/channel/UC3SPZgyqS_YHzBcbUpeVNog"));
        startActivity(i);
    }

    class FetchChannelLogo extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                JSONObject jsonObject=new JSONObject(result);
                String items=jsonObject.getString("items");
                JSONArray jsonArray=new JSONArray(items);
                String logoStr=null;
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonPart = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high");
                    logoStr = jsonPart.getString("url");
                    JSONObject jsonPart2=jsonArray.getJSONObject(i).getJSONObject("snippet");
                    channelName=jsonPart2.getString("title");
                    channelDescription=jsonPart2.getString("description");
                }
                System.out.println(logoStr);
                URL url1=new URL(logoStr);
                HttpURLConnection connection=(HttpURLConnection) url1.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                //Below line converts data that has been downloaded to an image
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            try {
                if(bitmap!=null) {
                    logoImageView.setImageBitmap(bitmap);
                    channelNameTextView.setText("Channel Name: " + channelName);
                    channelDescriptionTextView.setText("Description: " + channelDescription);
                    loading.setVisibility(View.GONE);
                    dynamicContent.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class FetchChannelSubscribers extends AsyncTask<String,Void,Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                JSONObject jsonObject = new JSONObject(result);
                String items = jsonObject.getString("items");
                JSONArray jsonArray = new JSONArray(items);
                Integer noOfSubscribers = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonPart = jsonArray.getJSONObject(i).getJSONObject("statistics");
                    noOfSubscribers = Integer.parseInt(jsonPart.getString("subscriberCount"));
                }
                return noOfSubscribers;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            try {
                if (!isSubsribersCountAlreadyAnimated) {
                    isSubsribersCountAlreadyAnimated = true;
                    startCountAnimation(subscribersCount, 0, integer, 2000);
                }
                else {
                    subscribersCount.setText(integer.toString());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void moreOptions(View view) {
        if (!isRatingOverlayed) {
            intent = new Intent(this, MoreOptions.class);
            startActivity(intent);
        }
    }

    public void startCountAnimation(final TextView textView, int initialNumber, int finalNumber, int duration) {
        ValueAnimator animator=ValueAnimator.ofInt(initialNumber,finalNumber);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                textView.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        if(!isDoubleTap) {
            isDoubleTap=true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDoubleTap=false;
                }
            },2000);
        }
        else {
            this.finishAffinity();
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
        }
    }
}