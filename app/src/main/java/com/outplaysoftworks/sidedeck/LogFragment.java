package com.outplaysoftworks.sidedeck;


import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {
    public static Integer currentTurn;
    public static View view;
    public static LinearLayout[] sections = new LinearLayout[100];
    private static LinearLayout myLayout;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    public static Integer lastDuelMaxTurns = 0;

    public LogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_log, container, false);
        myLayout = (LinearLayout)view.findViewById(R.id.viewHolder);
        init();
        return view;
    }

    public static void init(){
        currentTurn = 1;
        addSection();
    }

    public static void addSection(){
        /*if(view.findViewById(currentTurn) == null) {*/
            sections[currentTurn] = new LinearLayout(view.getContext());
            sections[currentTurn].setId(currentTurn);
            sections[currentTurn].setOrientation(LinearLayout.VERTICAL);
            TextView turnLabel = new TextView(view.getContext());
            turnLabel.setTextColor(Color.WHITE);
            turnLabel.setText("Turn: " + currentTurn);
            turnLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            turnLabel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            sections[currentTurn].addView(turnLabel);
            myLayout.addView(sections[currentTurn], 0);
            sections[currentTurn].addView(new TextView(view.getContext()));
        /*}*/
    }

    public static void addDataToSection(int section, String text){
        TextView temp = (TextView)sections[section].getChildAt(1);
        String tempString = temp.getText().toString();
        tempString = text + "\n" + tempString;
        temp.setTextColor(Color.WHITE);
        temp.setText(tempString);
        System.out.print(tempString);
    }

    public static void resetLog(){
        currentTurn = 1;

        for(int i = 1; i < lastDuelMaxTurns + 1; i++){  //TODO Crahses here when run for first time but doesn't crash if run without "+ 1" and then run again what the fuck
            System.out.println(i);
            sections[i].removeAllViews();
        }
        addSection();
    }

}
