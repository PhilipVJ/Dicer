package com.example.dicer;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;

public class ImageFactory {


    public static ImageView getImageViewFromInt(int i, Context context, DiceSize screen) {
        ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        int orientation = context.getResources().getConfiguration().orientation;
        if(screen == DiceSize.SHAKESCREEN) {
            if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                imageView.setMaxHeight(Utility.getScreenHeight() / 6);
            }
            else{
                imageView.setMaxHeight(Utility.getScreenHeight() / 4);
            }
        }
        else if(screen == DiceSize.HISTORYSCREEN){
            imageView.setMaxHeight(80);
        }
        switch (i) {
            case 1:
                imageView.setImageResource(R.drawable.dice1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.dice2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.dice3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.dice4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.dice5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.dice6);
                break;
        }
        return imageView;
    }
}
