package com.example.hello;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hello.destroy.ForcedTerminationService;
import com.example.hello.layout.SettingActivity;
import com.example.hello.memory.SharedPreferencesMemory;
import com.example.hello.mqtt.MqttService;
import com.example.hello.mqtt.MqttUtil;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final String OFF = "UV OFF";
    private final String ON = "UV ON";


    private MqttUtil mqttUtil;
    private SharedPreferencesMemory sharedPreferencesMemory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesMemory = SharedPreferencesMemory.getInstance();
        sharedPreferencesMemory.setSharedPreferencesMemory(this);

        mqttUtil = MqttUtil.getMqttUtilInstance();

        makeSubscribeList();
    }

    public void intentSettingLayout(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void uvButtonClick(View view) throws MqttException {
        Button uvBtn = findViewById(R.id.UV_button);
        CompoundButton changeSwitch = findViewById(R.id.on_off_switch);

        // uvBtn의 텍스트 값이 기본값이 있으면 안된다. 현재 내 자동차 상태를 보고 text를 결정해야한다.

//        if (uvBtn.getText().equals(OFF)) {
//            Toast.makeText(this, "UV가 켜졌습니다.", Toast.LENGTH_SHORT).show();
//            changeButtonLayout(Color.parseColor("#0101DF"), ON, uvBtn);
//            mqttUtil.publishMessage("UV ON");
//
//        } else {
//            Toast.makeText(this, "UV가 꺼졌습니다.", Toast.LENGTH_SHORT).show();
//            changeButtonLayout(Color.parseColor("#FF0000"), OFF, uvBtn);
//            mqttUtil.publishMessage("UV OFF");
//        }

        if (changeSwitch.isChecked()) {
            Toast.makeText(this, "UV가 켜졌습니다.", Toast.LENGTH_SHORT).show();
            mqttUtil.publishMessage("UV ON");
        } else {
            Toast.makeText(this, "UV가 꺼졌습니다.", Toast.LENGTH_SHORT).show();
            mqttUtil.publishMessage("UV OFF");
        }
    }

    private void changeButtonLayout(int color, String text, Button button) {
        button.setBackgroundColor(color);
        button.setText(text);
    }

    private void makeSubscribeList() {
        CompoundButton changeSwitch = findViewById(R.id.on_off_switch);
        String nowTopic = sharedPreferencesMemory.getNowTopic();

        if (nowTopic != null) {
            changeSwitch.setText(nowTopic);
        }

//        changeSwitch.setChecked(true);
        /*
        * 앱이 켜질 때 현재 구독 중인 자동차에게 check 메시지를 보내고
        * 해당 자동차는 check 메시지를 받으면 현재 상태(ON/OFF)를 보낸다.
        * 해당 메시지에 따라 스위치의 상태를 바꾼다.
        * */
    }

}