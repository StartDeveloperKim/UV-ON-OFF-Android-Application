package com.example.hello.destroy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hello.mqtt.MqttService;
import com.example.hello.mqtt.MqttUtil;

public class ForcedTerminationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Error", "onTaskRemoved - 강제 종료" + rootIntent);

        startService(new Intent(this, MqttService.class));
//        stopSelf();
    }
}
