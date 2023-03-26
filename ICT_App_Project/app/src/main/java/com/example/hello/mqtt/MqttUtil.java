package com.example.hello.mqtt;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.hello.MainActivity;
import com.example.hello.R;
import com.example.hello.memory.SharedPreferencesMemory;
import com.example.hello.notification.MqttNotification;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Set;

public class MqttUtil {

    private static final MqttUtil mqttUtil = new MqttUtil();
    private final SharedPreferencesMemory sharedPreferencesMemory;
    //    private static final String BROKER_URL = "tcp://10.0.2.2:1883";
    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String PUBLISH_TOPIC = "topic";

    private static boolean published;
    private static MqttAndroidClient client;

    private static final String TAG = "MqttUtil";
    private MqttNotification mqttNotification = new MqttNotification();

    private MqttUtil() {
        sharedPreferencesMemory = SharedPreferencesMemory.getInstance();
    }

    public static MqttUtil getMqttUtilInstance() {
        return mqttUtil;
    }


    public void getClient(Context context) {
        if (client == null) {
//            String clientId = MqttClient.generateClientId();
            String clientId = "taewoo";
            client = new MqttAndroidClient(context, BROKER_URL, clientId);
        }
        if (!client.isConnected()) {
            connect(context);
        }
    }

    public boolean checkConnect() {
        System.out.println("client null check : " + client != null);
        System.out.println("client connect check :  " + client.isConnected());
        return client != null && client.isConnected();
    }


    public void connect(Context context) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(30);

        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                    reSubscribe(context);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("연결 실패");
                    Log.d(TAG, "onFailure. Exception when Connecting" + exception);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error while connecting to Mqtt broker : " + e);
            e.printStackTrace();
        }
    }

    public boolean setSubscribe(final String topic, final Context context) {
        try {
            client.subscribe(topic, 1);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String arrivedMessage = new String(message.toString());
                    Log.d("valueOf", String.valueOf(message.getPayload()));
                    Log.d("text", arrivedMessage);

                    mqttNotification.createNotification(context, arrivedMessage, topic);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            return true;
        } catch (MqttException e) {
            Log.e(TAG, "Error when subscribe : " + e);
            Toast.makeText(context, "구독에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

            return false;
        }
    }

    /*앱을 다시 켰을 때 재구독*/
    public void reSubscribe(Context context) {
        Set<String> savedTopics = sharedPreferencesMemory.getTopicsAtSharedPreference();
        if (savedTopics != null) {
            for (String topic : savedTopics) {
                setSubscribe(topic, context);
            }
        }
    }

    /*구독 삭제*/
    public boolean unSubscribe(final String topic, final Context context) {
        try {
            client.unsubscribe(topic);
            Toast.makeText(context, "구독이 취소되었습니다.",  Toast.LENGTH_SHORT).show();

            return true;
        } catch (MqttException e) {
            Log.e(TAG, "Error when unSubscribe : " + e);
            Toast.makeText(context, "구독취소에 실패했습니다.",  Toast.LENGTH_SHORT).show();
            e.printStackTrace();

            return false;
        }
    }


    public void publishMessage(final String payload) {
        published = false;
        try {
            byte[] encodedPayload = payload.getBytes();
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(PUBLISH_TOPIC, message);
            published = true;
            Log.i(TAG, "message successfully published : " + payload);
        } catch (Exception e) {
            Log.e(TAG, "Error when publishing message : " + e);
            e.printStackTrace();
        }
    }

    public void close(Context context) {
        if (client != null) {
            try {
                IMqttToken token = client.disconnect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(context, "연결이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(context, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


}
