package com.rahulkantwa.publicproblemsindia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textView = (TextView)findViewById(R.id.name_admin);
        textView.setText("       Rahul Kantwa\n<Designer/Developer>");
    }
}
