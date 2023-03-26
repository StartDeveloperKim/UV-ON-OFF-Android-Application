package com.example.hello.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hello.MainActivity;
import com.example.hello.R;
import com.example.hello.memory.SharedPreferencesMemory;
import com.example.hello.mqtt.MQTT;
import com.example.hello.mqtt.MqttUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    private MqttUtil mqttUtil;
    private SharedPreferencesMemory sharedPreferencesMemory;

    private Map<Integer, String> buttonTopicRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mqttUtil = MqttUtil.getMqttUtilInstance();
        sharedPreferencesMemory = SharedPreferencesMemory.getInstance();
        buttonTopicRepository = new HashMap<>();

        makeTableLayout();
    }

    public void backButtonOnclick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void onClickAddButton(View view) {
        String topic = getTopic();

        if (checkDuplicateTopic(topic)) {
            sharedPreferencesMemory.addTopic(topic);
            addTableRow(findViewById(R.id.tableLayout), topic);
        } else {
            Toast.makeText(this, "중복되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTopic() {
        EditText editText = findViewById(R.id.editText);
        return editText.getText().toString();
    }

    private boolean checkDuplicateTopic(String topic) {
        Set<String> savedTopics = sharedPreferencesMemory.getTopicsAtSharedPreference();
        if (savedTopics == null) {
            return true;
        }
        for (String t : savedTopics) {
            System.out.println("t = " + t);
        }

        return savedTopics.contains(topic) == false;
    }

    private void subscribeTopic(int buttonId) {
        String topic = buttonTopicRepository.get(buttonId);
        if (mqttUtil.setSubscribe(topic, this)) {
            Toast.makeText(this, topic + "구독되었습니다.", Toast.LENGTH_SHORT);
        }
    }

    private void unSubscribeTopic(int buttonId) {
        String topic = buttonTopicRepository.get(buttonId);
        mqttUtil.unSubscribe(topic, this);
    }

    private void removeTopic(int buttonId) {
        SharedPreferences.Editor editor = sharedPreferencesMemory.getEditor();
        Set<String> savedTopics = sharedPreferencesMemory.getTopicsAtSharedPreference();

        String topic = buttonTopicRepository.get(buttonId);
        if (savedTopics.contains(topic)) {
            if (mqttUtil.unSubscribe(topic, this)) {
                savedTopics.remove(topic);
                editor.putStringSet(MQTT.TOPICS.value(), savedTopics);
            }
        }
        editor.apply();
    }

    private void makeTableLayout() {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        Set<String> topics = sharedPreferencesMemory.getTopicsAtSharedPreference();
        if (topics == null) {
            return;
        }
        for (String topic : topics) {
            addTableRow(tableLayout, topic);
        }
    }

    private void addTableRow(TableLayout tableLayout, String topic) {
        TableRow tableRow = new TableRow(this);

        TextView textView = getTextView(topic);
        Button subscribeButton = getSubscribeButton(topic);
        Button cancelButton = getCancelButton(topic);
        Button removeButton = getRemoveButton(topic);

        tableRow.addView(textView);
        tableRow.addView(subscribeButton);
        tableRow.addView(cancelButton);
        tableRow.addView(removeButton);

        tableLayout.addView(tableRow);
    }

    @NonNull
    private TextView getTextView(String topic) {
        TextView textView = new TextView(this);
        textView.setText(topic);
        return textView;
    }

    @NonNull
    private Button getSubscribeButton(String topic) {
        return makeButton(topic, ButtonContent.SUBSCRIBE);
    }

    @NonNull
    private Button getRemoveButton(String topic) {
        return makeButton(topic, ButtonContent.REMOVE);
    }

    @NonNull
    private Button getCancelButton(String topic) {
        return makeButton(topic, ButtonContent.UNSUBSCRIBE);
    }

    private Button makeButton(String topic, ButtonContent buttonContent) {
        Button button = new Button(this);
        int buttonId = View.generateViewId();
        button.setId(buttonId);
        button.setText(buttonContent.getContent());
        buttonTopicRepository.put(buttonId, topic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceOnclickMethod(buttonContent, buttonId);
            }
        });
        return button;
    }

    private void choiceOnclickMethod(ButtonContent buttonContent, int buttonId) {
        if (buttonContent == ButtonContent.SUBSCRIBE) {
            subscribeTopic(buttonId);
        } else if (buttonContent == ButtonContent.UNSUBSCRIBE) {
            unSubscribeTopic(buttonId);
        }else{
            removeTopic(buttonId);
        }
    }

}



