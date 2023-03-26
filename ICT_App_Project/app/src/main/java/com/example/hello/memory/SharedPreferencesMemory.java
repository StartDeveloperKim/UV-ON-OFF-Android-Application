package com.example.hello.memory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.hello.mqtt.MQTT;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesMemory {

    private static final SharedPreferencesMemory sharedPreferencesMemory = new SharedPreferencesMemory();
    private static final String TOPICS = "topics";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferencesMemory() {
    }

    public void setSharedPreferencesMemory(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    public static SharedPreferencesMemory getInstance() {
        return sharedPreferencesMemory;
    }

    public Set<String> getTopicsAtSharedPreference() {
        return sharedPreferences.getStringSet(TOPICS, null);
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void addTopic(String topic) {
        Set<String> savedTopics = getTopicsAtSharedPreference();

        if (savedTopics == null) {
            savedTopics = new HashSet<>();
        }
        savedTopics.add(topic);
        editor.putStringSet(TOPICS, savedTopics);
        editor.apply();
    }
}
