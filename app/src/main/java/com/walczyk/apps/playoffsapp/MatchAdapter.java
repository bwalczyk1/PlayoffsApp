package com.walczyk.apps.playoffsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MatchAdapter extends ArrayAdapter{
    private ArrayList<String> matches;
    private Context _context;
    private int _resource;
    private HashMap<String, Integer> players = new HashMap<>();
    private DatabaseReference playersData;

    public MatchAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);

        matches = (ArrayList<String>) objects;
        _context = context;
        _resource = resource;

        playersData = FirebaseDatabase.getInstance("https://playoffsapp-fa522-default-rtdb.europe-west1.firebasedatabase.app/").getReference("players");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(_resource, null);
        if(matches.size() > 0 && !matches.get(0).equals("")) {
            ArrayList<String> matchPlayers = new ArrayList<>(Arrays.asList(matches.get(position).split("-")));
            TextView player1View = convertView.findViewById(R.id.player1_tv);
            String player1Name = matchPlayers.get(0);
            player1View.setText(player1Name);
            TextView player2View = convertView.findViewById(R.id.player2_tv);
            String player2Name = matchPlayers.get(1);
            player2View.setText(player2Name);

            playersData.get().addOnCompleteListener(task -> {
                if(!task.isSuccessful())
                    return;
                if(players.size() == 0) {
                    GenericTypeIndicator<HashMap<String, Integer>> typeIndicator = new GenericTypeIndicator<HashMap<String, Integer>>() {};
                    players = task.getResult().getValue(typeIndicator);
                }

                try {
                    if (players.get(player1Name) > players.get(player2Name)){
                        player1View.setBackground(_context.getResources().getDrawable(R.drawable.round_left_green));
                        player1View.setTextColor(_context.getResources().getColor(R.color.white));
                    }
                    else if (players.get(player2Name) > players.get(player1Name)){
                        player2View.setBackground(_context.getResources().getDrawable(R.drawable.round_right_green));
                        player2View.setTextColor(_context.getResources().getColor(R.color.white));
                    }

                }catch (Exception e){

                }
                player1View.setOnClickListener(v -> {
                    player1View.setBackground(_context.getResources().getDrawable(R.drawable.round_left_green));
                    player1View.setTextColor(_context.getResources().getColor(R.color.white));
                    player2View.setBackground(_context.getResources().getDrawable(R.drawable.round_right_white));
                    player2View.setTextColor(_context.getResources().getColor(R.color.green));
                    int player1Score = players.get(player1Name);
                    int player2Score = players.get(player2Name);
                    if(player2Score > player1Score)
                        players.put(player2Name, player2Score - 1);
                    players.put(player1Name, player1Score + 1);
                    playersData.setValue(players);
                });
                player2View.setOnClickListener(v -> {
                    player2View.setBackground(_context.getResources().getDrawable(R.drawable.round_right_green));
                    player2View.setTextColor(_context.getResources().getColor(R.color.white));
                    player1View.setBackground(_context.getResources().getDrawable(R.drawable.round_left_white));
                    player1View.setTextColor(_context.getResources().getColor(R.color.green));
                    int player1Score = players.get(player1Name);
                    int player2Score = players.get(player2Name);
                    if(player1Score > player2Score)
                        players.put(player1Name, player1Score - 1);
                    players.put(player2Name, player2Score + 1);
                    playersData.setValue(players);
                });
            });
        }
        return convertView;
    }
}
