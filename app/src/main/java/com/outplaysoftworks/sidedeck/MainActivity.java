package com.outplaysoftworks.sidedeck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_PLAYER_ONE_DEF_NAME = "playerOneDefaultNameSetting"; //NON-NLS
    public static final String KEY_PLAYER_TWO_DEF_NAME = "playerTwoDefaultNameSetting"; //NON-NLS
    public static final String KEY_SOUND_ONOFF = "soundOnOff"; //NON-NLS
    public static final String KEY_DEFAULT_LP = "defaultLpSetting"; //NON-NLS
    public static final String KEY_AMOLED_BLACK = "amoledBlackTheme"; //NON-NLS

    public static final String THEME_A_MATERIAL = "a_material_theme"; //NON-NLS
    public static final String THEME_A_MATERIAL_DARK = "a_material_theme_dark"; //NON-NLS

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    PopupMenu popup;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TextView playerOneName;
    TextView playerTwoName;
    static Context myContext;
    static boolean firstRun = true;
    public static SharedPreferences sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Pure layout stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //End of pure layout stuff
        playerOneName = (TextView)findViewById(R.id.playerOneName);
        playerTwoName= (TextView)findViewById(R.id.playerTwoName);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setPreferences();


    }

    @Override
    public void setTitle(CharSequence title) {
        CharSequence mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch(position){
                case 0: return new CalcFragment();
                case 1: return new LogFragment();
            }
            return new LogFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.calculator);
                case 1:
                    return getResources().getText(R.string.duelLog);
            }
            return null;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    //On click method for overflow button
    public void showPopup(final View v) {
        popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().toString().equals(getResources().getText(R.string.reset).toString())){
                    CalcFragment.reset();
                    return true;
                }else if(item.getTitle().toString().equals(getResources().getText(R.string.quickCalc).toString())){
                    CalcFragment.qcShow();
                }else if(item.getTitle().toString().equals(getResources().getText(R.string.settings).toString())){
                    Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }


    //Test line for git
    //Appends whatever numerical button is pressed to the lp calc preview
    public void addToNumberHolder(View view){
        CalcFragment.addToNumberHolder(view);
    }

    //Sets everything related to lp calc preview to 0 or empty
    public void resetNumberHolder(View view){
        CalcFragment.resetNumberHolder();
    }

    public void modLP(View view){
        CalcFragment.modLP(view.getTag().toString());
    }

    public void diceRoll(View view){
        CalcFragment.diceRoll();
    }

    public void coinFlip(View view){
        CalcFragment.coinFlip();
    }

    public void qcAddToWorkHolder(View view){
        CalcFragment.qcAddToWorkHolder(view);
    }

    public void qcResetHolder(View view){
        CalcFragment.qcResetHolder(view);
    }

    public void qcShow(View view){
        CalcFragment.qcShow();
    }

    public void qcHide(View view){
        CalcFragment.qcHide();
    }

    public void qcOpperators(View view){
        CalcFragment.qcOpperators(view);


    }

    public static   void setPreferences() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(myContext);
        CalcFragment.playerOneNameString = sharedPrefs.getString(KEY_PLAYER_ONE_DEF_NAME, "");
        CalcFragment.playerTwoNameString = sharedPrefs.getString(KEY_PLAYER_TWO_DEF_NAME, "");
        CalcFragment.soundOn = sharedPrefs.getBoolean(KEY_SOUND_ONOFF, true);
        CalcFragment.defaultLP = Integer.parseInt(sharedPrefs.getString(KEY_DEFAULT_LP, "8000"));
        if (!firstRun) {
            CalcFragment.playerTwoName.setText(CalcFragment.playerTwoNameString);
            CalcFragment.playerOneName.setText(CalcFragment.playerOneNameString);
        }
        firstRun = false;

    }
}
