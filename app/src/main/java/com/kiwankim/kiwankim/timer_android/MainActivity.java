package com.kiwankim.kiwankim.timer_android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    boolean isFirst = true;
    boolean isOn = false;
    boolean isAlarmed = false;
    Timer timer;
    TimerTask tt;

    NumberPicker hourpick;
    NumberPicker minutepick;
    NumberPicker secpick;

    Button startbtn;
    Button stopbtn;
    Button resetbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        hourpick = findViewById(R.id.hourpick);
        minutepick = findViewById(R.id.minutepick);
        secpick = findViewById(R.id.secpick);

        hourpick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutepick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        secpick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        hourpick.setMinValue(0);
        hourpick.setMaxValue(24);
        minutepick.setMinValue(0);
        minutepick.setMaxValue(59);
        secpick.setMinValue(0);
        secpick.setMaxValue(59);
        timer = new Timer();

        startbtn = findViewById(R.id.startbtn);
        stopbtn = findViewById(R.id.stopbtn);
        resetbtn = findViewById(R.id.resetbtn);

        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                // 원래 하려던 동작 (UI변경 작업 등)
                if (secpick.getValue() != 0)
                    secpick.setValue(secpick.getValue() - 1);
                else {
                    if (minutepick.getValue() != 0) {
                        minutepick.setValue(minutepick.getValue() - 1);
                        secpick.setValue(59);
                    } else {
                        if (hourpick.getValue() != 0) {
                            hourpick.setValue(hourpick.getValue() - 1);
                            minutepick.setValue(59);
                            secpick.setValue(59);
                        }
                    }
                }
            }
        };
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAlarmed = false;
                isFirst=false;
                if(!isOn) {
                    tt = new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                            if(!isAlarmed && hourpick.getValue()==0 && minutepick.getValue()==0 && secpick.getValue()==0) {
                                createNoti();
                                tt.cancel();
                                isOn=false;
                            }
                        }
                    };
                    timer.schedule(tt, 0, 1000);
                    isOn=true;
                }

            }
        });
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFirst)
                    tt.cancel();
                isOn=false;

            }
        });
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFirst)
                    tt.cancel();
                isOn=false;
                hourpick.setValue(0);
                minutepick.setValue(0);
                secpick.setValue(0);
            }
        });
    }

    private void createNoti(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher_simple_round);
        builder.setContentTitle("Alarm!");

        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(new NotificationChannel("default","default",NotificationManager.IMPORTANCE_HIGH));

        }
        notificationManager.notify(1,builder.build());
        isAlarmed=true;

    }



}