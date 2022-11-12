package com.walczyk.apps.playoffsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class MatchesActivity extends AppCompatActivity {
    private ArrayList<String> matches = new ArrayList<>();
    private ListView matchList;
    private HashMap<Integer, String> winners = new HashMap<>();
    private MatchAdapter matchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        matchList = findViewById(R.id.match_list);
        matchList.removeViews(0, matchList.getCount());

        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("appPrefs", MODE_PRIVATE);
        String matchesString = sharedPrefs.getString("matches", "");
        matches = new ArrayList<>(Arrays.asList(matchesString.split(",")));
        matchAdapter = new MatchAdapter(
                MatchesActivity.this,
                R.layout.match_layout,
                matches
        );
        matchList.setAdapter(matchAdapter);

        Button roundBtn = findViewById(R.id.round_btn);
        roundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String winner = "", newMatch = "";
                ArrayList<String> newMatches = new ArrayList<>(), winners = new ArrayList<>();
                View matchView;
                TextView player1View, player2View;
                for(int i = 0; i < matchList.getCount(); i++){
                    matchView = matchList.getChildAt(i);
                    player1View = matchView.findViewById(R.id.player1_tv);
                    player2View = matchView.findViewById(R.id.player2_tv);
                    if(player1View.getCurrentTextColor() == getResources().getColor(R.color.white))
                        winner = player1View.getText().toString();
                    else if(player2View.getCurrentTextColor() == getResources().getColor(R.color.white))
                        winner = player2View.getText().toString();
                    else{
                        Toast.makeText(MatchesActivity.this, "Not all results given", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    winners.add(winner);
                    if(i % 2 == 0)
                        newMatch = winner + "-";
                    else{
                        newMatch += winner;
                        newMatches.add(newMatch);
                    }
                }
//                String playersString = sharedPrefs.getString("players", (new JSONObject()).toString());
//                try {
//                    JSONObject playersObject = new JSONObject(playersString);
//                    Iterator<String> keysItr = playersObject.keys();
//                    HashMap<String, Integer> players = new HashMap<>();
//                    while (keysItr.hasNext()) {
//                        String key = keysItr.next();
//                        int playerScore = playersObject.getInt(key);
//                        if(winners.contains(key))
//                            playerScore += 1;
//                        players.put(key, playerScore);
//                    }
//                    playersObject = new JSONObject(players);
//                    playersString = playersObject.toString();
                    String matchesString = String.join(",", newMatches);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
//                    editor.putString("players", playersString);
                    editor.putString("matches", matchesString);
                    editor.apply();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                matches = newMatches;
                matchAdapter.notifyDataSetChanged();
                matchAdapter = new MatchAdapter(
                        MatchesActivity.this,
                        R.layout.match_layout,
                        matches
                );
                matchList.setAdapter(matchAdapter);
            }
        });
    }
}