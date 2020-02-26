package com.example.dicer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private final int CANCEL = 0;
    private final int UPDATE = 1;
    private Context context;
    private Settings sentInSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (DiceService.getInstance().getLocale() != null) {
            Utility.changeLocale(this, DiceService.getInstance().getLocale());
        }
        setContentView(R.layout.settings);
        context = this;
        getSupportActionBar().hide();
        sentInSettings = (Settings) getIntent().getSerializableExtra("settings");

        if(savedInstanceState != null && savedInstanceState.containsKey("showHiddenInfo") && savedInstanceState.getBoolean("showHiddenInfo") == true){
            findViewById(R.id.chanceInput).setVisibility(View.VISIBLE);
           findViewById(R.id.chanceLabel).setVisibility(View.VISIBLE);
        }
        ImageView secretImgView = findViewById(R.id.secretBtn);
        CheckBox shakeBtn = findViewById(R.id.shakeCheckBox);
        CheckBox darkBtn = findViewById(R.id.darkThemeCheckBox);
        CheckBox activateEnglish = findViewById(R.id.activateEnglishCheckBox);
        activateEnglish.setChecked(sentInSettings.isActivateEnglish());
        EditText sixerChance = findViewById(R.id.chanceInput);
        if (sentInSettings.getSixerChanceMode()) {
            sixerChance.setText("" + sentInSettings.getChanceOfSixes());
        }
        else{
            sixerChance.setText("16");
        }

        secretImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView sixerChanceLabel = findViewById(R.id.chanceLabel);
                EditText sixerChance = findViewById(R.id.chanceInput);

                if (sixerChance.getVisibility() == View.INVISIBLE) {
                    sixerChance.setVisibility(View.VISIBLE);
                    sixerChanceLabel.setVisibility(View.VISIBLE);
                } else {
                    sixerChance.setVisibility(View.INVISIBLE);
                    sixerChanceLabel.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Add listeners
        darkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                   if (isChecked) {
                                                       findViewById(R.id.settingsRootLayout).setBackgroundColor(DiceService.DARKMODE);
                                                   } else {
                                                       findViewById(R.id.settingsRootLayout).setBackgroundColor(DiceService.NOTDARKMODE);
                                                   }
                                               }
                                           }
        );

        // Add listeners
        activateEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           Utility.makeToast(context, getString(R.string.changesSoon));
                                                       }
                                                   }
        );

        shakeBtn.setChecked(sentInSettings.getShakeMode());
        darkBtn.setChecked(sentInSettings.getDarkMode());


        if (sentInSettings.getDarkMode()) {
            findViewById(R.id.settingsRootLayout).setBackgroundColor(DiceService.DARKMODE);
        }

        Button cancelBtn = findViewById(R.id.cancelSettingsBtn);
        Button saveBtn = findViewById(R.id.saveSettingsBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox shakeBtn = findViewById(R.id.shakeCheckBox);
                CheckBox darkBtn = findViewById(R.id.darkThemeCheckBox);
                CheckBox activateEnglish = findViewById(R.id.activateEnglishCheckBox);
                boolean shakeEnabled = shakeBtn.isChecked();
                boolean darkEnabled = darkBtn.isChecked();
                boolean englishEnabled = activateEnglish.isChecked();

                if (activateEnglish.isChecked()) {
                    DiceService.getInstance().changeLanguage(Language.ENGLISH, getBaseContext());
                } else {
                    DiceService.getInstance().changeLanguage(Language.DANISH, getBaseContext());
                }

                EditText sixerChance = findViewById(R.id.chanceInput);
                int chanceOfSixes = 0;
                boolean activateCheatMode = false;
                if (sixerChance.getVisibility() == View.VISIBLE) {
                    try {
                        int inputInt = Integer.parseInt(sixerChance.getText().toString());
                        if (inputInt < 0 || inputInt > 100) {
                            Utility.makeToast(context, getString(R.string.wringChance));
                            return;
                        } else {
                            activateCheatMode = true;
                            chanceOfSixes = inputInt;
                        }
                    } catch (Exception ex) {
                        Utility.makeToast(context, getString(R.string.wringChance));
                        return;
                    }
                }

                Settings settings = (Settings) getIntent().getSerializableExtra("settings");
                settings.setDarkMode(darkEnabled);
                settings.setShakeMade(shakeEnabled);
                settings.setActivateEnglish(englishEnabled);
                // Activate cheat mode
                if(sixerChance.getVisibility() == View.VISIBLE) {
                    settings.setChanceOfSixes(chanceOfSixes);
                    settings.setSixerChanceMode(activateCheatMode);
                }
                else{ // Reuse old settings
                    settings.setChanceOfSixes(sentInSettings.getChanceOfSixes());
                    settings.setSixerChanceMode(sentInSettings.getSixerChanceMode());
                }

                Intent data = new Intent();
                data.putExtra("settings", settings);
                setResult(UPDATE, data);
                finish();

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("info", "Cancelled");
                setResult(CANCEL, data);
                finish();
            }
        });
    }


    public void onBackPressed() {
        Utility.writeLog("Closing settings screen");
        finish();
    }

    protected void onSaveInstanceState(Bundle save)
    {
        boolean isShowing = false;
        if(findViewById(R.id.chanceInput).getVisibility() == View.VISIBLE){
            isShowing = true;
        }
        save.putBoolean("showHiddenInfo",isShowing);
        super.onSaveInstanceState(save);
    }
}
