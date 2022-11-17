package com.walczyk.apps.playoffsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, Integer> players = new HashMap<>();
    private ArrayList<String> matches = new ArrayList<>();
    private LinearLayout playersLayout;
    private int roundsFinished;
    private LayoutInflater inflater;

    private DatabaseReference playersData;
    private DatabaseReference matchesData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = getLayoutInflater();
        playersLayout = findViewById(R.id.players_layout);


        playersData = FirebaseDatabase.getInstance("https://playoffsapp-fa522-default-rtdb.europe-west1.firebasedatabase.app/").getReference("players");
        matchesData = FirebaseDatabase.getInstance("https://playoffsapp-fa522-default-rtdb.europe-west1.firebasedatabase.app/").getReference("matches");

        Button confirmButton = findViewById(R.id.confirm_btn);
        playersLayout.setOnLongClickListener(v -> {
            if(matches != null && matches.size() != 0)
                return false;

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("New player");
            alert.setMessage("Add new player");
            EditText input = new EditText(MainActivity.this);
            input.setHint("Name");
            alert.setView(input);
            alert.setPositiveButton("ADD", (dialog, which) -> {
                String playerName = input.getText().toString();
                players.put(playerName, 0);

                playersData.setValue(players);

                addPlayerLayout(playerName, 0);
            });
            alert.show();
            return false;
        });

        confirmButton.setOnClickListener(v -> {
            if(confirmButton.getText().toString().equals("CONFIRM")) {
                int playersNumber = players.size();
                if(playersNumber < 2){
                    Toast.makeText(MainActivity.this, "Add players to start matches", Toast.LENGTH_SHORT).show();
                    return;
                }
                while(playersNumber > 1){
                    if(playersNumber % 2 == 1){
                        Toast.makeText(MainActivity.this, "Number of players must be a power of 2", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    playersNumber /= 2;
                }
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

                DatabaseReference newMatches = matchesData.child("0");;
                newMatches.setValue(matches);

                confirmButton.setText("MATCHES");
            }
            Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
            startActivity(intent);
        });

        SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_layout);

        swipeLayout.setOnRefreshListener(() -> {
            players = new HashMap<>();
            matches = new ArrayList<>();

            playersData.setValue(players);
            matchesData.setValue(matches);

            roundsFinished = 0;
            playersLayout.removeAllViews();
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setText("CONFIRM");

            swipeLayout.setRefreshing(false);
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

        playersData.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful())
                return;
            GenericTypeIndicator<HashMap<String, Integer>> typeIndicator = new GenericTypeIndicator<HashMap<String, Integer>>() {};
            players = task.getResult().getValue(typeIndicator);
            if(players == null) {
                players = new HashMap<>();
                return;
            }
            int numberOfPlayers = players.size();
            int points = 0;
            ArrayList<Integer> playerScores = new ArrayList<Integer>(players.values());
            for(int i = 0; i < numberOfPlayers; i++)
                points += playerScores.get(i);

            roundsFinished = 0;
            while(points != 0 && points>= numberOfPlayers/2){
                roundsFinished += 1;
                numberOfPlayers -= numberOfPlayers/2;
                points -= numberOfPlayers;
            }
            matchesData.get().addOnCompleteListener(task1 -> {
                if(!task1.isSuccessful())
                    return;
                GenericTypeIndicator<ArrayList<ArrayList<String>>> typeIndicator1 = new GenericTypeIndicator<ArrayList<ArrayList<String>>>() {};
                ArrayList<ArrayList<String>> matchesHistory = task1.getResult().getValue(typeIndicator1);
                if(matchesHistory == null)
                    return;
                matches = matchesHistory.get(matchesHistory.size() - 1);
                if(matches.size() != 0) {
                    if(!matches.get(0).contains("-"))
                        findViewById(R.id.confirm_btn).setVisibility(View.GONE);
                    else
                        ((Button)findViewById(R.id.confirm_btn)).setText("MATCHES");
                }
            });
            playersLayout.removeAllViews();
            for (int i = roundsFinished + 1; i >= 0; i--) {
                for(String key : players.keySet()){
                    int playerScore = players.get(key);
                    if(playerScore == i)
                        addPlayerLayout(key, playerScore);
                }
            }
        });
    }
}