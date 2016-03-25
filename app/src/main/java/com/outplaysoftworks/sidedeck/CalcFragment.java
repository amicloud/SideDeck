package com.outplaysoftworks.sidedeck;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.sourceforge.jeval.*;
import java.util.Random;

public class CalcFragment extends Fragment {

    static String playerOneNameString = "";
    static String playerTwoNameString = "";
    static boolean soundOn;
    static TextView numberHolder;
    static TextView playerOneLP;
    static TextView playerTwoLP;
    static Button turnButton;
    static EditText playerOneName;
    static EditText playerTwoName;
    static SoundPool soundPool;
    static Integer lpCounterSoundId;
    static Context ourContext;
    static Toast lpToast;
    static Button diceButton;
    static Button coinButton;
    static View thisView;
    static ViewGroup parentView;
    static ImageButton overFlowButton;

    static TextView qcWorkHolder;
    static TextView qcResultHolder;
    static AppBarLayout appBarLayout;

    static Integer numberTransitionDuration = 1050;
    public static Integer defaultLP = 8000;
    static Integer turnNumber = 1;
    static Integer currentLP1 = defaultLP;
    static Integer previousLP1;
    static Integer currentLP2 = defaultLP;
    static Integer previousLP2;
    static Integer diceNumber;
    static Random random = new Random();
    static String numberHolderString = "";
    static Integer numberHolderNumber = 0;
    static String toastText = "";
    public static boolean firstRun = true;
    static boolean justPressedOpeator = false;

    static String qcWorkString = "";
    static String qcResultString = "";
    static Evaluator evaluator = new Evaluator();
    static RelativeLayout qcHolderView;
    static Resources resources;
    private static String lastButtonPressed;
    private static String lastOperatorPressed;

    static LinearLayout topHalf;
    static LinearLayout bottomHalf;

