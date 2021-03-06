package com.jmelzer.myttr.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;

import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 * Shows the results of a team in a saison.
 *
 * @see LigaMannschaftOrLigaResultsFragment
 */
public class LigaMannschaftResultsActivity extends AbstractLigaResultActivity {
    MytClickTTWrapper clickTTWrapper = new MytClickTTWrapper();

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return MyApplication.selectedMannschaft != null;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_mannschaft_detail);

        init();

        setTitle(MyApplication.selectedMannschaft.getName() + " - Ergebnisse");

    }

    @Override
    boolean startWithRR() {
        List<Mannschaftspiel> spiele = MyApplication.getSelectedLiga().getSpieleFor(MyApplication.selectedMannschaft.getName(),
                Liga.Spielplan.RR);
        return (spiele.size() > 0 && spiele.get(0).getErgebnis() != null);
    }

    protected LigaTabsPagerAdapter createTabsAdapter() {
        return new LigaTabsPagerAdapter(getSupportFragmentManager(),
                MyApplication.selectedMannschaft);
    }


    public void bilanz(MenuItem item) {
        readInfoAndStartActivity(LigaMannschaftBilanzActivity.class);

    }

    private void readInfoAndStartActivity(Class clz) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, clz) {
            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                boolean read = false;
                boolean own = false;

                for (Mannschaftspiel mannschaftspiel : MyApplication.getSpieleForActualMannschaft()) {
                    if (mannschaftspiel.getHeimMannschaft().getSpielLokale().size() == 0) {
                        read = true;
                        if (mannschaftspiel.getHeimMannschaft().getLiga().getUrl().contains("/clicktt/home"))
                            own = true;

                        break;
                    }
                }
                if (read) {
                    if (own)
                        clickTTWrapper.readOwnAdressen(MyApplication.selectedMannschaft.getLiga());
                    else
                        clickTTWrapper.readAdressen(MyApplication.saison, MyApplication.getSelectedLiga());
                }
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedMannschaft.getKontakt() != null || MyApplication.selectedMannschaft.getSpielLokale().size() > 0;
            }

        };
        task.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.liga_mannschaft_actions, menu);
        return true;
    }

    public void info(MenuItem item) {
        readInfoAndStartActivity(LigaMannschaftInfoActivity.class);
    }

    public void verein(MenuItem item) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, LigaVereinActivity.class) {
            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                if (MyApplication.selectedMannschaft.getVereinUrl() == null) {
                    clickTTWrapper.readMannschaftsInfo(MyApplication.saison, MyApplication.selectedMannschaft);
                }
                MyApplication.selectedVerein = clickTTWrapper.readVerein(MyApplication.selectedMannschaft.getVereinUrl(), MyApplication.saison);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedVerein != null;
            }

        };
        task.execute();
    }

    public void calendar(MenuItem item) {
        readInfoAndStartActivity(CalendarExportActivity.class);
    }
}
