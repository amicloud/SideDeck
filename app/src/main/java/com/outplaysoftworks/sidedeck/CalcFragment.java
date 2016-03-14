package com.outplaysoftworks.sidedeck;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.sourceforge.jeval.*;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
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

    static TextView qcWorkHolder;
    static TextView qcResultHolder;

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
    static boolean justPressedOppeator = false;

    static String qcWorkString = "";
    static String qcResultString = "";
    static Evaluator evaluator = new Evaluator();
    static RelativeLayout qcHolderView;
    static Resources resources;

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
        assignVariableIds(view);
        makeListeners();
        reset();
        thisView = view;
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(view.getContext(), R.raw.lpcountersound, 1);

        //return that view
        playerTwoName.setText(CalcFragment.playerTwoNameString);
        playerOneName.setText(CalcFragment.playerOneNameString);

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
        /*System.out.println(temp);*/
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

    public static void qcAddToWorkHolder(View view){
        if(justPressedOppeator){
            qcWorkString = "";
            qcWorkHolder.setText("");
        }
        justPressedOppeator = false;
        String tag = view.getTag().toString();
        String temp = "";
        Integer tagSizeAppended = tag.toString().length() + qcWorkHolder.getText().toString().length();
        if(tagSizeAppended < 15) {
            qcWorkString += tag;
            qcWorkHolder.append(tag);
            //System.out.println("String:" + qcWorkString);
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
        /*System.out.println("String:" + qcWorkString);*/
    }

    //TODO: Make qcShow and qcHide
    public static void qcShow() {
        qcHolderView.setVisibility(View.VISIBLE);
    }

    public static void qcHide() {
        qcHolderView.setVisibility(View.GONE);
    }

    public static void qcOpperators(View view) {
        justPressedOppeator = true;
        String answer;
        String tag = view.getTag().toString();
        if(tag.equals(ourContext.getString(R.string.x))){
            tag = "*";
        }
        /*System.out.println("\nDebug 0");*/
        if(tag.equals("=")){
            qcResultString += qcWorkString;
            try {
                answer = evaluator.evaluate(qcResultString);
                qcWorkString = answer;
                qcWorkHolder.setText(answer);
                qcResultHolder.setText("");
                qcResultString = "";
            } catch (EvaluationException e) {
                e.printStackTrace();
            }
        }else {
            if (qcResultString.equals("")) {
                /*System.out.println("\nDebug 1");*/
                qcResultString += qcWorkString + tag;
                qcResultHolder.setText(qcResultString);
            } else if (!qcResultString.equals("")) {
                /*System.out.println("\nDebug 2");*/
                qcResultString = qcResultString
                        + qcWorkString + tag;
                qcResultHolder.setText(qcResultString);
                String temp = qcResultString.substring(0, qcResultString.length() - 2);
                answer = "";
                try {
                    answer = evaluator.evaluate(temp);
                } catch (EvaluationException e) {
                    e.printStackTrace();
                }
                qcWorkString = answer;
                qcWorkHolder.setText(answer);
            }
            qcWorkString = "";
        }
    }
}