    public CalcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calc, container, false);
        resources = view.getContext().getResources();
        //Assign view variables after inflation is done
        parentView = container;
        assignVariableIds(view);
        makeListeners();
        reset();
        thisView = view;
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(view.getContext(), R.raw.lpcountersound, 1);

        //return that view
        playerTwoName.setText(CalcFragment.playerTwoNameString);
        playerOneName.setText(CalcFragment.playerOneNameString);
        AdView mAdView = (AdView) view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        showTutorial(numberHolder, 1);
        amoledBlackToggle();
        return view;
    }

    private void makeListeners(){
        playerOneName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER | event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    v.clearFocus();
                    hideKeyboardFrom(getContext(), v);
                    return true;
                }
                return false;
            }
        });
        playerTwoName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER | event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    v.clearFocus();
                    hideKeyboardFrom(getContext(), v);
                    return true;
                }
                return false;
            }
        });
        turnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnNumber++;
                String temp = resources.getText(R.string.turn).toString() + turnNumber;
                turnButton.setText(temp);
                LogFragment.currentTurn = turnNumber;
                LogFragment.addSection();
            }
        });
        turnButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(turnNumber > 0) {
                    turnNumber--;
                    String temp = resources.getText(R.string.turn).toString() + turnNumber;
                    turnButton.setText(temp);
                    LogFragment.currentTurn = turnNumber;
                }
                return true;
            }
        });
    }

    //Assigns view variables with java ids
    @SuppressLint("SetTextI18n")
    private void assignVariableIds(View view){
        numberHolder = (TextView)view.findViewById(R.id.numberHolder);
        playerOneLP = (TextView)view.findViewById(R.id.playerOneLP);
        playerOneLP.setText(defaultLP.toString());
        playerTwoLP = (TextView)view.findViewById(R.id.playerTwoLP);
        playerTwoLP.setText(defaultLP.toString());
        turnButton = (Button)view.findViewById(R.id.buttonTurn);
        playerOneName = (EditText)view.findViewById(R.id.playerOneName);
        playerTwoName = (EditText)view.findViewById(R.id.playerTwoName);
        ourContext = getContext();
        diceButton = (Button)view.findViewById(R.id.diceButton);
        coinButton = (Button)view.findViewById(R.id.coinButton);
        qcWorkHolder = (TextView)view.findViewById(R.id.qcWorkDisplay);
        qcResultHolder = (TextView)view.findViewById(R.id.qcResultDisplay);
        qcHolderView = (RelativeLayout)view.findViewById(R.id.quickCalcHolder);
        playerOneName.setText(playerOneNameString);
        playerTwoName.setText(playerTwoNameString);
        overFlowButton = (ImageButton)view.findViewById(R.id.overFlowButton);
        appBarLayout = MainActivity.appbar;
    }

    //Performs dice roll
    public static void diceRoll(){
        Integer temp = random.nextInt(6);
        temp++;//Can't roll a zero
        diceButton.setText(temp.toString());
        String diceRoll = resources.getText(R.string.diceRoll).toString();
        LogFragment.addDataToSection(turnNumber, diceRoll + ": " + temp);
    }

    //Performs coin flip
    public static void coinFlip(){
        String coinflip = resources.getText(R.string.coinFlip).toString() + ": ";
        Double temp = Math.random();
        if(temp>0.5){
            coinButton.setText(R.string.coinHeads);
            LogFragment.addDataToSection(turnNumber, coinflip + resources.getText(R.string.coinHeads).toString());
        }else if(temp<=0.5){
            coinButton.setText(R.string.coinsTails);
            LogFragment.addDataToSection(turnNumber, coinflip + resources.getText(R.string.coinsTails).toString());
        }
    }

    public static void setNumberHolderText(String value){
        numberHolder.setText(value);
    }

    //Changes LP
    public static void modLP(String tag){
        Integer value = numberHolderNumber;
        previousLP1 = currentLP1;
        previousLP2 = currentLP2;

        if(value != 0) {
            if(soundOn)soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
            switch (tag) {
                case "1+":
                    if (currentLP1 + value < 999999) {
                        currentLP1 += value;
                        toastText = value + resources.getText(R.string.lpAddedTo).toString() + playerOneNameString;
                        displayToastAndSendToLog();
                    } else {
                        currentLP1 = 999999;
                    }
                    break;
                case "1-":
                    if (currentLP1 - value > 0) {
                        currentLP1 -= value;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerOneNameString;
                        displayToastAndSendToLog();
                    } else {
                        currentLP1 = 0;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerOneNameString;
                        displayToastAndSendToLog();
                    }
                    break;
                case "2+":
                    if (currentLP2 + value < 999999) {
                        currentLP2 += value;
                        toastText = value + resources.getText(R.string.lpAddedTo).toString() + playerTwoNameString;
                        displayToastAndSendToLog();
                    } else {
                        currentLP2 = 999999;
                    }
                    break;
                case "2-":
                    if (currentLP2 - value > 0) {
                        currentLP2 -= value;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerTwoNameString;
                        displayToastAndSendToLog();
                    } else {
                        currentLP2 = 0;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerTwoNameString;
                        displayToastAndSendToLog();
                    }
                    break;
            }
            resetNumberHolder();
            animateTextView(previousLP1, currentLP1, playerOneLP);
            animateTextView(previousLP2, currentLP2, playerTwoLP);
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void reset(){
        LogFragment.lastDuelMaxTurns = turnNumber;
        currentLP1 = defaultLP;
        currentLP2 = defaultLP;
        previousLP1 = defaultLP;
        previousLP2 = defaultLP;
        turnNumber = 1;
        numberHolder.setText("");
        qcResultHolder.setText("");
        qcWorkHolder.setText("");
        playerOneLP.setText(defaultLP.toString());
        playerTwoLP.setText(defaultLP.toString());
        String temp = resources.getText(R.string.turn).toString() + turnNumber;
        turnButton.setText(temp);
        diceButton.setText(R.string.diceRoll);
        coinButton.setText(R.string.coinFlip);
        qcResultString = "";
        qcWorkString = "";
        if(!firstRun) {
            LogFragment.resetLog();
        }
        firstRun = false;
        /*MainActivity.app_launched(ourContext);*/
    }

    public static void addToNumberHolder(View view){
        if(numberHolderString.length()<5){
            numberHolderString += view.getTag();
            numberHolderNumber = Integer.parseInt(numberHolderString);
            CalcFragment.setNumberHolderText(numberHolderString);
        }

        if(numberHolderString.length() > 5){
            numberHolderString = "99999";
            numberHolderNumber = 99999;
            CalcFragment.setNumberHolderText(numberHolderString);
        }
    }

    public static void resetNumberHolder(){
        numberHolderNumber = 0;
        numberHolderString = "";
        CalcFragment.setNumberHolderText(numberHolderString);
    }

    //Animates the transition between numbers when calculation is performed
    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        if(initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            valueAnimator.setDuration(numberTransitionDuration);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    textview.setText(valueAnimator.getAnimatedValue().toString());
                }
            });
            valueAnimator.start();
        }

    }

    public static void displayToastAndSendToLog() {
        lpToast = Toast.makeText(ourContext, toastText, Toast.LENGTH_SHORT);
        lpToast.show();
        LogFragment.addDataToSection(turnNumber, toastText);
    }

    public static void qcAddToWorkHolder(View view){
        if(justPressedOpeator){
            qcWorkString = "";
            qcWorkHolder.setText("");
        }
        justPressedOpeator = false;
        String tag = view.getTag().toString();
        String temp = "";
        Integer tagSizeAppended = tag.toString().length() + qcWorkHolder.getText().toString().length();
        if(tagSizeAppended < 15) {
            qcWorkString += tag;
            qcWorkHolder.append(tag);
        }

    }

    public static void qcResetHolder(View view) {
        if(qcWorkHolder.getText().toString().equals("")){
            qcResultHolder.setText("");
            qcResultString = "";
        } else{
            qcWorkHolder.setText("");
            qcWorkString = "";

            qcResultHolder.setText("");
            qcResultString = "";
        }
    }

    public static void qcShow() {
        qcHolderView.setVisibility(View.VISIBLE);
    }

    public static void qcHide() {
        qcHolderView.setVisibility(View.GONE);
    }

    public static void qcOperators(View view) {
        lastButtonPressed = view.getTag().toString();
        //Don't add another operator to the string if the user just added one
        if(justPressedOpeator){
            return;
        }
        justPressedOpeator = true;
        String answer;
        String tag = view.getTag().toString();
        if (tag.equals(ourContext.getString(R.string.x))) {
            tag = "*"; //Didn't want to use * for the button text
        }
        if (tag.equals("=")) {
            qcResultString += qcWorkString;
            try {
                answer = evaluator.evaluate(qcResultString);
                answer = unDecimalizeIntegers(answer);
                qcWorkString = answer;
                qcWorkHolder.setText(answer);
                qcResultHolder.setText("");
                qcResultString = "";
            } catch (EvaluationException e) {
                e.printStackTrace();
            }
            lastButtonPressed = "=";

        } else {
            if (qcResultString.equals("")) {
                qcResultString += qcWorkString + tag;
                qcResultHolder.setText(qcResultString);
            } else if (!qcResultString.equals("")) {
                qcResultString = qcResultString + qcWorkString + tag;
                qcResultHolder.setText(qcResultString);
                String temp = qcResultString.substring(0, qcResultString.length() - 1);
                answer = "";
                try {
                    answer = evaluator.evaluate(temp);
                } catch (EvaluationException e) {
                    e.printStackTrace();
                }
                answer = unDecimalizeIntegers(answer);
                qcWorkString = answer;
                qcWorkHolder.setText(answer);
            }
            qcWorkString = "";
        }
    }

    public static String unDecimalizeIntegers(String input){
        Double inputDub = Double.parseDouble(input);
        String decString = inputDub.toString();
        decString = decString.substring(decString.indexOf("."), decString.length());

        if(checkIfHasDecimal(input)) {
            double moduloed = inputDub % 1;
            if((moduloed == 0.0d)){
                String output = removeDecimalPart(input);
                return output;
            }
        }
        return input;
    }

    public static boolean checkIfHasDecimal(String input){
        return input.contains(".");
    }

    public static String removeDecimalPart(String input){
        int inputLength = input.length();
        String temp = input.substring(0, inputLength - 2);
        return temp;
    }
    /*TODO: Create a tutorial, can probably do it by linking a bunch of showcase views together
    since the ShowcaseViews class was apparently removed...  Really like the effect though
    Should be able to make it work just fine
    This should probably be one of the final things to actually do for the app*/
    public void showTutorial(View targetView, int i){
        ViewTarget target = new ViewTarget(targetView);
        final ShowcaseView showcaseView = new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Clear this number by tapping it")
                .setContentText("")//NON-NLS
                .setStyle(R.style.CustomShowcaseTheme)
                .hideOnTouchOutside()
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showTutorialTurnButton();
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();
        showcaseView.show();
    }

    public void showTutorialTurnButton(){
        ViewTarget target = new ViewTarget(turnButton);
        final ShowcaseView showcaseView = new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Tap to increment the turn, long press to decrement the turn")
                .setContentText("")
                .setStyle(R.style.CustomShowcaseTheme)
                .hideOnTouchOutside()
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showTutorialOverFlow();
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();
        showcaseView.show();
    }

    public void showTutorialOverFlow(){
        ViewTarget target = new ViewTarget(appBarLayout.findViewById(R.id.overFlowButton));
        final ShowcaseView showcaseView = new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Press the menu button to find the Reset, QuickCalc, and Settings buttons")
                .setContentText("")
                .setStyle(R.style.CustomShowcaseTheme)
                .hideOnTouchOutside()
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();
        showcaseView.show();

    }

    public static void amoledBlackToggle(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisView.getContext().getApplicationContext());
        boolean on = preferences.getBoolean(MainActivity.KEY_AMOLED_BLACK, false);
        if(on) {
            thisView.findViewById(R.id.holderQC).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.holderBottom).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.holderholder).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.holderTop).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.adHolder).setBackgroundColor(resources.getColor(R.color.a_material_black));
            appBarLayout.findViewById(R.id.tabs).setBackgroundColor(resources.getColor(R.color.a_material_black));
        }else if(!on){
            thisView.findViewById(R.id.holderQC).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            thisView.findViewById(R.id.holderBottom).setBackgroundColor(resources.getColor(R.color.a_material_dark_tinted_dark));
            thisView.findViewById(R.id.holderholder).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            thisView.findViewById(R.id.holderTop).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            thisView.findViewById(R.id.adHolder).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            appBarLayout.findViewById(R.id.tabs).setBackgroundColor(resources.getColor(R.color.a_material_dark));
        }
    }


}



