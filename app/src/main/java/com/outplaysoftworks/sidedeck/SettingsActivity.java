package com.outplaysoftworks.sidedeck;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_PLAYER_ONE_DEF_NAME = "playerOneDefaultNameSetting";
    public static final String KEY_PLAYER_TWO_DEF_NAME = "playerTwoDefaultNameSetting";
    public static final String KEY_SOUND_ONOFF = "soundOnOff";
    public static final String KEY_DEFAULT_LP = "defaultLpSetting";
    public static SharedPreferences sharedPrefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }



    public static void doThing(){}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(KEY_PLAYER_ONE_DEF_NAME)) {
            Preference player1Pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            player1Pref.setSummary(sharedPreferences.getString(key, ""));
        }
        if(key.equals(KEY_PLAYER_TWO_DEF_NAME)) {
            Preference player2Pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            player2Pref.setSummary(sharedPreferences.getString(key, ""));
        }
        if(key.equals(KEY_DEFAULT_LP)){
            Preference temp = findPreference(key);
            temp.setSummary(sharedPreferences.getString(key, ""));
        }
        MainActivity.setPreferences();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}