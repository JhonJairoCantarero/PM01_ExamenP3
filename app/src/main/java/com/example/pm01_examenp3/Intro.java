package com.example.pm01_examenp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        SharedPreferences preferences3 = Intro.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if(preferences3.getBoolean("estado_2",false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent login = new Intent(Intro.this, menu.class);
                    startActivity(login);
                }
            }, 4000);

        } else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent inicio = new Intent(Intro.this, login.class);
                    startActivity(inicio);
                }
            }, 4000);
        }


    }
}