package com.example.dicer;

import android.widget.ImageView;

import java.io.Serializable;

public class Dice implements Serializable {
    private int number;
    private ImageView img;

    public Dice(int number, ImageView img){
        this.number = number;
        this.img = img;
    }

    public ImageView getImage(){
        return img;
    }
    public int getNumber(){
        return number;
    }
}
