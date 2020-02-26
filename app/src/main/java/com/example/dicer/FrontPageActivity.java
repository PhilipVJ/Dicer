package com.example.dicer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FrontPageActivity extends AppCompatActivity {

    private DiceService service;
    private boolean isEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = DiceService.getInstance();
        if(service.getLocale()!=null){
            Utility.changeLocale(this,service.getLocale());
        }
        setContentView(R.layout.frontpage);
        getSupportActionBar().hide();

        isEnglish = service.getSettings().isActivateEnglish();

        Button button = findViewById(R.id.createDiceButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeDices();
            }
        });
        updateGUI();
    }


    private void makeDices() {
        EditText input = findViewById(R.id.input);
        int inputInt = Integer.parseInt(input.getText().toString());

        if (inputInt <= 0 || inputInt > 6) {
            Toast.makeText(this, getString(R.string.wrongInput), Toast.LENGTH_LONG + 2).show();
            return;
        } else {
            Intent x = new Intent(this, ShakeScreenActivity.class);
            x.putExtra("numberOfDices", inputInt);
            startActivity(x);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGUI();

    }

    private void updateGUI() {
        if (service.getSettings().getDarkMode()) {
            findViewById(R.id.frontpageRootLayout).setBackgroundColor(DiceService.DARKMODE);
        }

        // If the locale has changed - the showing elements text should be updated (mostly relevant when onResume was called
        if(isEnglish!=service.getSettings().isActivateEnglish()){
          refreshViews();
        }

    }

    private void refreshViews(){
        Utility.writeLog("Changes to locale noticed on front page");
        ;Button createDice = findViewById(R.id.createDiceButton);
        createDice.setText(R.string.makeDice);
        TextView text = findViewById(R.id.numberOfDice);
        text.setText(R.string.numberOfDice);
    }

    protected void onSaveInstanceState(Bundle save) {
        super.onSaveInstanceState(save);
    }
}
