package com.example.peoplesegmentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {
    Button B1;
    Button B2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        B1=(Button) findViewById(R.id.button);
        B2=(Button) findViewById(R.id.button2);
        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Srnm",Toast.LENGTH_LONG).show();
            }
        });
        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),MainActivity3.class);
                startActivity(i);

            }
        });
    }
}