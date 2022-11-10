package com.walczyk.apps.playoffsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MatchesActivity extends AppCompatActivity {
    private ArrayList<String> matches = new ArrayList<>();
    private ListView matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        matchList = findViewById(R.id.match_list);

        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        String matchesString = sharedPrefs.getString("matches", "");
        matches = new ArrayList<>(Arrays.asList(matchesString.split(",")));
        MatchAdapter matchAdapter = new MatchAdapter(
                MatchesActivity.this,
                R.layout.match_layout,
                matches
        );
        matchList.setAdapter(matchAdapter);
    }
}