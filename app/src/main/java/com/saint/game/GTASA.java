package com.saint.game;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GTASA extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, com.saint.game.core.GTASA.class);
        intent.putExtras(getIntent());
        System.out.println("StartActivity GTASA.class");
        startActivity(intent);
        finish();
    }
}