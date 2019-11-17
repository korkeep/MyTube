package com.example.swonlinelectureapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IntroActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(3000); // 3초 인트로 화면 보여주기
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // next Activity 기재.
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
