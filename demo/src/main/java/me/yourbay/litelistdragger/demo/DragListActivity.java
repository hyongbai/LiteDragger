package me.yourbay.litelistdragger.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import me.yourbay.litelistdragger.DragListView;

public class DragListActivity extends Activity {
    private DragListAdapter adapter = null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("REFRESH").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        refreshData();
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DragListAdapter();
        //
        DragListView listView = new DragListView(this);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setAdapter(adapter);
        setContentView(listView);
        refreshData();
    }

    public ArrayList<String> initData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            StringBuilder sb = new StringBuilder("ITEM ");
            for (int j = 0; j < i; j++) {
                sb.append("  ");
            }
            sb.append(i);
            data.add(sb.toString());
        }
        return data;
    }

    private void refreshData() {
        adapter.setData(initData());
        adapter.notifyDataSetChanged();
    }
}