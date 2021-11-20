package com.cookandroid.healthscanner.ui.data;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cookandroid.healthscanner.R;

public class ListActivity extends AppCompatActivity {
//리스트 창
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }
}