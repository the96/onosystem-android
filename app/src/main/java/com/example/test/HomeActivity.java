package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

public class HomeActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private ListView listView;

    private String[] name = { "1", "2", "3","4", "5",
            "6", "7","8", "9", "10", "11", "12", "13","abc", "cda"
    };

    /* ----- 実際はこんな感じ？ ------
    // https://techacademy.jp/magazine/17669

    private String [][] deliveryInfo = {{"宛名", "時間", "日付", "住所"}, {"宛名", "時間", "日付", "住所"},
            {"宛名", "時間", "日付", "住所"}, {"宛名", "時間", "日付", "住所"}, }];
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // リスト
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); // リストの項目が選択されたときのイベントを追加

        // 検索
        SearchView search = (SearchView) findViewById(R.id.searchView);
        search.setOnQueryTextListener(this);
        listView.setTextFilterEnabled(true); // インクリメンタルサーチをおこなうかどうか
        search.setQueryHint("検索文字を入力して下さい"); // 何も入力されてないときのテキスト
    }

    // リスト項目が押されたときのイベント
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplication(), CourierDeliveryDetail.class);  // 遷移先指定
        intent.putExtra("DATA", position);// 遷移先に値を渡す，(ここではリストのポジションにしている)
        startActivity(intent);// SubActivityに遷移
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ↓https://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener
    // 検索関係
    public boolean onQueryTextSubmit(String query){
        return false; // summitButtonを実装していないので，falseを返すだけのやつ
    }

    public boolean onQueryTextChange(String newText){
        if (newText == null || newText.equals("")) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText); // ここで絞込み
        }
        return false;
    }
}