package com.example.ps.geodroidapp.Activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.ps.geodroidapp.R;

import java.util.ArrayList;

public class DataMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SqlDataBase db;
    private String session="";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            session = extras.getString("Session");
        }
        
        db = SqlDataBase.getInstance(this);
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
        mMap = googleMap;

        Discontinuity base =new Discontinuity();
        ArrayList<Discontinuity> disc = db.getAllDiscontinuities(session);
        for (Discontinuity d:disc) {
           base=d;
            //LatLng x = new LatLng(d.getLatitude(), d.getLongitude()).;
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(d.getLatitude(), d.getLongitude()))
                    .title(d.getId()+"("+d.getDirection()+","+d.getDip()+")")
                    .anchor(0.5F,0.5F)            // faz com que o marcador seja girado em torno do centro (em vez da base)
                    .rotation(d.getDirection())
                    .flat(true)                     // fixa a rotação
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.strike))
            );
        }
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(38.5, -9);
        //LatLng sydney2 = new LatLng(38.5, -9.5);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Portugal"));
        //mMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in Portugal2"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(base.getLatitude(),base.getLongitude()),10));
    }
}
