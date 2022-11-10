package com.walczyk.apps.playoffsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchAdapter extends ArrayAdapter{
    private ArrayList<String> matches = new ArrayList<>();
    private Context _context;
    private int _resource;

    public MatchAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);

        matches = (ArrayList<String>) objects;
        _context = context;
        _resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(_resource, null);
        if(matches.size() > 0) {
            ArrayList<String> matchPlayers = new ArrayList<>(Arrays.asList(matches.get(position).split("-")));
            TextView player1View = convertView.findViewById(R.id.player1_tv);
            player1View.setText(matchPlayers.get(0));
            TextView player2View = convertView.findViewById(R.id.player2_tv);
            player2View.setText(matchPlayers.get(1));
            player1View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player1View.setBackgroundColor(_context.getResources().getColor(R.color.green));
                    player2View.setBackgroundColor(_context.getResources().getColor(R.color.white));
                }
            });
            player2View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player2View.setBackgroundColor(_context.getResources().getColor(R.color.green));
                    player1View.setBackgroundColor(_context.getResources().getColor(R.color.white));
                }
            });
        }
        return convertView;
    }
}
