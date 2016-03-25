package com.outplaysoftworks.sidedeck;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String KEY_PLAYER_ONE_DEF_NAME = MainActivity.KEY_PLAYER_ONE_DEF_NAME;
    private static final String KEY_PLAYER_TWO_DEF_NAME = MainActivity.KEY_PLAYER_TWO_DEF_NAME;
    private static final String KEY_SOUND_ONOFF = MainActivity.KEY_SOUND_ONOFF;
    private static final String KEY_DEFAULT_LP = MainActivity.KEY_DEFAULT_LP;
    private static SharedPreferences sharedPrefs = MainActivity.sharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        try {
            findPreference(KEY_PLAYER_ONE_DEF_NAME).setSummary(CalcFragment.playerOneNameString);
            findPreference(KEY_PLAYER_TWO_DEF_NAME).setSummary(CalcFragment.playerTwoNameString);
            findPreference(KEY_DEFAULT_LP).setSummary(CalcFragment.defaultLP.toString());
        } catch (Exception e){
        }
        /*Context context = getApplicationContext();
        ListView listView = getListView();
        listView.setBackgroundColor(getResources().getColor(R.color.a_material_dark));
        for(int i = 0; i < listView.getChildCount() - 1; i++){
            try{
                EditTextPreference temp = (EditTextPreference)listView.getChildAt(i);
            }catch (Exception e){

            }
        }*/
    }

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
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}