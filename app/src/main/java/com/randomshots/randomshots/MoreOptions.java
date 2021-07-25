package com.randomshots.randomshots;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;

import java.io.File;

public class MoreOptions extends AppCompatActivity {

    LinearLayout shareAppLinearLayout;
    LinearLayout feedbackLinearLayout;
    EditText feedbackEditText;
    Button feedbackButton;
    LinearLayout bugLinearLayout;
    LinearLayout feedbackFormLinearLayout;
    LinearLayout bugFormLinearLayout;
    EditText bugEditText;
    Button bugFileButton;
    Button bugButton;
    TextView bugFileLabel;
    File bugFile=null;

    String subject;
    String body;

    Animation slideDown;
    Animation slideUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_options);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},1);
        }

        slideDown= AnimationUtils.loadAnimation(this,R.anim.slidedown);
        slideUp=AnimationUtils.loadAnimation(this,R.anim.slideup);

        shareAppLinearLayout=findViewById(R.id.shareAppLinearLayout);
        feedbackLinearLayout=findViewById(R.id.feedbackLinearLayout);
        feedbackEditText=findViewById(R.id.feedbackEditText);
        feedbackButton=findViewById(R.id.feedbackButton);
        bugLinearLayout=findViewById(R.id.bugLinearLayout);
        feedbackFormLinearLayout=findViewById(R.id.feedbackFormLinearLayout);
        bugFormLinearLayout=findViewById(R.id.bugFormLinearLayout);
        bugEditText=findViewById(R.id.bugEditText);
        bugFileButton=findViewById(R.id.chooseFileButton);
        bugFileLabel=findViewById(R.id.fileName);
        bugButton=findViewById(R.id.bugButton);

        feedbackEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        feedbackEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        bugEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        bugEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        AudienceNetworkAds.initialize(this);
        AdView facebookAdView=new AdView(this,"737206096852710_737209603519026", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer=(LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(facebookAdView);
        facebookAdView.loadAd();

        shareAppLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.randomshots.randomshots");
                startActivity(Intent.createChooser(i,"Share with..."));
            }
        });

        feedbackLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedbackFormLinearLayout.getVisibility()!=View.VISIBLE) {
                    feedbackFormLinearLayout.setVisibility(View.VISIBLE);
                }
                else {
                    feedbackFormLinearLayout.setVisibility(View.GONE);
                }
            }
        });


        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(feedbackEditText.getWindowToken(), 0);
                if (feedbackEditText.getText().toString().trim().length()==0) {
                    Toast.makeText(MoreOptions.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    subject="Feedback";
                    body=feedbackEditText.getText().toString();
                    final boolean[] isSent = new boolean[1];
                    final ProgressDialog dialog = new ProgressDialog(MoreOptions.this);
                    dialog.setTitle("Submitting Feedback");
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    Thread sender = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GmailSender sender = new GmailSender(MainActivity.senderEmail, MainActivity.senderPassword);
                                isSent[0] =sender.sendMail(subject,
                                        body,
                                        MainActivity.senderEmail,
                                        MainActivity.receiverEmail,
                                        null);
                                System.out.println("isSent:"+isSent[0]);
                                dialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isSent[0]) {
                                            Toast.makeText(MoreOptions.this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MoreOptions.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("mylog", "Error: " + e.getMessage());
                            }
                        }
                    });
                    sender.start();
                }
            }
        });

        bugFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,1);
            }
        });
        bugLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bugFormLinearLayout.getVisibility()!=View.VISIBLE) {
                    bugFormLinearLayout.setVisibility(View.VISIBLE);
                }
                else {
                    bugFormLinearLayout.setVisibility(View.GONE);
                }
            }
        });
        bugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(bugEditText.getWindowToken(), 0);
                if(bugEditText.getText().toString().trim().length()==0) {
                    Toast.makeText(MoreOptions.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    subject="Feedback";
                    body=bugEditText.getText().toString();
                    final boolean[] isSent = new boolean[1];
                    final ProgressDialog dialog = new ProgressDialog(MoreOptions.this);
                    dialog.setTitle("Submitting Bug");
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    Thread sender = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GmailSender sender = new GmailSender(MainActivity.senderEmail, MainActivity.senderPassword);
                                isSent[0] =sender.sendMail(subject,
                                        body,
                                        MainActivity.senderEmail,
                                        MainActivity.receiverEmail,
                                        bugFile);
                                dialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isSent[0]) {
                                            Toast.makeText(MoreOptions.this, "Bug submitted", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MoreOptions.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("mylog", "Error: " + e.getMessage());
                            }
                        }
                    });
                    sender.start();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK) {
            Uri Urifile=data.getData();
            System.out.println("URI is:"+Urifile);
            String path=FileUtils.getPath(this,Urifile);
            System.out.println("Paths is:"+path);
            File file=new File(path);
            bugFileLabel.setText(file.getName());
            System.out.println("File Size:"+(double)file.length()/(1024*1024)+"MB");
            double fileSize=(double)file.length()/(1024*1024);
            if(fileSize<=20.00) {
                bugFile=file;
            }
            else{
                Toast.makeText(this, "File should be less than 20MB", Toast.LENGTH_SHORT).show();
            }
        }
    }
}