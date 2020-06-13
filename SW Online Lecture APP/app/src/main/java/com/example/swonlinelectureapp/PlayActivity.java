package com.example.swonlinelectureapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView ytpv;
    private YouTubePlayer ytp;
    final String serverKey="AIzaSyAOUuKJ9HOxGT7pCqvsj5RMPbDE6k9gRo0";
    //final String serverKey="AIzaSyBg-eEaLFpQN1scxt5HWA1vADzTKyKE6B0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ytpv = (YouTubePlayerView) findViewById(R.id.youtubeplayer);
        ytpv.initialize(serverKey, this);
    }

    @Override
    public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
        Toast.makeText(this, "Error Detected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasrestored) {
        ytp = player;
        Intent gt =getIntent();
        ytp.loadVideo(gt.getStringExtra("id"));
    }

    //뒤로가기 버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "보관함으로 이동합니다", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

}