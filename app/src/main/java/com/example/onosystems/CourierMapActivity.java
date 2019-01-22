package com.example.onosystems;

import android.location.Geocoder;
import android.location.Address;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourierMapActivity extends FragmentActivity  implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LatLng mylocation; //初期現在地(test用)
    List<Map<String, String>> deliverylist;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    int time;
    int maxResults = 1;
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.map_toolbar); //R.id.toolbarは各自で設定したidを入れる
        toolbar.inflateMenu(R.menu.tool_options_couriermaps);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.toggle_pin_blue) {
                    Toast.makeText(CourierMapActivity.this, "settings clicked 2", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

        });
    }




    private class MyLocationSource implements LocationSource { //現在地を指定した座標に変える(テスト用)
        @Override
        public void activate(OnLocationChangedListener listener) {
            // 好きな緯度・経度を設定した Location を作成(test用)
            Location location = new Location("MyLocation");
            location.setLatitude(33.620972);
            location.setLongitude(133.719778);
            location.setAccuracy(100); // 精度
            // Location に設定
            listener.onLocationChanged(location);
            // カメラの初期位置用にLatLng型にもしておく
            mylocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        @Override
        public void deactivate() {
        }
    }

    private void locationStart() { //
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);

            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);

    }
    // 結果の受け取り

    /**
     * Android Quickstart:
     * https://developers.google.com/sheets/api/quickstart/android
     *
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */

    //--LocationListenerの構成要素、現在地マーカーの設置に必要
    @Override
    public void onRequestPermissionsResult( //パーミッションの許可を聞きにいった結果を返してくれる
                                            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //--ここまで

    // アクションバーを表示するメソッド
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_options_couriermaps, menu);
        return true;
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TextView varTextView = (TextView) findViewById(R.id.textView);
        switch (item.getItemId()) {
            case R.id.toggle_pin_green:
                //varTextView.setText(R.string.menu_item1);
                return true;
            case R.id.toggle_layout_pin_red:
                //varTextView.setText(R.string.menu_item2);
                return true;
            case R.id.toggle_pin_blue:
                //varTextView.setText(R.string.menu_item3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        final Intent detailActivity = new Intent(getApplication(), CourierDeliveryDetail.class);

        // 画面上にマップを作成
        mMap = googleMap; //マップ


        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                        1000);
            } else {
                locationStart();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, 50, this);
            }
            // 現在地設定の取得
            MyLocationSource source = new MyLocationSource();
            mMap.setLocationSource(source);

            // UI設定の取得
            UiSettings settings = mMap.getUiSettings();


            mMap.setMyLocationEnabled(true); //現在地表示の有効化
            settings.setZoomControlsEnabled(true); //ズームボタン有効化
        }

        //CourierHomeActivityから荷物データを受けとる。
        Intent intent = getIntent();
        deliverylist = (List<Map<String, String>>) intent.getSerializableExtra("deliveryInfo");
        //List<Map<String, String>> deliverylist = (List<Map<String, String>>) intent.getSerializableExtra("deliveryInfo");

        // ひとまず作ったデータをマーカーとして配置
        LatLng[] points = new LatLng[deliverylist.size()]; // maps apiが用意してくれている緯度経度を入れるやつ(LatLng)
        MarkerOptions[] option = new MarkerOptions[deliverylist.size()];

        for(int i = 0; i < points.length; i++) {

            option[i] = new MarkerOptions();
        }



        //Geocoder APIを使って住所から座標への変換を行う
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        List<Address> lstAddr;
        try {
            for (int i = 0; i < deliverylist.size(); i++) {
                //ListにGeocoderAPIから帰ってきた値を入れる
                lstAddr = gcoder.getFromLocationName(deliverylist.get(i).get("address"), maxResults);
                Address addr = lstAddr.get(0);
                points[i] = new LatLng((addr.getLatitude()), (addr.getLongitude()));


                option[i].position(points[i]);
                option[i].title(deliverylist.get(i).get("name"));

                //deliveryTimeによる配達時間の判定
                switch (deliverylist.get(i).get("deliveryTime")) {
                    case "0":
                        option[i].snippet("時間指定無し");
                        break;
                    case "1":
                        option[i].snippet("9時から12時");
                        break;
                    case "2":
                        option[i].snippet("12時から15時");
                        break;
                    case "3":
                        option[i].snippet("15時から18時");
                        break;
                    case "4":
                        option[i].snippet("18から21時");
                        break;
                    default:
                        option[i].snippet("なし");
                }

                //deliveryStatusによるピンの色判定
                status = deliverylist.get(i).get("deliveredStatus");
                switch(status) {
                    case "0":
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        break;
                    case "1":
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        break;
                    case "2":
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //ピンの配置を開始
        Marker[] markers = new Marker[deliverylist.size()];
        for(int i = 0; i < deliverylist.size(); i++) {
            markers[i] = mMap.addMarker(option[i]); // ここでピンをセット
            mHashMap.put(markers[i], i);
        }
        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int pos = mHashMap.get(marker); //タップされた情報ウインドウを持つMarkerのIDを取得
                Intent intent = detailActivity;  // 遷移先指定
                intent.putExtra("itemInfo", (Serializable) deliverylist.get(pos)); //タップされたピンの荷物情報を用意
                startActivity(intent);// 詳細画面に遷移
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15));
    }
}

