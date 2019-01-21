package com.example.onosystems;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class HomeActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, TimeChangeAPI.Callback {

    public ArrayList<Delivery> deliveryInfo = new ArrayList<>();
    public HashMap<Long, Boolean> deliveryCheck = new HashMap<>();
    public ArrayList<HashMap<String, String>> list = new ArrayList<>();
    public ListView listView;
    public ToggleButton toggle0, toggle1, toggle2, toggle3;
    public SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm"); //日付フォーマット
    public User User = new User();
    public Object profileInfo;
    public EditText profileName, profileMail, profileTel, profileRePassword;
    public TextView profilePassword;
    public AlertDialog alertDialog;

    public int deliveredStatus;
    public int receivableStatus;

    public int toolBarLayout;
    public int drawerLayout;
    public int homeLayout;
    public Class detailActivity;

    public ActionBarDrawerToggle toggle;
    public DrawerLayout drawer;
    public SwipeRefreshLayout SwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        int keyword = i.getIntExtra("customer_id", 0);


        setUserOptions();
        setContentView(homeLayout);


        SampleLogin loginTask = new SampleLogin();
        String body = "{\n" +
                "  id: \"kut@gmail.com\",\n" +
                "  password: \"onosystems\"\n" +
                "}";
        loginTask.execute("http://www.onosystems.work/aws/Login", body);

        findDeliveries();
        toolbarView();
        refresh();
        getDeliveries();
        getProfile();
    }

    public void getDeliveries() {
        TimeChangeAPI postAsync = new TimeChangeAPI();
        postAsync.setReference(new TimeChangeAPI.Callback() {
            @Override
            public void callbackMethod(String result) {
                parseDeliveries(result);
                reloadDeliveries();
            }
        });
        postAsync.execute(User.getUrl(), User.getUserId());
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadDeliveries();
    }

    public void setUserOptions() { }

    //プロフィール関係
    public void getProfile() {
        TimeChangeAPI postAsync = new TimeChangeAPI();
        postAsync.setReference(new TimeChangeAPI.Callback() {
            @Override
            public void callbackMethod(String result) {
                parseProfile(result);
                setProfile();
            }
        });
        postAsync.execute(User.getProfileURL(), User.getUserId());
    }
    public void parseProfile(String json) { }
    public void setProfile() { }
    public void updateProfile() { }

    //荷物関係
    public void parseDeliveries(String json) {

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < json.length(); i++) {
                JSONObject deliveryData = jsonArray.getJSONObject(i);
                if(deliveryCheck.get(deliveryData.getLong("slip_number")) == null) {
                    deliveryInfo.add(new Delivery(deliveryData.getLong("slip_number"),
                            deliveryData.getString("name"),
                            deliveryData.getString("address"),
                            deliveryData.getString("ship_from"),
                            deliveryData.getInt("time"),
                            deliveryData.getInt("delivery_time"),
                            deliveryData.getInt("delivered_status"),
                            deliveryData.getInt("receivable_status"),
                            Delivery.VISIBLE,
                            Delivery.READ_FLAG));
                    deliveryCheck.put(deliveryData.getLong("slip_number"), true);
                }
            }

            sortTime(); //時間順にソート
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void reloadDeliveries() {
        list = new ArrayList<>(); //初期化

        for (int i = 0; i < deliveryInfo.size(); i++) {
            HashMap<String, String> item = new HashMap<>();
            Date date = new Date(deliveryInfo.get(i).getTime() * 1000L);

            String statusName =  String.valueOf(getResources().getIdentifier("receivable_image" + deliveryInfo.get(i).getReceivable_status(),"drawable",this.getPackageName()));

            if(deliveryInfo.get(i).getVisible()) {
                item.put("itemNumber", String.valueOf(i));
                item.put("name", deliveryInfo.get(i).name);
                item.put("time", String.valueOf(sdf.format(date)));
                item.put("slipNumber", String.valueOf(deliveryInfo.get(i).slipNumber));
                item.put("address", deliveryInfo.get(i).address);
                item.put("shipFrom", deliveryInfo.get(i).ship_from);
                item.put("deliveredStatus", String.valueOf(deliveryInfo.get(i).delivered_status));
                item.put("receivableStatus", String.valueOf(deliveryInfo.get(i).receivable_status));
                item.put("unixTime", String.valueOf(deliveryInfo.get(i).time)); //受け渡し用
                item.put("deliveryTime", String.valueOf(deliveryInfo.get(i).delivery_time));
                item.put("image", statusName);
                if(deliveryInfo.get(i).read_flag) {
                    String newName =  String.valueOf(getResources().getIdentifier("newtext","drawable",this.getPackageName()));
                    item.put("new", newName);
                }
                list.add(item);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                list, R.layout.list_layout,
                new String[]{"name", "time", "slipNumber", "address", "image", "shipFrom", "new"}, // どの項目を
                new int[]{R.id.addressText, R.id.timeText, R.id.slipNumberText, R.id.deliveryAddressText, R.id.image, R.id.shipFrom, R.id.newText} // どのidの項目に入れるか
        );

        listView.setEmptyView(findViewById(R.id.emptyView));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); // リストの項目が選択されたときのイベントを追加
    }
    public void refresh() {
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeliveries();

                if (SwipeRefresh.isRefreshing()) {
                    SwipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    public void sortTime() {
        Collections.sort( deliveryInfo, new Comparator<Delivery>(){
            @Override
            public int compare(Delivery a, Delivery b){
                return a.time - b.time;
            }
        });
    }

    //toolbarのアイテム表示
    public void toolbarView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(drawerLayout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.user);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    getProfile();
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(toolBarLayout, menu);

        toggle0 = menu.findItem(R.id.toggle0).getActionView().findViewById(R.id.toggle_layout_switch0);
        toggle1 = menu.findItem(R.id.toggle1).getActionView().findViewById(R.id.toggle_layout_switch1);
        toggle2 = menu.findItem(R.id.toggle2).getActionView().findViewById(R.id.toggle_layout_switch2);
        toggle3 = menu.findItem(R.id.toggleReceivable).getActionView().findViewById(R.id.receivableSelect);
        toggle0.setChecked(true);
        toggle1.setChecked(true);
        toggle2.setChecked(true);
        toggle3.setChecked(true);
        toggle0.setOnCheckedChangeListener(this);
        toggle1.setOnCheckedChangeListener(this);
        toggle2.setOnCheckedChangeListener(this);
        toggle3.setOnCheckedChangeListener(this);

        return true;
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int buttonId = buttonView.getId();

        switch(buttonId) {
            case R.id.toggle_layout_switch0:
                toggleVisibleFromReceivable(Delivery.UNSELECTED, isChecked);
                break;
            case R.id.toggle_layout_switch1:
                toggleVisibleFromReceivable(Delivery.NOT_RECEIVABLE, isChecked);
                break;
            case R.id.toggle_layout_switch2:
                toggleVisibleFromReceivable(Delivery.RECEIVABLE, isChecked);
                break;
            case R.id.receivableSelect:
                deliveredSelect(isChecked);
                break;
        }
    }

    // リスト項目が押されたときのイベント
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //既読済みに変更
        HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
        int itemNum = Integer.valueOf(item.get("itemNumber"));
        deliveryInfo.get(itemNum).setRead_flag(Delivery.NOT_READ_FLAG);

        Intent intent = new Intent(getApplication(), detailActivity);  // 遷移先指定
        intent.putExtra("itemInfo", (HashMap<String, String>) parent.getItemAtPosition(position));
        startActivity(intent);// 詳細画面に遷移
    }

    //バッグボタンが押されたときのイベント
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //どこにも遷移しない
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            // フォーカスが外れた場合キーボードを非表示にする
            InputMethodManager inputMethodMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 検索関係
    public void findDeliveries() {
        SearchView search = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        search.setOnQueryTextListener(this);
        listView.setTextFilterEnabled(true); // インクリメンタルサーチをおこなうかどうか
        search.setQueryHint("検索文字を入力して下さい"); // 何も入力されてないときのテキスト
    }
    public boolean onQueryTextSubmit(String query){
        return false; // summitButtonを実装していないので，falseを返すだけのやつ
    }
    public boolean onQueryTextChange(String queryText){
        SimpleAdapter filterList = (SimpleAdapter) listView.getAdapter();

        if (TextUtils.isEmpty(queryText)) {
            filterList.getFilter().filter(null);
        } else {
            filterList.getFilter().filter(queryText.toString());
        }

        return true;
    }

    //商品ステータスの切り替え
    public void toggleVisibleFromReceivable(int number, boolean isChecked) {
        for (int i = 0; i < deliveryInfo.size(); i++) {
            deliveredStatus = deliveryInfo.get(i).getDelivered_status();
            receivableStatus = deliveryInfo.get(i).getReceivable_status();

            if(receivableStatus == number) {
                if (isChecked) {
                    if(!(!toggle3.isChecked() && deliveredStatus== Delivery.DELIVERED)) {
                        deliveryInfo.get(i).setVisible(Delivery.VISIBLE);
                    }
                } else {
                    deliveryInfo.get(i).setVisible(Delivery.NOT_VISIBLE);
                }
            }
        }

        reloadDeliveries();
    }
    public void deliveredSelect(boolean isChecked) {
        Boolean check[] = {toggle0.isChecked(), toggle1.isChecked(), toggle2.isChecked()};

        for (int i = 0; i < deliveryInfo.size(); i++) {
            deliveredStatus = deliveryInfo.get(i).getDelivered_status();
            receivableStatus = deliveryInfo.get(i).getReceivable_status();

            if (deliveredStatus == Delivery.DELIVERED) {
                if (isChecked && check[receivableStatus]) {
                    deliveryInfo.get(i).setVisible(Delivery.VISIBLE);
                } else {
                    deliveryInfo.get(i).setVisible(Delivery.NOT_VISIBLE);
                }
            }
        }

        reloadDeliveries();
    }

    @Override
    public void callbackMethod(String result) {
        System.out.println(result);
    }
}

