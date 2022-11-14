package com.walczyk.apps.playoffsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private Map<String, Integer> players = new HashMap<>();
    private ArrayList<String> matches = new ArrayList<>();
    private LinearLayout playersLayout;
    private int roundsFinished;
    private LayoutInflater inflater;
    private SharedPreferences sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = getLayoutInflater();
        playersLayout = findViewById(R.id.players_layout);

        Button addBtn = findViewById(R.id.add_btn);
        Button confirmButton = findViewById(R.id.confirm_btn);
        Button restartButton = findViewById(R.id.restart_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("New player");
                alert.setMessage("Add new player");
                EditText input = new EditText(MainActivity.this);
                input.setHint("Name");
                alert.setView(input);
                alert.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playerName = input.getText().toString();
                        players.put(playerName, 0);
                        JSONObject playersObject = new JSONObject(players);
                        String playersString = playersObject.toString();
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("players", playersString);
                        editor.apply();
                        addPlayerLayout(playerName, 0);
                    }
                });
                alert.show();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(players.size() == 0){
                    Toast.makeText(MainActivity.this, "Add players to start matches", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!sharedPrefs.contains("matches")) {
                    int i = 0;
                    String matchString = "";
                    for (String key : players.keySet()) {
                        if (i % 2 == 0) {
                            matchString = key + "-";
                        } else {
                            matchString += key;
                            matches.add(matchString);
                        }
                        i++;
                    }
                    String matchesString = String.join(",", matches);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("matches", matchesString);
                    editor.apply();
                    addBtn.setVisibility(View.GONE);
                    confirmButton.setText("MATCHES");
                }
                Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
                startActivity(intent);
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                players = new HashMap<>();
                matches = new ArrayList<>();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.remove("players");
                editor.remove("matches");
                editor.apply();
                roundsFinished = 0;
                playersLayout.removeAllViews();
                addBtn.setVisibility(View.VISIBLE);
                confirmButton.setText("MATCHES");
            }
        });
    }

    public void addPlayerLayout(String playerName, int playerScore){
        View playerLayout = inflater.inflate(R.layout.player_layout, playersLayout, false);
        ((TextView)playerLayout.findViewById(R.id.player_name)).setText(playerName);
        ((TextView)playerLayout.findViewById(R.id.player_score)).setText(String.valueOf(playerScore));
        if(playerScore < roundsFinished)
            playerLayout.setBackgroundColor(getResources().getColor(R.color.cloud));
        playersLayout.addView(playerLayout, playersLayout.getChildCount());
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPrefs = getApplicationContext().getSharedPreferences("appPrefs", MODE_PRIVATE);
        if(sharedPrefs.contains("players")){
            String playersString = sharedPrefs.getString("players", (new JSONObject()).toString());
            if(!playersString.equals((new JSONObject()).toString())){
                try {
                    JSONObject jsonObject = new JSONObject(playersString);
                    Iterator<String> keysItr = jsonObject.keys();
                    int numberOfPlayers = 0;
                    int points = 0;
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        numberOfPlayers += 1;
                        points += jsonObject.getInt(key);
                        players.put(key, jsonObject.getInt(key));
                    }
                    while(points != 0 && points>= numberOfPlayers/2){
                        roundsFinished += 1;
                        numberOfPlayers -= numberOfPlayers/2;
                        points -= numberOfPlayers;
                    }
                    if(sharedPrefs.contains("matches")) {
                        findViewById(R.id.add_btn).setVisibility(View.GONE);
                        ((Button)findViewById(R.id.confirm_btn)).setText("MATCHES");
                    }
                    playersLayout.removeAllViews();
                    for (int i = roundsFinished + 1; i >= 0; i--) {
                        for(String key : players.keySet()){
                            int playerScore = players.get(key);
                            if(playerScore == i){
                                addPlayerLayout(key, playerScore);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}