package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.widget.Toast;

import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.util.GoogleMapStarter;

/**
 * Reading Details
 */
public class ReadInfoAsyncTask extends BaseAsyncTask {

    Mannschaft mannschaft;
    int nr;

    public ReadInfoAsyncTask(Mannschaft mannschaft, int nr, Activity parent) {
        super(parent, null);
        this.mannschaft = mannschaft;
        this.nr = nr;
    }

    @Override
    protected boolean dataLoaded() {
        return mannschaft.getSpielLokale().size() > 0;
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
        new MytClickTTWrapper().readMannschaftsInfo(MyApplication.saison, mannschaft);
    }

    @Override
    protected void startActivityAfterParsing() {
        String lokal = mannschaft.getSpielLokal(nr);
        if (lokal != null) {
            GoogleMapStarter.showMap(parent, mannschaft.getSpielLokal(nr));
        } else {
            Toast.makeText(parent, "Konnte das Spiellokal nicht feststellen. Nr=" +nr,
                    Toast.LENGTH_LONG).show();
        }
    }
}