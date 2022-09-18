package com.rumble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonFall = (Button)findViewById(R.id.buttonFall);
        Button buttonSTT = (Button)findViewById(R.id.buttonSTT);

        buttonFall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FallActivity.class);
                startActivity(intent);
            }
        });

        buttonSTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), STTActivity.class);
                startActivity(intent);
            }
        });
    }
}