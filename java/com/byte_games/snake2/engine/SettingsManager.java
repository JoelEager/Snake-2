package com.byte_games.snake2.engine;

import android.app.Activity;
import android.content.SharedPreferences;

//A wrapper around the android.content.SharedPreferences class

public class SettingsManager {
    private SharedPreferences mySettings;
    private SharedPreferences.Editor myEditor;
    Object value;
    String SettingName;

    public SettingsManager(Activity myContext, String Group, String Setting, int defaultValue) {
        mySettings = myContext.getSharedPreferences(Group, Activity.MODE_PRIVATE);
        myEditor = mySettings.edit();
        value = mySettings.getInt(Setting, defaultValue);
        SettingName = Setting;
    }

    public SettingsManager(Activity myContext, String Group, String Setting, boolean defaultValue) {
        mySettings = myContext.getSharedPreferences(Group, Activity.MODE_PRIVATE);
        myEditor = mySettings.edit();
        value = mySettings.getBoolean(Setting, defaultValue);
        SettingName = Setting;
    }

    public int getInt() {
        return (int) value;
    }

    public boolean getBoolean() {
        return (boolean) value;
    }

    public void putInt(int newValue) {
        myEditor.putInt(SettingName, newValue);
        myEditor.commit();

        value = newValue;
    }

    public void putBoolean(boolean newValue) {
        myEditor.putBoolean(SettingName, newValue);
        myEditor.commit();

        value = newValue;
    }
}