package com.speedtableview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SpeedTableView speedtableview;

    private Handler handler;
    int whattag = 666;
    int i = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        speedtableview = (SpeedTableView) findViewById(R.id.speedtableview);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == whattag) {
                    if (i < 300) {
                        i = i + 1;
                        speedtableview.setDelta(i);
                        handler.sendEmptyMessageDelayed(whattag, 100);
                    }

                }
                super.handleMessage(msg);
            }
        };
        handler.sendEmptyMessageDelayed(whattag, 1000);


    }
}
