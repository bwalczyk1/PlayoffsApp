package com.walczyk.apps.playoffsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchesActivity extends AppCompatActivity {
    private ArrayList<String> matches = new ArrayList<>();
    private ListView matchList;
    private HashMap<Integer, String> winners = new HashMap<>();
    private MatchAdapter matchAdapter;
    private DatabaseReference matchesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        matchList = findViewById(R.id.match_list);
        matchList.removeViews(0, matchList.getCount());

        matchesData = FirebaseDatabase.getInstance("https://playoffsapp-fa522-default-rtdb.europe-west1.firebasedatabase.app/").getReference("matches");


        matchesData.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful())
                return;
            GenericTypeIndicator<ArrayList<ArrayList<String>>> typeIndicator = new GenericTypeIndicator<ArrayList<ArrayList<String>>>() {};
            ArrayList<ArrayList<String>> matchesHistory = task.getResult().getValue(typeIndicator);
            if(matchesHistory == null)
                return;
            matches = matchesHistory.get(matchesHistory.size() - 1);
            matchAdapter = new MatchAdapter(
                    MatchesActivity.this,
                    R.layout.match_layout,
                    matches
            );
            matchList.setAdapter(matchAdapter);
        });


        Button roundBtn = findViewById(R.id.round_btn);
        roundBtn.setOnClickListener(v -> {
            String winner = "", newMatch = "";
            ArrayList<String> newMatches = new ArrayList<>();
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
                if(i % 2 == 0)
                    newMatch = winner + "-";
                else{
                    newMatch += winner;
                    newMatches.add(newMatch);
                }
            }

            matchesData.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    GenericTypeIndicator<ArrayList<ArrayList<String>>> typeIndicator = new GenericTypeIndicator<ArrayList<ArrayList<String>>>() {};
                    ArrayList<ArrayList<String>> matchesHistory = task.getResult().getValue(typeIndicator);
                    DatabaseReference newMatchesData = matchesData.child(String.valueOf(matchesHistory.size()));
                    newMatchesData.setValue(newMatches);
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
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}