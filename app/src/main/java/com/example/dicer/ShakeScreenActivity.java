package com.example.dicer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class ShakeScreenActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private final int SETTINGS = 0;
    private final int CANCEL = 0;
    private final int UPDATE = 1;

    private Throw recentThrow;
    private Calendar lastThrowTime;
    private Context context;


    private DiceService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.writeLog("Opening shake screen");
        service = DiceService.getInstance();
        if (service.getLocale() != null) {
            Utility.changeLocale(this, service.getLocale());
        }
        setContentView(R.layout.shakescreen);
        context = this;
        Button rollButton = findViewById(R.id.rollButton);
        rollButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                roll();
            }
        });

        Button historyButton = findViewById(R.id.historyBtn);
        historyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHistory();
            }
        });

        Button settingsButton = findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSettings();
            }
        });

        if (savedInstanceState == null) {
            int number = getIntent().getIntExtra("numberOfDices", 0);
            service.setNumberOfDice(number);
            makeDices();
        } else {
            Utility.writeLog("Saved instance loaded - viewing previous throw");
            recentThrow = service.getCurrentThrow();
            setupRecentThrow();
        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        updateGUI();
    }

    private void setupRecentThrow() {
        LinearLayout diceLayout = findViewById(R.id.diceView);
        LinearLayout rowLayout;
        ArrayList<Dice> tempDice = new ArrayList<>();
        int x = 0;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            for (int i = 0; i < recentThrow.dices.size(); i++) {
                x++;
                Dice dice = recentThrow.dices.get(i);
                tempDice.add(dice);
                boolean addToView = false;
                // Makes sure two dice are showed on each row
                if (x % 2 == 0) {
                    addToView = true;
                }
                if (i == service.getNumberOfDice() - 1) {
                    addToView = true;
                }

                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // If in landscape - all dices will be added to the same line
                    addToView = true;
                }

                if (addToView) {
                    rowLayout = new LinearLayout(this);
                    rowLayout.setGravity(Gravity.CENTER);
                    for (Dice curDice : tempDice) {
                        rowLayout.addView(ImageFactory.getImageViewFromInt(curDice.getNumber(), this, DiceSize.SHAKESCREEN));
                    }
                    diceLayout.addView(rowLayout);
                    tempDice.clear();
                }

            }
        } else {
            Utility.writeLog("Adding dice to landscape mode");
            rowLayout = new LinearLayout(this);
            rowLayout.setGravity(Gravity.CENTER);
            for (Dice curDice : recentThrow.dices) {
                rowLayout.addView(ImageFactory.getImageViewFromInt(curDice.getNumber(), this, DiceSize.SHAKESCREEN));
            }
            diceLayout.addView(rowLayout);
        }
    }

    public void openHistory() {
        Intent x = new Intent(this, HistoryActivity.class);
        startActivity(x);
        Utility.writeLog("The history is being opened");
    }

    private void roll() {
        LinearLayout diceLayout = findViewById(R.id.diceView);
        diceLayout.removeAllViewsInLayout();
        makeDices();
        service.addThrow(recentThrow);

        if (recentThrow.dices.size() == 2 && recentThrow.dices.get(0).getNumber() == 1
                && recentThrow.dices.get(1).getNumber() == 1) {
            MediaPlayer sound = MediaPlayer.create(this, R.raw.snakeyes);
            sound.start();
        } else {
            MediaPlayer sound = MediaPlayer.create(this, R.raw.diceroll);
            sound.start();
        }
        Button rollButton = findViewById(R.id.rollButton);
        rollButton.setEnabled(false);

        // Disable roll button for 2 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Button rollButton = findViewById(R.id.rollButton);
                rollButton.setEnabled(true);
            }
        }, 2000);
    }

    private void openSettings() {
        Intent x = new Intent(this, SettingsActivity.class);
        x.putExtra("settings", service.getSettings());
        Utility.writeLog("Opening settings");
        startActivityForResult(x, SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SETTINGS)
            switch (resultCode) {
                case CANCEL:
                    Utility.makeToast(this, getString(R.string.nothingChanged));
                    break;
                case UPDATE:
                    // Add code
                    Utility.writeLog("" + Locale.getDefault().getLanguage());
                    Settings settings = (Settings) data.getSerializableExtra("settings");
                    service.updateSettings(settings);
                    //Refresh button text in case of language change
                    refreshViews();
                    // Update GUI
                    updateGUI();
                    Utility.makeToast(this, getString(R.string.changed));
                    break;
                default:
                    break;
            }
    }

    private void refreshViews() {
        Button button = findViewById(R.id.rollButton);
        button.setText(R.string.throwDice);
        Button history = findViewById(R.id.historyBtn);
        history.setText(R.string.history);
        Button settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setText(R.string.settings);
    }

    private void updateGUI() {
        Button rollButton = findViewById(R.id.rollButton);
        LinearLayoutCompat root = findViewById(R.id.shakeScreenRootLayout);
        if (service.getSettings().getShakeMode()) {
            rollButton.setVisibility(View.INVISIBLE);
        } else {
            rollButton.setVisibility(View.VISIBLE);
        }
        if (service.getSettings().getDarkMode()) {
            root.setBackgroundColor(service.DARKMODE);
        } else {
            root.setBackgroundColor(service.NOTDARKMODE);
        }

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long elapsedTime = 0;
            if (lastThrowTime != null) {
                elapsedTime = Calendar.getInstance().getTimeInMillis() - lastThrowTime.getTimeInMillis();
            }
            if (service.getSettings().getShakeMode() && (lastThrowTime == null || elapsedTime > 2000)) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;
                if (mAccel > 12) {
                    rollFromShake();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void rollFromShake() {
        roll();
        Utility.makeToast(context, getString(R.string.hasBeenThrown));
    }

    public void onBackPressed() {
        Utility.writeLog("Closing shake screen");
        finish();
    }


    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private void makeDices() {
        LinearLayout diceLayout = findViewById(R.id.diceView);
        LinearLayout rowLayout;
        ArrayList<Dice> allDice = new ArrayList<>();
        ArrayList<Dice> tempDice = new ArrayList<>();
        int x = 0;
        int orientation = getResources().getConfiguration().orientation;

        for (int i = 0; i < service.getNumberOfDice(); i++) {
            x++;
            Dice dice = GetRandomDice();
            tempDice.add(dice);
            boolean addToView = false;
            // Makes sure two dice are showed on each row
            if (x % 2 == 0) {
                addToView = true;
            }
            if (i == service.getNumberOfDice() - 1) {
                addToView = true;
            }
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (i != service.getNumberOfDice() - 1) {
                    addToView = false;
                } else {
                    addToView = true;
                }
            }

            if (addToView) {
                rowLayout = new LinearLayout(this);
                rowLayout.setGravity(Gravity.CENTER);
                for (Dice curDice : tempDice) {
                    rowLayout.addView(curDice.getImage());
                }
                diceLayout.addView(rowLayout);
                tempDice.clear();
            }
            allDice.add(dice);
        }
        recentThrow = new Throw(allDice, Calendar.getInstance());
        service.setCurrentThrow(recentThrow);
        lastThrowTime = Calendar.getInstance();
    }

    private Dice GetRandomDice() {
        Random rand = new Random();
        ImageView img;
        int chosenNumber = 0;
        if (!service.getSettings().getSixerChanceMode()) {
            chosenNumber = rand.nextInt(6) + 1;
            img = ImageFactory.getImageViewFromInt(chosenNumber, this, DiceSize.SHAKESCREEN);
        } else {
            // Cheat mode
            int theChance = service.getSettings().getChanceOfSixes();
            int randomNumber = rand.nextInt(100) + 1;
            if (randomNumber <= theChance) {
                chosenNumber = 6;
                img = ImageFactory.getImageViewFromInt(6, this, DiceSize.SHAKESCREEN);
            } else {
                chosenNumber = rand.nextInt(5) + 1;
                img = ImageFactory.getImageViewFromInt(chosenNumber, this, DiceSize.SHAKESCREEN);
            }

        }
        Dice newThrow = new Dice(chosenNumber, img);
        return newThrow;
    }

}
