package com.example.onosystems;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private ListView listView;

    /* ----- 実際はこんな感じ？ ------ */
    // https://techacademy.jp/magazine/17669
    private String [][] deliveryInfo = {{"宛名1", "1/1", "日付1", "番号1", "住所1"}, {"宛名2", "1/2", "日付2", "番号2", "住所2"},
            {"宛名3", "1/3", "日付3", "番号3", "住所3"}, {"宛名4", "1/4", "日付4", "番号4", "住所4"}};

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
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i=0; i<deliveryInfo.length; i++){
            Map<String, String> item = new HashMap<String, String>();
            item.put("address", deliveryInfo[i][0]);
            item.put("time", deliveryInfo[i][1]);
            item.put("slipNumber", deliveryInfo[i][3]);
            item.put("deliveryAddress", deliveryInfo[i][4]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                list, // 使用するデータ
                R.layout.list_layout, // 自作したレイアウト
                new String[]{"address","time","slipNumber", "deliveryAddress"}, // どの項目を
                new int[]{R.id.addressText, R.id.timeText, R.id.slipNumberText, R.id.deliveryAddressText} // どのidの項目に入れるか
        );
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
        startActivity(intent);// CourierDeliveryDetailに遷移
    }

    //バッグボタンが押されたときのイベント
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //どこにも遷移しない
        }
    }

    // ↓https://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener
    // 検索関係
    public boolean onQueryTextSubmit(String query){
        return false; // summitButtonを実装していないので，falseを返すだけのやつ
    }

    public boolean onQueryTextChange(String queryText){
        if (TextUtils.isEmpty(queryText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(queryText.toString());
        }
        return false;
    }
}