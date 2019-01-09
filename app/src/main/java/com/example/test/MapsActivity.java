package com.example.takamaru.test;
import android.location.Geocoder;
import android.location.Address;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(mMap != null) {
            //mMap.setMyLocationEnabled(true);
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

        // ひとまず許しておいてほしいゾーン
        // 簡易的なマーカーのデータを作成
        Deliver[] deliver = new Deliver[4];
        for(int i = 0; i < deliver.length; i++) {
            deliver[i] = new Deliver();
        }
        int maxResults = 1;
        String place = "高知県香美市土佐山田町宮ノ口１８５";

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
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        List<Address> lstAddr;
        try {
            for(int i = 0; i < deliver.length ; i++) {
                lstAddr = gcoder.getFromLocationName(deliver[i].address, maxResults);
                Address addr = lstAddr.get(0);
                deliver[i].lat = (addr.getLatitude());
                deliver[i].lng = (addr.getLongitude());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Intent intent_CourierDeliveryDetail = new Intent(getApplication(), CourierDeliveryDetail.class);
        // 画面上にマップを作成
        mMap = googleMap; //マップ
        // ひとまず作ったデータをマーカーとして配置
        //LatLng sydney = new LatLng(33.3333, 133.3333);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Kochi"));


        LatLng[] points = new LatLng[deliver.length]; // maps apiが用意してくれている緯度経度を入れるやつ(LatLng)
        for(int i = 0; i < points.length; i++) {
            points[i] = new LatLng(deliver[i].lat, deliver[i].lng);
            // mMap.addMarker(new MarkerOptions().position(markers[i]).title(deliver[i].address).snippet(deliver[i].address));
        }

        MarkerOptions[] option = new MarkerOptions[deliver.length];
        for(int i = 0; i < deliver.length; i++){ // ピンごとに設定を変更
            option[i] = new MarkerOptions();
            option[i].position(points[i]);
            option[i].title(deliver[i].name);
            option[i].snippet(String.valueOf(deliver[i].time) + "時頃");
            //option[i].snippet(String.valueOf(deliver[i].lat) + ":" + String.valueOf(deliver[i].lng));
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points[3], 15));


    }



}

