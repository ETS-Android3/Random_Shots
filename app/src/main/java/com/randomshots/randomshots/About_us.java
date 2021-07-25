package com.randomshots.randomshots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class About_us extends AppCompatActivity {

    ImageView profilePic;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        profilePic=findViewById(R.id.profilePic);
        lottieAnimationView=findViewById(R.id.loading);

        AudienceNetworkAds.initialize(this);
        AdView facebookAdView=new AdView(this,"737206096852710_737209603519026", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer=(LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(facebookAdView);
        facebookAdView.loadAd();

        FetchProfilePic fetchProfilePic=new FetchProfilePic();
        fetchProfilePic.execute("https://www.instagram.com/nishantbatra360/?__a=1");

    }
    class FetchProfilePic extends AsyncTask<String,Void, Bitmap> {

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
                String profilePicStr=jsonObject.getJSONObject("graphql").getJSONObject("user").getString("profile_pic_url_hd");
                System.out.println("Profile pic url is:"+profilePicStr);
                URL url1=new URL(profilePicStr);
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
            lottieAnimationView.setVisibility(View.GONE);
            profilePic.setImageBitmap(bitmap);
        }
    }

    public void openInstagram(View view) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.instagram.com/nishantbatra360/"));
        startActivity(i);
    }

    public void openLinkedin(View view) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.linkedin.com/in/nishant-batra-2b3918171"));
        startActivity(i);
    }

    public void openMail(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"nishantbatra360@gmail.com"});
        intent.setType("message/rfc822");
        startActivity(
                Intent
                        .createChooser(intent,
                                "Choose an Email client :"));
    }

    public void openFreelancer(View view) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.freelancer.in/u/nishantbatra360"));
        startActivity(i);
    }

    public void close(View view) {
        finish();
    }
}