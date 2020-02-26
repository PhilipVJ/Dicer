package com.example.dicer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Throw implements Serializable {
    public ArrayList<Dice> dices;
    public Calendar timeStamp;

    public Throw(ArrayList<Dice> dices, Calendar timeStamp){
        this.dices = dices;
        this.timeStamp = timeStamp;
    }

    public ArrayList<Dice> getDices(){
        return dices;
    }

    public Calendar getTimeStamp(){
        return timeStamp;
    }

}
