package com.outplaysoftworks.sidedeck;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {
    public static Integer currentTurn;
    public static View view;
    public static Integer lastDuelMaxTurns = 0;
    static Resources resources;
    private static LinearLayout myLayout;
    private static ArrayList<LinearLayout> data = new ArrayList<LinearLayout>();
    private static ArrayList<LinearLayout> sections = new ArrayList<LinearLayout>();
    private static LayoutInflater ourInflater;
    private TextView p1Name;
    private TextView p2Name;

    public LogFragment() {
        // Required empty public constructor
    }

    public static void init() {
        currentTurn = 1;
        LinearLayout layout = createNewSection(currentTurn);
        myLayout.addView(layout, 0);
    }

    public static void resetLog() {
        sections.clear();
        sections.trimToSize();
        data.clear();
        data.trimToSize();
        myLayout.removeAllViews();
        init();
    }


    private static LinearLayout createNewSection(int turn) {
        LinearLayout mainView = new LinearLayout(view.getContext());
        View temp = ourInflater.inflate(R.layout.section, mainView, false);
        mainView = (LinearLayout) temp;
        TextView turnLabel = (TextView) mainView.findViewById(R.id.turnLabel);
        turnLabel.setText(view.getResources().getText(R.string.logTurn) + " " + currentTurn.toString());
        sections.add(mainView);
        return mainView;
    }

    public static void addSection() {
        try {
            if (sections.get(getCurrentSection()) != null) {
                return;
            }
        } catch (Exception e) {

        }
        LinearLayout layout = createNewSection(currentTurn);
        myLayout.addView(layout, 0);
    }

    private static int getCurrentSection() {
        int t = currentTurn - 1;
        return t;
    }

    private static View createNewData(String lp, String lpChange, boolean player1, boolean isDamage) {
        LinearLayout mainView = new LinearLayout(view.getContext());
        LinearLayout temp = (LinearLayout) ourInflater.inflate(R.layout.data, mainView, false);
        TextView playerName = (TextView) temp.findViewById(R.id.playerName);
        AutofitHelper.create(playerName);
        TextView lpDifference = (TextView) temp.findViewById(R.id.lpDifference);
        TextView lpAfter = (TextView) temp.findViewById(R.id.lpAfter);
        String player = "";
        lpDifference.setText(lpChange);
        lpAfter.setText(lp);

        if (isDamage) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.a_material_red));
        } else if (!isDamage) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.a_material_green));
        }
        if (player1) {
            player = CalcFragment.getPlayerOneNameString();
        } else if (!player1) {
            player = CalcFragment.getPlayerTwoNameString();
        }
        playerName.setText(player);
        data.add(temp);
        return temp;
    }

    public static void addDataToSection(String lp, String damage, boolean player1, boolean isDamage) {
        View newData = createNewData(lp, damage, player1, isDamage);
        if (sections.get(getCurrentSection()).getChildCount() != 1) {
            //Add horizontal rule
            RelativeLayout horizontalRule = new RelativeLayout(view.getContext());
            View temp = ourInflater.inflate(R.layout.log_horizontal_rule, horizontalRule, false);
            horizontalRule = (RelativeLayout) temp;
            sections.get(getCurrentSection()).addView(horizontalRule, 1);
        }
        sections.get(getCurrentSection()).addView(newData, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_log, container, false);
        myLayout = (LinearLayout) view.findViewById(R.id.viewHolder);
        ourInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resources = view.getContext().getResources();
        init();

        //AdView mAdView = (AdView) view.findViewById(R.id.adView2);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        return view;
    }


    public static void amoledBlackToggle() {

    }
}
