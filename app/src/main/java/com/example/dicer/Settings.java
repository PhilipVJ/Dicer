package com.example.dicer;

import java.io.Serializable;
import java.util.Locale;

public class Settings implements Serializable {
    private boolean darkMode = false;
    private boolean shakeMode = false;
    private boolean activateEnglish = false;
    private boolean sixerMode = false;
    private int chanceOfSixes;

    public Settings(){
        if(Locale.getDefault().getLanguage() != "da"){
            activateEnglish = true;
        }
    }

    public int getChanceOfSixes() {
        return chanceOfSixes;
    }

    public void setChanceOfSixes(int chanceOfSixes) {
        this.chanceOfSixes = chanceOfSixes;
    }

    public boolean getSixerChanceMode() {return sixerMode; }

    public void setSixerChanceMode(boolean sixMode) {
        sixerMode = sixMode;
    }

    public boolean getDarkMode() {
        return darkMode;
    }

    public boolean getShakeMode() {
        return shakeMode;
    }

    public boolean isActivateEnglish() {
        return activateEnglish;
    }

    public void setActivateEnglish(boolean activateEnglish) {
        this.activateEnglish = activateEnglish;
    }

    public void setShakeMade(boolean shakeMode) {
        this.shakeMode = shakeMode;

    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
}
