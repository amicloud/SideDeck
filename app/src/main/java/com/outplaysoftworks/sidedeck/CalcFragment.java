package com.outplaysoftworks.sidedeck;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


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

    static Integer numberTransitionDuration = 1050;
    public static Integer defaultLP = 8000;
    static Integer turnNumber = 0;
    static Integer currentLP1 = defaultLP;
    static Integer previousLP1;
    static Integer currentLP2 = defaultLP;
    static Integer previousLP2;

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
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(view.getContext(), R.raw.lpcountersound, 1);


        //return that view
        return view;
    }

    private void makeListeners(){
        playerOneName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
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
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    v.clearFocus();
                    hideKeyboardFrom(getContext(), v);
                    return true;
                }
                return false;
            }
        });


    }

    public static void setNumberHolderText(String value){
        numberHolder.setText(value);
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

    //Changes LP
    public static void modLP(String tag, int value){
        previousLP1 = currentLP1;
        previousLP2 = currentLP2;
        String toastText = "";
        if(value != 0){
            soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
        }
        switch (tag){
            case "1+":
                if(currentLP1 + value < 999999){
                    currentLP1 += value;
                    toastText = "Added " + value +" LP to " + playerOneName.getText().toString() ;
                }else
                    currentLP1 = 999999;
                break;
            case "1-":
                if(currentLP1 - value > 0)
                currentLP1 -= value;
                else
                    currentLP1 = 0;
                break;
            case "2+":
                if(currentLP2 + value < 999999)
                    currentLP2 += value;
                else
                    currentLP2 = 999999;
                break;
            case "2-":
                if(currentLP2 - value > 0)
                    currentLP2 -= value;
                else
                    currentLP2 = 0;
                break;
        }
        //TODO: Create more toasts
        lpToast = Toast.makeText(ourContext, toastText, Toast.LENGTH_SHORT);
        lpToast.show();
        animateTextView(previousLP1, currentLP1, playerOneLP);
        animateTextView(previousLP2, currentLP2, playerTwoLP);

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

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
