package com.example.dicer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryAdapter extends ArrayAdapter<Throw> {
    private ArrayList<Throw> allThrows;
    private final int[] colours = {
            Color.parseColor("#FFC500"),
            Color.parseColor("#26B1FE")
    };

    public HistoryAdapter(Context context, int textViewResourceId,
                          ArrayList<Throw> allThrows) {
        super(context, textViewResourceId, allThrows);
        this.allThrows = allThrows;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if (v == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.cell, null);
        }
        else{
            // Reusing view
            LinearLayout layout = v.findViewById(R.id.dices);
            layout.removeAllViews();
        }

        v.setBackgroundColor(colours[position % colours.length]);
        Throw f = allThrows.get(position);

        TextView timeStamp = v.findViewById(R.id.timeStamp);
        LinearLayout dices = v.findViewById(R.id.dices);

        String minuteString = "";
        if(f.getTimeStamp().get(Calendar.MINUTE)<10){
            minuteString+=0;
        }
        minuteString+=""+f.getTimeStamp().get(Calendar.MINUTE);
        timeStamp.setText(f.getTimeStamp().get(Calendar.HOUR_OF_DAY)+":"+minuteString);
        timeStamp.setTextSize(Utility.getScreenWidth()/50);

        for (Dice curDice : f.getDices()) {
            dices.addView(ImageFactory.getImageViewFromInt(curDice.getNumber(), v.getContext(),DiceSize.HISTORYSCREEN));
        }

        return v;
    }
}
