package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Date extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        textView = (TextView) findViewById(R.id.textview_date);
        Intent intent;
        intent = getIntent();
        String name = intent.getStringExtra("name");
        textView.setText(name);
    }
}