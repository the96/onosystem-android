package com.example.onosystems;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
       implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    public ListView listView;
    public Delivery[] deliveryInfo = new Delivery[100];
    public List<Map<String, String>> list;
    public JSONArray datalength;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        toolbarView();
        getDeliveries();
        reloadDeliveries();
        findDeliveries();

    }

    public void findDeliveries() {
       SearchView search = findViewById(R.id.searchView);
       search.setOnQueryTextListener(this);
       listView.setTextFilterEnabled(true); // インクリメンタルサーチをおこなうかどうか
       search.setQueryHint("検索文字を入力して下さい"); // 何も入力されてないときのテキスト
    }

    public void toolbarView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void getDeliveries() {
        //本来はサーバからデータ受け取る
        // 各要素ごとにインスタンス化
        for(int i = 0; i < deliveryInfo.length; i++) {
            deliveryInfo[i] = new Delivery();
        }

        try {
            JSONObject json = new JSONObject("{\"data\":[{\"name\":\"001\", \"time\":\"001\", \"day\":\"1\", \"slipNumber\":\"1111\", \"address\":\"1001\"}," +
                    " {\"name\":\"002\", \"time\":\"002\", \"day\":\"2\", \"slipNumber\":\"1112\", \"address\":\"1002\"}, " +
                    "{\"name\":\"003\", \"time\":\"003\", \"day\":\"3\", \"slipNumber\":\"1113\", \"address\":\"1003\"}]}");

            datalength = json.getJSONArray("data");

            for (int i = 0; i < datalength.length(); i++) {
                deliveryInfo[i].name = json.getJSONArray("data").getJSONObject(i).getString("name");
                deliveryInfo[i].day = json.getJSONArray("data").getJSONObject(i).getInt("day");
                deliveryInfo[i].slipNumber = json.getJSONArray("data").getJSONObject(i).getLong("slipNumber");
                deliveryInfo[i].address = json.getJSONArray("data").getJSONObject(i).getString("address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            SampleLogin loginTask = new SampleLogin();
            String body = "{\n" +
                    "  id: \"kut@gmail.com\",\n" +
                    "  password: \"onosystems\"\n" +
                    "}";
            loginTask.execute("http://54.92.85.232/aws/Login", body);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void reloadDeliveries() {
        list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < datalength.length(); i++) {
            Map<String, String> item = new HashMap<String, String>();
            item.put("name", deliveryInfo[i].name);
            item.put("day", String.valueOf(deliveryInfo[i].day));
            item.put("slipNumber", String.valueOf(deliveryInfo[i].slipNumber));
            item.put("address", deliveryInfo[i].address);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                list, // 使用するデータ
                R.layout.list_layout, // 自作したレイアウト
                new String[]{"name", "day", "slipNumber", "address"}, // どの項目を
                new int[]{R.id.addressText, R.id.timeText, R.id.slipNumberText, R.id.deliveryAddressText} // どのidの項目に入れるか
        );
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); // リストの項目が選択されたときのイベントを追加
    }

    // リスト項目が押されたときのイベント
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplication(), CourierDeliveryDetail.class);  // 遷移先指定
        intent.putExtra("DATA", position);// 遷移先に値を渡す，(ここではリストのポジションにしている)
        startActivity(intent);// CourierDeliveryDetailに遷移
    }

    //バッグボタンが押されたときのイベント
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //どこにも遷移しない
        }
    }

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


    public void toggleVisibleFromReceivable() {
    }

    public void receivableSelect() {
    }


}

class Delivery {
    long slipNumber;
    String name;
    String address;
    long slip_from;
    int time;
    int day;
    int receivable_status;
    int delivered_tatus;
    double lat;
    double lng;
    boolean visible;
}