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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourierMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CompoundButton.OnCheckedChangeListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
//    private ArrayList<Delivery> deliverylist;
//    private LatLng[] points;
    int index;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    int maxResults = 1;
    int colorStatus;
    private FusedLocationProviderClient mLocationClient = null;
    LocationCallback locationCallback = null;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(1000) // 1 seconds
            .setFastestInterval(100) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    private GoogleApiClient mGoogleApiClient;
    private OnLocationChangedListener onLocationChangedLister;
    private boolean firstGetLocationFlag = true;
    private Circle circle;
    private LatLng latlng;

    boolean blueVisible, redVisible, greenVisible;
    boolean[] pinVisible;
    MarkerOptions[] option;
    Marker[] markers;
    public ToggleButton toggle_blue, toggle_red, toggle_green;



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

        Toolbar toolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        setDelivers();
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
                if (firstGetLocationFlag) {
                    circle = mMap.addCircle(new CircleOptions()
                            .radius(100.0)
                            .fillColor(Color.argb(25, 0, 0,255))
                            .strokeColor(Color.rgb(0,0,255))
                            .strokeWidth(1f)
                            .center(new LatLng(location.getLatitude(), location.getLongitude())));
                    if (latlng == null) {
                        moveCamera(new LatLng(location.getLatitude(),location.getLongitude()));
                    } else {
                        moveCamera(latlng);
                    }
                    firstGetLocationFlag = false;
                } else {
                    circle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
                }

            }
        };
    }

    public void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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

    public void setDelivers(){
        //CourierHomeActivityから荷物データを受けとる。
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        if (address != null && !address.isEmpty()) {
            //Geocoder APIを使って住所から座標への変換を行う
            Geocoder gcoder = new Geocoder(this, Locale.getDefault());
            try {
                Address location = gcoder.getFromLocationName(address, 1).get(0);
                latlng = new LatLng(location.getLatitude(), location.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            latlng = null;
        }
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
            settings.setMyLocationButtonEnabled(true);
            settings.setZoomControlsEnabled(true); //ズームボタン有効化
            reloadDelivers(0);
        }


    }

    public void reloadDelivers(int virgin) {

        // ひとまず作ったデータをマーカーとして配置
        option = new MarkerOptions[HomeActivity.deliveryInfo.size()];
        for(int i = 0; i < HomeActivity.deliveryInfo.size(); i++) {
            option[i] = new MarkerOptions();
        }

        for (int i = 0; i < HomeActivity.deliveryInfo.size(); i++) {

            Delivery delivery = HomeActivity.deliveryInfo.get(i);

            option[i].position(new LatLng(delivery.getLatitude(), delivery.getLongitude()));
            option[i].title(delivery.getName());

            //deliveryTimeによる配達時間の判定
            switch (delivery.delivery_time) {
                case 0:
                    option[i].snippet("時間指定無し");
                    break;
                case 1:
                    option[i].snippet("9時から12時");
                    break;
                case 2:
                    option[i].snippet("12時から15時");
                    break;
                case 3:
                    option[i].snippet("15時から18時");
                    break;
                case 4:
                    option[i].snippet("18から21時");
                    break;
                default:
                    option[i].snippet("なし");
            }
        }
        if(virgin == 0) {
            addMapMarker();
        }else if(virgin == 1){
            changeMarkerVisible();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_options_couriermaps, menu);

        toggle_green = menu.findItem(R.id.toggle_pin_green).getActionView().findViewById(R.id.toggle_layout_pin_green);
        toggle_red = menu.findItem(R.id.toggle_pin_red).getActionView().findViewById(R.id.toggle_layout_pin_red);
        toggle_blue = menu.findItem(R.id.toggle_pin_blue).getActionView().findViewById(R.id.toggle_layout_pin_blue);

        toggle_green.setChecked(greenVisible);
        toggle_red.setChecked(redVisible);
        toggle_blue.setChecked(blueVisible);

        toggle_green.setOnCheckedChangeListener(this);
        toggle_red.setOnCheckedChangeListener(this);
        toggle_blue.setOnCheckedChangeListener(this);
        return true;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.toggle_layout_pin_green:
                Log.i("onCheckedChanged", "clicked R.id.toggle_layout_pin_green");
                greenVisible = !greenVisible;
                reloadDelivers(1);
                break;
            case R.id.toggle_layout_pin_red:
                Log.i("onCheckedChanged", "clicked R.id.toggle_layout_pin_red");
                redVisible = !redVisible;
                reloadDelivers(1);
                break;
            case R.id.toggle_layout_pin_blue:
                Log.i("onCheckedChanged", "clicked R.id.toggle_layout_pin_blue");
                blueVisible = !blueVisible;
                reloadDelivers(1);
        }
    }

    public void addMapMarker() {
        final Intent detailActivity = new Intent(getApplication(), CourierDeliveryDetail.class);
        //ピンの配置を開始
        markers = new Marker[HomeActivity.deliveryInfo.size()];
        pinVisible  = new boolean[HomeActivity.deliveryInfo.size()];


        for(int i = 0; i < HomeActivity.deliveryInfo.size(); i++) {

            if(HomeActivity.deliveryInfo.get(i).getDelivered_status() == 1) {
                //ReceivableStatusによるピンの色判定
                colorStatus = HomeActivity.deliveryInfo.get(i).receivable_status;
                switch (colorStatus) {
                    case 0:
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        blueVisible = HomeActivity.deliveryInfo.get(i).getVisible();
                        pinVisible[i] = blueVisible;
                        break;
                    case 1:
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        redVisible = HomeActivity.deliveryInfo.get(i).getVisible();
                        pinVisible[i] = redVisible;
                        break;
                    case 2:
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        greenVisible = HomeActivity.deliveryInfo.get(i).getVisible();
                        pinVisible[i] = greenVisible;
                }
                markers[i] = mMap.addMarker(option[i]); // ここでピンをセット
                markers[i].setVisible(pinVisible[i]);
                mHashMap.put(markers[i], i);
            }
        }

        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int pos = mHashMap.get(marker); //タップされた情報ウインドウを持つMarkerのIDを取得
                Intent intent = detailActivity;  // 遷移先指定
                HashMap<String, String> delivery = new HashMap<>();
                delivery.put("name", HomeActivity.deliveryInfo.get(pos).getName());
                delivery.put("slipNumber", String.valueOf(HomeActivity.deliveryInfo.get(pos).slipNumber));
                delivery.put("address", HomeActivity.deliveryInfo.get(pos).getAddress());
                delivery.put("unixTime", String.valueOf(HomeActivity.deliveryInfo.get(pos).time));
                delivery.put("deliveryTime", String.valueOf(HomeActivity.deliveryInfo.get(pos).delivery_time));
                intent.putExtra("item", delivery); //タップされたピンの荷物情報を用意
                startActivity(intent);// 詳細画面に遷移
            }
        });
    }

    public void changeMarkerVisible(){
        int receivableStatus;
        for (int i = 0; i < HomeActivity.deliveryInfo.size(); i++) {
            if (HomeActivity.deliveryInfo.get(i).delivered_status == 1) {
                receivableStatus = HomeActivity.deliveryInfo.get(i).receivable_status;
                switch (receivableStatus) {
                    case 0:
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        pinVisible[i] = blueVisible;
                        break;
                    case 1:
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        pinVisible[i] = redVisible;
                        break;
                    case 2:
                        option[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        pinVisible[i] = greenVisible;
                }
                markers[i].setVisible(pinVisible[i]);
                HomeActivity.deliveryInfo.get(i).setVisible(pinVisible[i]);
            }
        }
    }
}

