package com.outplaysoftworks.sidedeck;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
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

import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    static IInAppBillingService mService;
    static ServiceConnection mServiceConn;
    static PopupMenu popup;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TextView playerOneName;
    TextView playerTwoName;
    static Context myContext;
    static boolean firstRun = true;
    public static SharedPreferences sharedPrefs;
    public static SharedPreferences.Editor editor;
    public static AppBarLayout appbar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Pure layout stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;

        //iapCreate();
        //TODO: Finish iapCreate stuff http://developer.android.com/google/play/billing/billing_integrate.html
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //Setup the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        playerOneName = (TextView) findViewById(R.id.playerOneName);
        playerTwoName = (TextView) findViewById(R.id.playerTwoName);

        //End of pure layout stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPrefs.edit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setPreferences();
        /*if(debug){editor.clear().commit();}*/
        /*sharedPrefs.edit().putLong("launch_count", 5L).commit();
        sharedPrefs.edit().putBoolean("dontshowagain", false).commit();
        editor.putBoolean("remind_me_later", false).commit();*/
        appbar = (AppBarLayout) findViewById(R.id.appbar);

        app_launched(this, sharedPrefs);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

 /*   @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.outplaysoftworks.sidedeck/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.outplaysoftworks.sidedeck/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
*/
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
            switch (position) {
                case 0:
                    return new CalcFragment();
                case 1:
                    return new LogFragment();
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
    public static void showPopup(final View v) {
        popup = new PopupMenu(myContext, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuItemReset) {
                    AlertDialog.Builder confirm = new AlertDialog.Builder(myContext);
                    confirm.setMessage(myContext.getString(R.string.AreYouSure))
                            .setPositiveButton(myContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast toast = Toast.makeText(myContext, R.string.Resetting, Toast.LENGTH_SHORT);
                                    toast.show();
                                    CalcFragment.reset();
                                }
                            })
                            .setNegativeButton(myContext.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();

                    return true;
                } else if (item.getItemId() == R.id.menuItemShowQuickCalc) {
                    CalcFragment.qcShow();
                } else if (item.getItemId() == R.id.menuItemSettings) {
                    Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                    myContext.startActivity(intent);
/*                } else if (item.getItemId() == R.id.debugRating) {
                    showEnjoyOrNotDialog(myContext, sharedPrefs.edit(), 100l);
                } else if (item.getItemId() == R.id.debugTutorial) {
                    CalcFragment.showTutorial();
                } else if (item.getItemId() == R.id.clearPreferences) {
                    clearPreferences();*/
                }
                return false;
            }
        });
    }

    //Appends whatever numerical button is pressed to the lp calc preview
    public void addToNumberHolder(View view) {
        CalcFragment.addToNumberHolder(view);
    }

    //Sets everything related to lp calc preview to 0 or empty
    public void resetNumberHolder(View view) {
        CalcFragment.resetNumberHolder();
    }

    public void modLP(View view) {
        CalcFragment.modLP(view.getTag().toString());
    }

    public void diceRoll(View view) {
        CalcFragment.diceRoll();
    }

    public void coinFlip(View view) {
        CalcFragment.coinFlip();
    }

    public void qcAddToWorkHolder(View view) {
        CalcFragment.qcAddToWorkHolder(view);
    }

    public void qcResetHolder(View view) {
        CalcFragment.qcResetHolder(view);
    }

    public void qcShow(View view) {
        CalcFragment.qcShow();
    }

    public void qcHide(View view) {
        CalcFragment.qcHide();
    }

    public void qcOperators(View view) {
        CalcFragment.qcOperators(view);
    }

    public static void setPlayerOneNamePreference() {
        CalcFragment.setPlayerOneNameString(sharedPrefs.getString(Constants.KEY_PLAYER_ONE_DEF_NAME,
                myContext.getResources().getString(R.string.playerOne)));
        if (!firstRun) {
            CalcFragment.getPlayerOneName().setText(CalcFragment.playerOneNameString);
        }
    }

    public static void setPlayerTwoNamePreference() {
        CalcFragment.setPlayerTwoNameString(sharedPrefs.getString(Constants.KEY_PLAYER_TWO_DEF_NAME,
                myContext.getResources().getString(R.string.playerTwo)));
        if (!firstRun) {
            CalcFragment.getPlayerTwoName().setText(CalcFragment.playerTwoNameString);
        }
    }

    public static void setSoundOnOffPreference() {
        CalcFragment.setSoundOn(sharedPrefs.getBoolean(Constants.KEY_SOUND_ONOFF, true));
    }

    public static void setDefaultLPPreference() {
        CalcFragment.setDefaultLP(Integer.parseInt(sharedPrefs.getString(Constants.KEY_DEFAULT_LP, "8000")));
    }

    public static void setAmoledBlackTogglePreference() {
        try {
            CalcFragment.amoledBlackToggle();
            LogFragment.amoledBlackToggle();//TODO: Implement this method
        } catch (Exception e) {
        }
    }

    public static void setPreferences() {
        setDefaultLPPreference();
        setPlayerTwoNamePreference();
        setPlayerOneNamePreference();
        setAmoledBlackTogglePreference();
        setSoundOnOffPreference();
    }


    public static void app_launched(Context mContext, SharedPreferences prefs) {
        if (prefs.getBoolean(Constants.KEY_DONT_SHOW_AGAIN, false)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        long launch_count = 0;
        long launch_when_user_pressed_remind = 0;

        // Increment launch counter
        launch_count = prefs.getLong(Constants.KEY_LAUNCH_COUNT, 0) + 1;
        launch_when_user_pressed_remind = prefs.getLong(Constants.KEY_LAUNCH_COUNT_PRESSED_REMIND, launch_count);
        editor.putLong(Constants.KEY_LAUNCH_COUNT, launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong(Constants.KEY_DATE_FIRSTLAUNCH, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(Constants.KEY_DATE_FIRSTLAUNCH, date_firstLaunch);
        }
        editor.commit();

        // Wait at least n days before opening
        if (prefs.getBoolean(Constants.KEY_REMIND_ME_LATER, false) && launch_count <= launch_when_user_pressed_remind + Constants.LAUNCHES_UNTIL_PROMPT) {
            return;
        }
        if (launch_count >= Constants.LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch/* + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)*/) {
                showEnjoyOrNotDialog(mContext, editor, launch_count);
            }
        }

    }

    public static void showEnjoyOrNotDialog(final Context mContext, final SharedPreferences.Editor editor, final long launches) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.enjoyingSidedeck)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showRateDialog(mContext, editor, launches);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.notReally, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSendFeedbackDialog(mContext, editor);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    private static void showSendFeedbackDialog(final Context mContext, final SharedPreferences.Editor editor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.giveFeedback)
                .setPositiveButton(R.string.okSure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendFeedBack(mContext);
                        editor.putBoolean(Constants.KEY_DONT_SHOW_AGAIN, true);
                        editor.apply();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.noThanks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean(Constants.KEY_DONT_SHOW_AGAIN, true);
                        editor.apply();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private static void sendFeedBack(Context mContext) {
        try {
            Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
            intent.setData(Uri.parse(mContext.getString(R.string.mailTo)));
            myContext.startActivity(intent);
        } catch (Exception e) {

        }

    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor, final Long launches) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.rateMeText)
                .setPositiveButton(R.string.rateMe, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        editor.putBoolean(Constants.KEY_REMIND_ME_LATER, false);
                        editor.putBoolean(Constants.KEY_DONT_SHOW_AGAIN, true);
                        editor.apply();
                        try {
                            intent.setData(Uri.parse(myContext.getString(R.string.playStoreMarketLink) + Constants.APP_PNAME));
                            myContext.startActivity(intent);
                        } catch (Exception e) {
                            intent.setData(Uri.parse(myContext.getString(R.string.playStoreHttpLink) + Constants.APP_PNAME));
                            myContext.startActivity(intent);

                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.noThanksAndDoNotAskAgain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editor != null) {
                            editor.putBoolean(Constants.KEY_REMIND_ME_LATER, false);
                            editor.putBoolean(Constants.KEY_DONT_SHOW_AGAIN, true);
                            editor.apply();
                        }
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.remindMeLater, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putLong(Constants.KEY_LAUNCH_COUNT_PRESSED_REMIND, launches);
                        editor.putBoolean(Constants.KEY_REMIND_ME_LATER, true);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private static void displayTutorial() {
        if (checkHasLaunched()) {
            CalcFragment.showTutorial();
        }
    }

    private static boolean checkHasLaunched() {
        boolean hasLaunched = sharedPrefs.getBoolean(Constants.KEY_HAS_BEEN_LAUNCHED, false);
        editor.putBoolean(Constants.KEY_HAS_BEEN_LAUNCHED, true).commit();
        return hasLaunched;
    }

    public static void clearPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private static void iapCreate() {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}