class Delivery {
    public static final boolean VISIBLE = TRUE;
    public static final boolean NOT_VISIBLE = FALSE;
    public static final boolean READ_FLAG = TRUE;
    public static final boolean NOT_READ_FLAG = FALSE;
    // public static final int UNDELIVERED = 0;
    public static final int DELIVERED = 1;
    public static final int UNSELECTED = 0;
    public static final int NOT_RECEIVABLE = 1;
    public static final int RECEIVABLE = 2;
    long slipNumber;
    String name;
    String address;
    String ship_from;
    int time;
    int delivery_time;
    int delivered_status;
    int receivable_status;
    boolean visible;
    boolean read_flag;

    public String getName() { return this.name; }

    public String getAddress() { return this.address; }

    public int getTime() { return this.time; }

    public int getDelivered_status() { return delivered_status; }

    public int getReceivable_status() { return receivable_status; }

    public boolean getVisible() { return visible; }

    public void setVisible(boolean visible) { this.visible = visible; }

    public void setRead_flag(boolean read_flag) { this.read_flag = read_flag; }

    public Delivery(long slipNumber, String name, String address, String ship_from, int time, int delivery_time,
                    int delivered_status, int receivable_status, boolean visible, boolean read_flag) {
        this.slipNumber = slipNumber;
        this.name = name;
        this.address = address;
        this.ship_from = ship_from;
        this.time = time;
        this.delivery_time = delivery_time;
        this.delivered_status = delivered_status;
        this.receivable_status = receivable_status;
        this.visible = visible;
        this.read_flag = read_flag;
    }
}

class User {
    String userId;
    String name;
    String password;
    String mail;
    long tel;
    String url;
    String profileURL;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getProfileURL() { return profileURL; }
    public void setProfileURL(String profileURL) { this.profileURL = profileURL; }

    public String getName() { return name; }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public long getTel() {
        return tel;
    }

}