package com.outplaysoftworks.sidedeck;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import net.sourceforge.jeval.*;
import net.sourceforge.jeval.function.math.Log;

import java.util.ArrayList;
import java.util.Random;

import me.grantland.widget.AutofitHelper;

public class CalcFragment extends Fragment {

    static String playerOneNameString = "";
    static final Handler coinHandler = new Handler();
    static final Handler diceHandler = new Handler();

    private static Integer diceRollAnimationDuration = 2000; //In milliseconds
    private static Integer diceRollAnimationFrameCount = 5;
    //Drawable stuff
    private static ArrayList<Drawable> diceDrawables = new ArrayList<>();
    private static Drawable diceRollBackgroundDrawable;

    public static void setPlayerTwoNameString(String playerTwoNameString) {
        CalcFragment.playerTwoNameString = playerTwoNameString;
    }

    public static void setPlayerOneNameString(String playerOneNameString) {
        CalcFragment.playerOneNameString = playerOneNameString;
    }

    static String playerTwoNameString = "";

    public static boolean isSoundOn() {
        return soundOn;
    }

    public static void setSoundOn(boolean soundOn) {
        CalcFragment.soundOn = soundOn;
    }

    static boolean soundOn;
    static TextView numberHolder;
    static TextView playerOneLP;
    static TextView playerTwoLP;
    static Button turnButton;

    public static String getPlayerOneNameString() {
        return playerOneNameString;
    }

    public static String getPlayerTwoNameString() {
        return playerTwoNameString;
    }

    public static EditText getPlayerOneName() {
        return playerOneName;
    }

    public static EditText getPlayerTwoName() {
        return playerTwoName;
    }
    private static Activity activity;
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

    private static Integer numberTransitionDuration = 1050;
    private static Integer numberTransitionDurationShort = numberTransitionDuration/2;

