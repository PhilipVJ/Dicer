package com.example.dicer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class HistoryActivity extends AppCompatActivity {
    private DiceService service;
    ArrayAdapter<Throw> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(DiceService.getInstance().getLocale()!=null){
            Utility.changeLocale(this,DiceService.getInstance().getLocale());
        }
        setContentView(R.layout.history);
        service = DiceService.getInstance();
        Utility.writeLog("Creating new history view");
        getSupportActionBar().hide();
        Button button = findViewById(R.id.clearBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearHistory();
            }
        });
        // Setup adapter
        adapter = new HistoryAdapter(this,
                R.layout.cell,
                service.getThrows());
        ListView historyView = findViewById(R.id.historyView);
        historyView.setMinimumHeight(200);
        historyView.setAdapter(adapter);
        historyView.setDividerHeight(0);
        historyView.setDivider(null);

        if (service.getSettings().getDarkMode()) {
            findViewById(R.id.historyRootLayout).setBackgroundColor(DiceService.DARKMODE);
        }

    }

    private void clearHistory() {
        service.removeThrows();
        adapter.notifyDataSetChanged();
    }

    public void onBackPressed(){
        Utility.writeLog("Closing history screen");
        finish();
    }


}
