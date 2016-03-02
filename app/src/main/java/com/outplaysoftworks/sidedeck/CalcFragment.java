package com.outplaysoftworks.sidedeck;


import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
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

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalcFragment extends Fragment {

    static TextView numberHolder;
    static TextView playerOneLP;
    static TextView playerTwoLP;
    static Button turnButton;
    EditText playerOneName;
    EditText playerTwoName;

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
        numberHolder = (TextView)view.findViewById(R.id.numberHolder);
        playerOneLP = (TextView)view.findViewById(R.id.playerOneLP);
        playerOneLP.setText(defaultLP.toString());
        playerTwoLP = (TextView)view.findViewById(R.id.playerTwoLP);
        playerTwoLP.setText(defaultLP.toString());
        turnButton = (Button)view.findViewById(R.id.buttonTurn);
        playerOneName = (EditText)view.findViewById(R.id.playerOneName);
        playerTwoName = (EditText)view.findViewById(R.id.playerTwoName);



        //return that view

        return view;
    }

    public static void setNumberHolderText(String value){
        numberHolder.setText(value);
    }


    //Animates the transition between numbers when calculation is performed
    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        if(initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt((int) initialValue, (int) finalValue);
            valueAnimator.setDuration(1000);

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
        switch (tag){
            case "1+":
                if(currentLP1 + value < 999999)
                    currentLP1 += value;
                else
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
        animateTextView(previousLP1, currentLP1, playerOneLP);
        animateTextView(previousLP2, currentLP2, playerTwoLP);
    }

}
