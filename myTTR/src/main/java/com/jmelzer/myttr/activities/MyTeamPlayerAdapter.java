package com.jmelzer.myttr.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Util;

import java.util.List;

/**
 * Adapter for displaying a list of players from aforeign team.
 * User: jmelzer
 * Date: 23.03.14
 * Time: 14:34
 */
public class MyTeamPlayerAdapter extends ArrayAdapter<Player> {

    public MyTeamPlayerAdapter(Context context, int resource, List<Player> players) {
        super(context, resource, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.myplayerrow, parent, false);
        Player player = getItem(position);


        TextView textView = (TextView) rowView.findViewById(R.id.firstname);
        textView.setText(player.getFirstname());
        textView = (TextView) rowView.findViewById(R.id.lastname);
        textView.setText(player.getLastname());
        return rowView;
    }
}