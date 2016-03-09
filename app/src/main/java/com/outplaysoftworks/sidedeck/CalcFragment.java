package com.outplaysoftworks.sidedeck;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalcFragment extends Fragment {

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
    public static Boolean firstRun = true;

    public CalcFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calc, container, false);
        //Assign view variables after inflation is done
        assignVariableIds(view);
        makeListeners();
        reset();
        thisView = view;
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(view.getContext(), R.raw.lpcountersound, 1);

        //return that view
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
                turnButton.setText("Turn\n " + turnNumber);
                LogFragment.currentTurn = turnNumber;
                LogFragment.addSection();
            }
        });
        turnButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(turnNumber > 0) {
                    turnNumber--;
                    turnButton.setText("Turn\n " + turnNumber);
                    LogFragment.currentTurn = turnNumber;
                }
                return true;
            }
        });
    }

    //Assigns view variables with java ids
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
    }

    //Performs dice roll
    public static void diceRoll(){
        Integer temp = random.nextInt(6);
        temp++;//Can't roll a zero
        diceButton.setText(temp.toString());
        String diceRoll = thisView.getContext().getResources().getText(R.string.diceRoll).toString();
        LogFragment.addDataToSection(turnNumber, diceRoll + ": " + temp);
    }

    //Performs coin flip
    public static void coinFlip(){
        String coinflip = thisView.getContext().getResources().getText(R.string.coinFlip).toString() + ": ";
        Double temp = Math.random();
        /*System.out.println(temp);*/
        if(temp>0.5){
            coinButton.setText(R.string.coinHeads);
            LogFragment.addDataToSection(turnNumber, coinflip + thisView.getContext().getResources().getText(R.string.coinHeads).toString());
        }else if(temp<=0.5){
            coinButton.setText(R.string.coinsTails);
            LogFragment.addDataToSection(turnNumber, coinflip + thisView.getContext().getResources().getText(R.string.coinsTails).toString());
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
            soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
            switch (tag) {
                case "1+":
                    if (currentLP1 + value < 999999) {
                        currentLP1 += value;
                        toastText = value + " LP added to " + playerOneName.getText().toString();
                        displayToastAndSendToLog();
                    } else {
                        currentLP1 = 999999;
                    }
                    break;
                case "1-":
                    if (currentLP1 - value > 0) {
                        currentLP1 -= value;
                        toastText = value + " LP subtracted from " + playerOneName.getText().toString();
                        displayToastAndSendToLog();
                    } else {
                        currentLP1 = 0;
                        toastText = value + " LP subtracted from " + playerOneName.getText().toString();
                        displayToastAndSendToLog();
                    }
                    break;
                case "2+":
                    if (currentLP2 + value < 999999) {
                        currentLP2 += value;
                        toastText = value + " LP added to " + playerTwoName.getText().toString();
                        displayToastAndSendToLog();
                    } else {
                        currentLP2 = 999999;
                    }
                    break;
                case "2-":
                    if (currentLP2 - value > 0) {
                        currentLP2 -= value;
                        toastText = value + " LP subtracted from " + playerTwoName.getText().toString();
                        displayToastAndSendToLog();
                    } else {
                        currentLP2 = 0;
                        toastText = value + " LP subtracted from " + playerTwoName.getText().toString();
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
        playerOneLP.setText(defaultLP.toString());
        playerTwoLP.setText(defaultLP.toString());
        turnButton.setText("Turn\n " + turnNumber);
        diceButton.setText(R.string.diceRoll);
        coinButton.setText(R.string.coinFlip);
        System.out.println("Reset");
        if(firstRun == false) {
            LogFragment.resetLog();
        }
        firstRun = false;
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
        /*System.out.println(numberHolderNumber + ", string: " + numberHolderString);*/
    }

    public static void resetNumberHolder(){
        numberHolderNumber = 0;
        numberHolderString = "";
        CalcFragment.setNumberHolderText(numberHolderString);
    }

    //Animates the transition between numbers when calculation is performed
    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        if(initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt((int) initialValue, (int) finalValue);
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

    public static void sendDataToLog(){

    }

    public static void displayToastAndSendToLog(){
        lpToast = Toast.makeText(ourContext, toastText, Toast.LENGTH_SHORT);
        lpToast.show();
        LogFragment.addDataToSection(turnNumber,toastText);
    }


}

