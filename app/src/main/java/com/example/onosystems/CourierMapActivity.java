package com.example.onosystems;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourierMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationSource, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    List<Map<String, String>> deliverylist;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    int time;
    int maxResults = 1;
    String status;
    private FusedLocationProviderClient mLocationClient = null;
    LocationCallback locationCallback = null;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(1000) // 5 seconds
            .setFastestInterval(100) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    private GoogleApiClient mGoogleApiClient;
    private OnLocationChangedListener onLocationChangedLister;
    private boolean moveCameraFlag = true;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // getLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, LOCATION_REQUEST_CODE);
            System.out.println("please permit GPS");
            return;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.removeLocationUpdates(locationCallback);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createAndSetLocationCallback();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationClient.requestLocationUpdates(REQUEST, locationCallback, null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void createAndSetLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                onLocationChangedLister.onLocationChanged(location);
                if (moveCameraFlag) {
                    circle = mMap.addCircle(new CircleOptions()
                            .radius(100.0)
                            .fillColor(Color.argb(25, 0, 0,255))
                            .strokeColor(Color.rgb(0,0,255))
                            .strokeWidth(1f)
                            .center(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    moveCameraFlag = false;
                } else {
                    circle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
                }

            }
        };
    }


    @Override
    public void onRequestPermissionsResult( //パーミッションの許可を聞きにいった結果を返してくれる
                                            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "地図機能は利用できません", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedLister = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

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
            }
            mMap.setLocationSource(this);

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

    }

}

