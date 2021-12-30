package com.learn.mymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LinearLayout l1,l2;
    TextView tv;

    Animation DownToUp, Fade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        tv = (TextView) findViewById(R.id.tag);
        DownToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        Fade = AnimationUtils.loadAnimation(this,R.anim.fade);
        l2.setAnimation(DownToUp);
        tv.setAnimation(Fade);

        final Intent i = new Intent(MainActivity.this, LibraryActivity.class);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        thread.start();
    }
}