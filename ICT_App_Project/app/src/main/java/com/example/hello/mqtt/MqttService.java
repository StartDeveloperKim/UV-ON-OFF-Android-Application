package com.example.hello.mqtt;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.hello.MainActivity;
import com.example.hello.notification.MqttNotification;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.util.Set;

public class MqttService extends Service {

    private MqttAndroidClient client;
    private MqttNotification mqttNotification;

    private MqttUtil mqttUtil;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MQTT 백그라운드서비스", "연결을 시도합니다.");

        mqttUtil = MqttUtil.getMqttUtilInstance();
        mqttUtil.getClient(this);

        return START_REDELIVER_INTENT;
    }

    private void connect() {
        try{
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT Background", "연결 성공");
                    reSubscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT Background", "연결 실패");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void reSubscribe() {
        SharedPreferences sharedPreferences = getSharedPreferences(MQTT.SUBSCRIBES.value(), Activity.MODE_PRIVATE);

        Set<String> savedTopics = sharedPreferences.getStringSet(MQTT.TOPICS.value(), null);
        if (savedTopics != null) {
            for (String topic : savedTopics) {
                subscribe(topic, this);
            }
        }
    }

    private void subscribe(String topic, Context context) {
        try {
            client.subscribe(topic, 1);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String arrivedMessage = new String(message.toString());
                    mqttNotification.createNotification(context, arrivedMessage, topic);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
