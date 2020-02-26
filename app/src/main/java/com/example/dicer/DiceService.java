package com.example.dicer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class DiceService implements Serializable {

    private static DiceService service = null;
    private ArrayList<Throw> allThrows = new ArrayList<>();
    private Throw currentThrow;
    private int totalThrows = 0;
    private int chosenNumberOfDice = 0;
    private Settings settings = new Settings();
    private Locale currentLocale;

    public static final int DARKMODE = Color.parseColor("#312B2D");
    public static final int NOTDARKMODE = Color.parseColor("#FFFFFF");

    private DiceService(){

    }
    public static DiceService getInstance()
    {
        if (service == null)
            service = new DiceService();

        return service;
    }


    public Throw getCurrentThrow() {
        return currentThrow;
    }

    public void setCurrentThrow(Throw currentThrow) {
        this.currentThrow = currentThrow;
    }

    public void addThrow(Throw newThrow) {
        allThrows.add(newThrow);
    }

    public ArrayList<Throw> getThrows() {
        return allThrows;
    }

    public void removeThrows() {
        allThrows.clear();
    }

    public void setNumberOfDice(int dice) {
        chosenNumberOfDice = dice;
    }

    public int getNumberOfDice() {
        return chosenNumberOfDice;
    }

    public Settings getSettings() {
        return settings;
    }

    public void updateSettings(Settings settings) {
        this.settings = settings;
    }

    public void changeLanguage(Language language, Context context) {

        String lang = "";
        String country = "";
        switch (language) {
            case DANISH:
                lang = "da";
                country = "DK";
                break;
            case ENGLISH:
                lang = "en";
                country = "US";
        }

        Locale locale = new Locale(lang, country);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        currentLocale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public Locale getLocale(){
        return currentLocale;
    }
}