    public static void setDefaultLP(Integer defaultLP) {
        CalcFragment.defaultLP = defaultLP;
    }

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
    static Integer diceRollSoundId;
    static Integer coinFlipSoundId;
    public CalcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calc, container, false);
        resources = view.getContext().getResources();
        activity = getActivity();
        //Assign view variables after inflation is done
        parentView = container;
        assignVariableIds(view);
        makeListeners();
        loadDrawables();
        reset();
        thisView = view;
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(view.getContext(), R.raw.lpcountersound, 1);
        diceRollSoundId = soundPool.load(view.getContext(), R.raw.dicerollsound, 1);
        coinFlipSoundId = soundPool.load(view.getContext(), R.raw.coinflipsound, 1);

        playerOneName.setText(getPlayerOneNameString());
        playerTwoName.setText(getPlayerTwoNameString());
        AutofitHelper.create(playerOneName);
        AutofitHelper.create(playerTwoName);
        AutofitHelper.create(qcWorkHolder);
        AutofitHelper.create(qcResultHolder);
        //AdView mAdView = (AdView) view.findViewById(R.id.adView1);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        tutorial();
        amoledBlackToggle();
        return view;
    }

    private void loadDrawables() {
        diceDrawables.add(resources.getDrawable(R.drawable.dice_1));
        diceDrawables.add(resources.getDrawable(R.drawable.dice_2));
        diceDrawables.add(resources.getDrawable(R.drawable.dice_3));
        diceDrawables.add(resources.getDrawable(R.drawable.dice_4));
        diceDrawables.add(resources.getDrawable(R.drawable.dice_5));
        diceDrawables.add(resources.getDrawable(R.drawable.dice_6));
        diceRollBackgroundDrawable = diceButton.getBackground();
    }

    private void makeListeners() {
        playerOneName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = playerOneName.getText().toString();
                playerOneNameString = temp;
            }
        });
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

        playerTwoName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = playerTwoName.getText().toString();
                playerTwoNameString = temp;
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
                    //LogFragment.deleteTurnOnDecrementTurnIfEmpty();
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
        if(soundOn) {
            soundPool.play(diceRollSoundId, 0.6f, 0.6f, 1, 0, 1);
        }
        Integer temp = random.nextInt(6);
        diceButton.setText("");
        //temp++;//Can't roll a zero
        //drawDiceButton(temp);
        makeDiceRoll(temp);
    }

    private static void makeDiceRoll(final int lastDiceRoll){
        diceButton.setClickable(false);
        final Drawable originalBackgroundDrawable = diceRollBackgroundDrawable;
        RandomAnimationBuilder randomAnimationBuilder = new RandomAnimationBuilder(diceDrawables,
                diceRollAnimationDuration, diceRollAnimationFrameCount);
        AnimationDrawable animation = randomAnimationBuilder.makeAnimation();
        diceButton.setBackground(animation);
        diceButton.setText("");
        animation.setEnterFadeDuration(randomAnimationBuilder.getFrameDuration()/2);
        animation.setExitFadeDuration(randomAnimationBuilder.getFrameDuration()/2);
        animation.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                diceButton.setAlpha(1f);
                diceButton.setBackground(diceDrawables.get(lastDiceRoll));
                diceButton.setClickable(true);
            }
        }, diceRollAnimationDuration + (randomAnimationBuilder.getFrameDuration()*2));
        resetDiceRollButtonAfterDelay(originalBackgroundDrawable);
    }

    private static void resetDiceRollButtonAfterDelay(final Drawable originalBackground){
        diceHandler.removeCallbacksAndMessages(null);
        diceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                diceButton.setText(resources.getString(R.string.diceRoll));
                diceButton.setBackground(originalBackground);
            }
        }, diceRollAnimationDuration + 6000);
    }

    private static void resetCoinFlipAfterDelay(){
        coinHandler.removeCallbacksAndMessages(null);
        coinHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                coinButton.setText(ourContext.getString(R.string.coinFlip));
            }
        }, 8000);

    }

    private static Drawable getScaledPng(int resource, Button button){
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resource);
        bitmap = Bitmap.createScaledBitmap(bitmap, button.getWidth(), button.getHeight(), true);
        Drawable drawable = new BitmapDrawable(resources, bitmap);
        return drawable;
    }
    public static int currentFrame;
    public static int getCurrentFrame(){
        return currentFrame;
    }
    public static void setCurrentFrame(int cf){ currentFrame = cf; }
    public static void coinFlip(){
        currentFrame = 0;
        if(soundOn){
            soundPool.play(coinFlipSoundId, 1, 1, 1, 0, 1);
        }
        coinButton.setClickable(false);
        final int frames = 5;
        Double temp = Math.random();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getCurrentFrame() < frames) {
                    Coin coin = makeCoin();
                    coinButton.setText(coin.getFace().toString());
                    setCurrentFrame(getCurrentFrame() + 1);
                    handler.postDelayed(this, 400);
                }
                if(frames == getCurrentFrame()) {
                    resetCoinFlipAfterDelay();
                    coinButton.setClickable(true);
                }
            }
        },400);
    }

    private static Coin makeCoin(){
        Double toss = Math.random();
        Coin coin = new Coin(toss, ourContext);
        return coin;
    }

    public static void setNumberHolderText(String value, String end, TextView textView){
        if (value.equals("")) value = "0";
        if (end.equals("")) end = "0";
        numberHolder.setText(value);
    }


    public static void modLP(String tag){
        Integer value = numberHolderNumber;
        String damage = numberHolderString;
        previousLP1 = currentLP1;
        previousLP2 = currentLP2;
        //I honestly hate this whole block of code right here but it works for now I guess.  Is dumb
        if(value != 0) {
            if(soundOn)soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
            switch (tag) {
                case "1+":
                    if (currentLP1 + value < 999999) {
                        damage = "+" + damage;
                        currentLP1 += value;
                        toastText = value + resources.getText(R.string.lpAddedTo).toString() + playerOneNameString;
                        makeToast();
                        sendToLog(currentLP1.toString(), damage, true, false);
                    } else {
                        currentLP1 = 999999;
                    }
                    break;
                case "1-":
                    if (currentLP1 - value > 0) {
                        damage = "-" + damage;
                        currentLP1 -= value;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerOneNameString;
                        makeToast();
                        sendToLog(currentLP1.toString(), damage, true, true);
                    } else {
                        damage = "-" + damage;
                        currentLP1 = 0;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerOneNameString;
                        makeToast();
                        sendToLog(currentLP1.toString(), damage, true, true);
                    }
                    break;
                case "2+":
                    if (currentLP2 + value < 999999) {
                        damage = "+" + damage;
                        currentLP2 += value;
                        toastText = value + resources.getText(R.string.lpAddedTo).toString() + playerTwoNameString;
                        makeToast();
                        sendToLog(currentLP2.toString(), damage, false, false);
                    } else {
                        currentLP2 = 999999;
                    }
                    break;
                case "2-":
                    if (currentLP2 - value > 0) {
                        damage = "-" + damage;
                        currentLP2 -= value;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerTwoNameString;
                        makeToast();
                        sendToLog(currentLP2.toString(), damage, false, true);
                    } else {
                        damage = "-" + damage;
                        currentLP2 = 0;
                        toastText = value + resources.getText(R.string.lpSubtracedFrom).toString() + playerTwoNameString;
                        makeToast();
                        sendToLog(currentLP2.toString(), damage, false, true);
                    }
                    break;
            }
            resetNumberHolder();
            animateTextView(previousLP1, currentLP1, playerOneLP, numberTransitionDuration);
            animateTextView(previousLP2, currentLP2, playerTwoLP, numberTransitionDuration);
        }
    }

    private static void sendToLog(String previousLP1, String damage, boolean b, boolean isDamage) {
        LogFragment.addDataToSection(previousLP1, damage, b, isDamage);
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
        diceButton.setBackground(diceRollBackgroundDrawable);
        diceButton.setText(R.string.diceRoll);
        coinButton.setText(R.string.coinFlip);
        qcResultString = "";
        qcWorkString = "";
        if(!firstRun) {
            LogFragment.resetLog();
        }
        firstRun = false;
    }

    public static void addToNumberHolder(View view){
        String temp = numberHolderString;
        if(numberHolderString.length()<5){
            numberHolderString += view.getTag();
            numberHolderNumber = Integer.parseInt(numberHolderString);
            CalcFragment.setNumberHolderText(numberHolderString, temp, numberHolder);
        }

        if(numberHolderString.length() > 5){
            numberHolderString = "99999";
            numberHolderNumber = 99999;
            CalcFragment.setNumberHolderText(numberHolderString, temp, numberHolder);
        }
    }

    public static void resetNumberHolder(){
        numberHolderNumber = 0;
        numberHolderString = "";
        CalcFragment.numberHolder.setText("");
    }

    //Animates the transition between numbers when calculation is performed
    public static void animateTextView(int initialValue, int finalValue, final TextView textview, int duration) {
        if(initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            valueAnimator.setDuration(duration);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    textview.setText(valueAnimator.getAnimatedValue().toString());
                }
            });
            valueAnimator.start();
        }
    }



    public static void makeToast() {
        lpToast = Toast.makeText(ourContext, toastText, Toast.LENGTH_SHORT);
        lpToast.show();
        //LogFragment.addDataToSection(turnNumber, toastText);
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

    private static void tutorial(){
        long count = getLaunchNumber();
        if(count < 2){
            showTutorial();
        }
    }

    private static long getLaunchNumber(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ourContext.getApplicationContext());
        long launch_count = preferences.getLong(Constants.KEY_LAUNCH_COUNT, 0l);
        return launch_count;
    }

    public static void showTutorial(){
        ViewTarget target = new ViewTarget(numberHolder);
        numberHolder.setText("2000"); //NON-NLS
        final ShowcaseView showcaseView = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle(activity.getString(R.string.TutorialMessage1))
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

    private static void showTutorialTurnButton(){
        ViewTarget target = new ViewTarget(turnButton);
        final ShowcaseView showcaseView = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle(activity.getString(R.string.TutorialMessage2))
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

    private static void showTutorialOverFlow(){
        ViewTarget target = new ViewTarget(appBarLayout.findViewById(R.id.overFlowButton));
        final ShowcaseView showcaseView = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle(activity.getString(R.string.TutorialMessage3))
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
        CalcFragment.reset();
        LogFragment.resetLog();
    }

    public static void amoledBlackToggle(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisView.getContext().getApplicationContext());
        boolean on = preferences.getBoolean(Constants.KEY_AMOLED_BLACK, false);
        if(on) {
            thisView.findViewById(R.id.holderQC).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.holderBottom).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.holderholder).setBackgroundColor(resources.getColor(R.color.a_material_black));
            thisView.findViewById(R.id.holderTop).setBackgroundColor(resources.getColor(R.color.a_material_black));
            //thisView.findViewById(R.id.adHolder).setBackgroundColor(resources.getColor(R.color.a_material_black));
            appBarLayout.findViewById(R.id.tabs).setBackgroundColor(resources.getColor(R.color.a_material_black));
        }else if(!on){
            thisView.findViewById(R.id.holderQC).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            thisView.findViewById(R.id.holderBottom).setBackgroundColor(resources.getColor(R.color.a_material_dark_tinted_dark));
            thisView.findViewById(R.id.holderholder).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            thisView.findViewById(R.id.holderTop).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            //thisView.findViewById(R.id.adHolder).setBackgroundColor(resources.getColor(R.color.a_material_dark));
            appBarLayout.findViewById(R.id.tabs).setBackgroundColor(resources.getColor(R.color.a_material_dark));
        }
    }

}



