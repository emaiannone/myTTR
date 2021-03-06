package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.util.GoogleMapStarter;

import java.util.List;

class SpielAdapter extends ArrayAdapter<Mannschaftspiel> {
    Activity parent;

    public SpielAdapter(Activity parent, int resource, List<Mannschaftspiel> list) {
        super(parent, resource, list);
        this.parent = parent;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.liga_mannschaft_results_row, viewGroup, false);
        final Mannschaftspiel spiel = getItem(position);

        TextView textView = rowView.findViewById(R.id.date);
        textView.setText(spiel.getDate());

        textView = rowView.findViewById(R.id.heim);
        textView.setText(spiel.getHeimMannschaft().getName());
        textView = rowView.findViewById(R.id.gast);
        textView.setText(spiel.getGastMannschaft().getName());
        textView = rowView.findViewById(R.id.result);
        if (spiel.isPlayed()) {
            textView.setText(spiel.getErgebnis());
        } else {
            textView.setText("");
        }

        final ImageView arrow = rowView.findViewById(R.id.arrow);
        if (spiel.getUrlDetail() == null || spiel.getUrlDetail().isEmpty()) {
            arrow.setVisibility(View.INVISIBLE);
        } else {
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.selectedMannschaftSpiel = spiel;
                    if (MyApplication.selectedMannschaftSpiel.getUrlDetail().contains("livescoring")) {
                        Toast.makeText(getContext(), "Livescoring wird noch nicht unterstützt.", Toast.LENGTH_LONG).show();
                    } else {
                        callMannschaftSpielDetail();
                    }
                }
            });
        }

        final ImageView map = rowView.findViewById(R.id.map);
        if (spiel.getUrlDetail() == null) {
            arrow.setVisibility(View.INVISIBLE);
            if (spiel.getNrSpielLokal() > -1) {
                map.setVisibility(View.VISIBLE);
                map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (spiel.getHeimMannschaft().getSpielLokale().size() == 0) {
                            new ReadInfoAsyncTask(spiel.getHeimMannschaft(),
                                    spiel.getNrSpielLokal(), parent).execute();
                        } else {
                            GoogleMapStarter.showMap(parent, spiel.getActualSpiellokal());
                        }
                    }
                });
            } else {
                map.setVisibility(View.INVISIBLE);
            }
        } else {
            map.setVisibility(View.INVISIBLE);
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.selectedMannschaftSpiel = spiel;
                    callMannschaftSpielDetail();
                }
            });
        }
        return rowView;
    }

    private void callMannschaftSpielDetail() {
        if (MyApplication.selectedMannschaftSpiel.getUrlDetail() == null ||
                MyApplication.selectedMannschaftSpiel.getUrlDetail().isEmpty()) {
            return;
        }
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(parent, LigaSpielberichtActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                new MytClickTTWrapper().readDetail(MyApplication.saison, MyApplication.selectedMannschaftSpiel);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedMannschaftSpiel.getSpiele().size() > 0;
            }


        };
        task.execute();
    }
}
