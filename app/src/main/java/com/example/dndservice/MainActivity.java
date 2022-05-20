package com.example.dndservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToSmsActivity(View view) {
        Intent intent = new Intent(this,SmsActivity.class);
        startActivity(intent);
    }
    public void goToCallActivity(View view) {
        Intent intent = new Intent(this,CallActivity.class);
        startActivity(intent);
    }

    public void goToRecordsActivity(View view) {
        Intent intent = new Intent(this,Records.class);
        startActivity(intent);
    }
}