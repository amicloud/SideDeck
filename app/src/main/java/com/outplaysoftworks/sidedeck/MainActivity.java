package com.outplaysoftworks.sidedeck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
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

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class MainActivity extends AppCompatActivity {

    private static boolean debug = true;

    public static final String KEY_PLAYER_ONE_DEF_NAME = "KEYplayerOneDefaultNameSetting"; //NON-NLS
    public static final String KEY_PLAYER_TWO_DEF_NAME = "KEYplayerTwoDefaultNameSetting"; //NON-NLS
    public static final String KEY_SOUND_ONOFF = "KEYsoundOnOff"; //NON-NLS
    public static final String KEY_DEFAULT_LP = "KEYdefaultLpSetting"; //NON-NLS
    public static final String KEY_AMOLED_BLACK = "KEYamoledNightModeSetting"; //NON-NLS
    public static final String KEY_HAS_USER_RATED = "KEYhasUserRatedAppYet"; //NON-NLS
    private final static String APP_PNAME = "com.outplaysoftworks.sidedeck";// Package Name

    public static final String THEME_A_MATERIAL = "a_material_theme"; //NON-NLS
    public static final String THEME_A_MATERIAL_DARK = "a_material_theme_dark"; //NON-NLS

    PopupMenu popup;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TextView playerOneName;
    TextView playerTwoName;
    static Context myContext;
    static boolean firstRun = true;
    public static SharedPreferences sharedPrefs;
    public static SharedPreferences.Editor editor;

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
        playerOneName = (TextView)findViewById(R.id.playerOneName);
        playerTwoName= (TextView)findViewById(R.id.playerTwoName);

        //End of pure layout stuff
        /*sharedPrefs = this.getSharedPreferences("com.outplaysoftworks.sidedeck", 0);*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPrefs.edit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setPreferences();
        /*if(debug){editor.clear().commit();}*/
        /*sharedPrefs.edit().putLong("launch_count", 0L).commit();*/
        System.out.println("Launch count should be: " + sharedPrefs.getLong("launch_count", 100));

        app_launched(this, sharedPrefs);
        /*showTutorialAfterViewsPopulated(playerOneName);*/
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

    public void qcOperators(View view){
        CalcFragment.qcOperators(view);


    }

    public static void setPreferences() {
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


    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches
    public static void app_launched(Context mContext, SharedPreferences prefs) {
        if (prefs.getBoolean("dontshowagain", false) ) { return ; }
        SharedPreferences.Editor editor = prefs.edit();
        long launch_count = 0;
        long launch_when_user_pressed_remdind = 0;


        // Increment launch counter
        launch_count = prefs.getLong("launch_count", 0) + 1;
        launch_when_user_pressed_remdind = prefs.getLong("launch_count_pressed_remind", launch_count);
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }
        editor.commit();

        // Wait at least n days before opening
        if(prefs.getBoolean("remind_me_later", false) && launch_count <= launch_when_user_pressed_remdind + LAUNCHES_UNTIL_PROMPT) {
            return;
        }
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            System.out.println("\n\n\n\nLaunch Count: " + launch_count + ", Launches until prompt: " + LAUNCHES_UNTIL_PROMPT);
            if (System.currentTimeMillis() >= date_firstLaunch/* + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)*/) {
                System.out.println("Showing Dialog");
                showRateDialog(mContext, editor, launch_count);
            }
        }

    }


    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor, final Long launches) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.rateMeText)
                .setPositiveButton(R.string.rateMe, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            intent.setData(Uri.parse(myContext.getString(R.string.playStoreMarketLink) + APP_PNAME));
                            myContext.startActivity(intent);
                        }catch (Exception e){
                            intent.setData(Uri.parse(myContext.getString(R.string.playStoreHttpLink) + APP_PNAME));
                            myContext.startActivity(intent);

                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.noThanks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.remindMeLater, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putLong("launch_count_pressed_remind", launches);
                        editor.putBoolean("remind_me_later", true);
                        editor.commit();
                        System.out.print(sharedPrefs.getLong("launch_count_pressed_remind", 27727));
                    }
                });
        builder.create().show();
    }

    private void displayTutorial(){
        if(checkHasLaunched()){
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    private boolean checkHasLaunched(){
        boolean hasLaunched = sharedPrefs.getBoolean("has_been_launch", false);
        editor.putBoolean("has_been_launched", true).commit();
        return hasLaunched;
    }

    public void showTutorialAfterViewsPopulated(View view){
        ViewTarget target = new ViewTarget(view);
        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("aaaaaaa")
                .setContentText("bbbbbbbb")
                .setStyle(R.style.MyMaterialTheme)
                .setShowcaseEventListener((OnShowcaseEventListener) this) //Wrong as hell
                .build();
    }
}
