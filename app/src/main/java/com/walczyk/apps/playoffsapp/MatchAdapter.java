package com.walczyk.apps.playoffsapp;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.PRINT_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MatchAdapter extends ArrayAdapter{
    private ArrayList<String> matches = new ArrayList<>();
    private Context _context;
    private int _resource;
    private HashMap<String, Integer> players = new HashMap<>();

    public MatchAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);

        matches = (ArrayList<String>) objects;
        _context = context;
        _resource = resource;

        try {
            SharedPreferences sharedPrefs = _context.getSharedPreferences("appPrefs", MODE_PRIVATE);
            String playersString = sharedPrefs.getString("players", "");
            JSONObject playersObject = new JSONObject(playersString);
            Iterator<String> keysItr = playersObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                int playerScore = playersObject.getInt(key);
                players.put(key, playerScore);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            try {
                if (players.get(player1Name) > players.get(player2Name)){
                    player1View.setBackgroundColor(_context.getResources().getColor(R.color.green));
                    player1View.setTextColor(_context.getResources().getColor(R.color.white));
                }
                else if (players.get(player2Name) > players.get(player1Name)){
                    player2View.setBackgroundColor(_context.getResources().getColor(R.color.green));
                    player2View.setTextColor(_context.getResources().getColor(R.color.white));
                }

            }catch (Exception e){

            }
            player1View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player1View.setBackgroundColor(_context.getResources().getColor(R.color.green));
                    player1View.setTextColor(_context.getResources().getColor(R.color.white));
                    player2View.setBackgroundColor(_context.getResources().getColor(R.color.white));
                    player2View.setTextColor(_context.getResources().getColor(R.color.green));
                    int player1Score = players.get(player1Name);
                    int player2Score = players.get(player2Name);
                    if(player2Score > player1Score)
                        players.put(player2Name, player2Score - 1);
                    players.put(player1Name, player1Score + 1);
                    updatePreferences();
                }
            });
            player2View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player2View.setBackgroundColor(_context.getResources().getColor(R.color.green));
                    player2View.setTextColor(_context.getResources().getColor(R.color.white));
                    player1View.setBackgroundColor(_context.getResources().getColor(R.color.white));
                    player1View.setTextColor(_context.getResources().getColor(R.color.green));
                    int player1Score = players.get(player1Name);
                    int player2Score = players.get(player2Name);
                    if(player1Score > player2Score)
                        players.put(player1Name, player1Score - 1);
                    players.put(player2Name, player2Score + 1);
                    updatePreferences();
                }
            });
        }
        return convertView;
    }

    public void updatePreferences(){
        JSONObject playersObject = new JSONObject(players);
        String playersString = playersObject.toString();
        SharedPreferences sharedPrefs = _context.getSharedPreferences("appPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("players", playersString);
        editor.apply();
    }
}
