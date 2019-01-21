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
import android.view.View;
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
import java.util.List;
import java.util.Locale;

class Deliver {
    String name;
    String address;
    int time;
    int deliverd_status;
    int visible;
    double lat;
    double lng;
}


public class CourierMapActivity extends FragmentActivity  implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LatLng mylocation; //初期現在地(test用)


    //toolbarのアイテム表示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

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


        // ひとまず許しておいてほしいゾーン
        // 簡易的なマーカーのデータを作成
        Deliver[] deliver = new Deliver[4];
        for (int i = 0; i < deliver.length; i++) {
            deliver[i] = new Deliver();
        }
        int maxResults = 1;
        final Intent intent_CourierDeliveryDetail = new Intent(getApplication(), CourierDeliveryDetail.class);

        deliver[0].name = "高知工科大学";
        deliver[0].address = "高知県香美市土佐山田町宮ノ口１８５";
        deliver[0].time = 15;
        deliver[0].deliverd_status = 0;
        deliver[0].visible = 0;


        deliver[1].name = "リトルガーデン庭園喫茶";
        deliver[1].address = "高知県香美市土佐山田町佐古藪２８６ー２９";
        deliver[1].time = 18;
        deliver[1].deliverd_status = 1;
        deliver[1].visible = 0;

        deliver[2].name = "おおぞら";
        deliver[2].address = "高知県香美市土佐山田町佐古藪１７２";
        deliver[2].time = 10;
        deliver[2].deliverd_status = 2;
        deliver[2].visible = 0;


        deliver[3].name = "片地小学校";
        deliver[3].address = "高知県香美市土佐山田町宮ノ口９";
        deliver[3].time = 10;
        deliver[3].deliverd_status = 2;
        deliver[3].visible = 0;

        // ここまで

        //Geocoder APIを使って住所から座標への変換を行う
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        List<Address> lstAddr;
        try {
            for (int i = 0; i < deliver.length; i++) {
                lstAddr = gcoder.getFromLocationName(deliver[i].address, maxResults);
                Address addr = lstAddr.get(0);
                deliver[i].lat = (addr.getLatitude());
                deliver[i].lng = (addr.getLongitude());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


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


        // ひとまず作ったデータをマーカーとして配置
        LatLng[] points = new LatLng[deliver.length]; // maps apiが用意してくれている緯度経度を入れるやつ(LatLng)
        for(int i = 0; i < points.length; i++) {
            points[i] = new LatLng(deliver[i].lat, deliver[i].lng);
        }

        MarkerOptions[] option = new MarkerOptions[deliver.length];
        for(int i = 0; i < deliver.length; i++){ // ピンごとに設定を変更
            option[i] = new MarkerOptions();
            option[i].position(points[i]);
            option[i].title(deliver[i].name);
            option[i].snippet(String.valueOf(deliver[i].time) + "時頃");
            if(deliver[i].deliverd_status == 0) {
                option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }else if(deliver[i].deliverd_status == 1) {
                option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }else if(deliver[i].deliverd_status == 2) {
                option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }

        }

        Marker[] markers = new Marker[deliver.length];

        for(int i = 0; i < deliver.length; i++){
            markers[i] = mMap.addMarker(option[i]); // ここでピンをセット
            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    startActivity(intent_CourierDeliveryDetail);
                }
            });
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15));


    }



}

