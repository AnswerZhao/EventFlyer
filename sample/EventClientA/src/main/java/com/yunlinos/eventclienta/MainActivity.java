package com.yunlinos.eventclienta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yunlinos.eventflyer.EventFlyer;
import com.yunlinos.eventmodel.OnControlEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventFlyer.getDefault().bindRemoteServer();
        EventFlyer.getDefault().post(new OnControlEvent("RemoteEvent"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventFlyer.getDefault().unBindRemoteServer();
    }
}