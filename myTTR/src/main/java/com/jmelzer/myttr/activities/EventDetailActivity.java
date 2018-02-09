package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Competition;
import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.tasks.Head2HeadAsyncTask;

import java.util.List;

/**
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class EventDetailActivity extends BaseActivity {
    EventDetail currentDetail;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.currentDetail != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.event_detail);

        currentDetail = MyApplication.currentDetail;
        final ListView listview = findViewById(R.id.event_detail_list);
        final EventDetailAdapter adapter = new EventDetailAdapter(this,
                android.R.layout.simple_list_item_1,
                currentDetail.getGames());
        listview.setAdapter(adapter);

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                view.setSelected(true);
//                if (position > -1 && position < MyApplication.getEvents().size()) {
//                    Game game = currentDetail.getGames().get(position);
//
//                    new EventsAsyncTask(EventDetailActivity.this, EventsActivity.class, game).execute();
//                }
//            }
//        });


    }

    private static class ViewHolder {
        TextView textName;
        TextView textResult;
        TextView textSets;
        int id;
    }

    class EventDetailAdapter extends ArrayAdapter<Game> {
        private LayoutInflater layoutInflater;

        public EventDetailAdapter(Context context, int resource, List<Game> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.eventdetailrow_linear, null);
                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.name);
                holder.textResult = convertView.findViewById(R.id.result);
                holder.textSets = convertView.findViewById(R.id.sets);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            EventDetailActivity.this.registerForContextMenu(convertView);
            Game game = getItem(position);
            if (game != null) {
                holder.id = position;
                holder.textName.setText(game.getPlayerWithPoints());
                holder.textResult.setText(game.getResult());
                holder.textSets.setText(game.getSetsInARow());
            }
            return convertView;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Aktionen");
        ViewHolder holder = (ViewHolder) v.getTag();
        menu.add(1, holder.id, 1, "Spieler Statistiken");
        menu.add(2, holder.id, 1, "Head 2 Head");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:

                callEvents(item.getItemId());
                break;
            case 2:
                callHead2Head(item.getItemId());
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void callHead2Head(int itemId) {
        Game game = currentDetail.getGames().get(itemId);
        new Head2HeadAsyncTask(this, game.getPlayerId(), Head2HeadActivity.class).execute();
    }


    private void callEvents(int itemId) {
        Game game = currentDetail.getGames().get(itemId);
        new EventsAsyncTask(this, EventsActivity.class, game).execute();
    }
}