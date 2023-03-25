package com.example.hello.memory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.hello.mqtt.MQTT;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesMemory {

    private static final String SUBSCRIBES = "subscribes";
    private static final String TOPICS = "topics";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesMemory(Context context) {
//        this.sharedPreferences = context.getSharedPreferences(SUBSCRIBES, Activity.MODE_PRIVATE);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    public Set<String> getTopicsAtSharedPreference(Context context) {
        return sharedPreferences.getStringSet(TOPICS, null);
    }

    public SharedPreferences.Editor getEditor(Context context) {
        return editor;
    }

    public SharedPreferences getSharedPreferences(Context context) {
        return sharedPreferences;
    }

    public void addTopic(String topic, Context context) {
        Set<String> savedTopics = getTopicsAtSharedPreference(context);

        if (savedTopics == null) {
            savedTopics = new HashSet<>();
        }
        savedTopics.add(topic);
        editor.putStringSet(TOPICS, savedTopics);
        editor.apply();
    }
}
