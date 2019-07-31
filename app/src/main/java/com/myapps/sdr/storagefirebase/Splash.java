package com.myapps.sdr.storagefirebase;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().hide();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        logolaunch logs = new logolaunch();
        logs.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(Splash.this, LoginAct.class);
                startActivity(splashIntent);
                finish();
            }
        },2000);
    }
    private class logolaunch extends Thread{
        public void run(){
            try{
                sleep(1000);
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
    }
    }
}
