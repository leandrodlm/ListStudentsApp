package com.bhsoftworks.liststudentsapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String endereco, nome;
    public final static String EXTRA_ENDERECO = "ENDERECO";
    public final static String EXTRA_NOME = "NOME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        endereco = intent.getStringExtra(MainActivity.EXTRA_ENDERECO);
        nome = intent.getStringExtra(MainActivity.EXTRA_NOME);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Geocoder coder = new Geocoder(this);
        try {
            List<Address> address = coder.getFromLocationName(endereco,5);
            if (address==null) {
                Toast.makeText(this, "Endereço não encontrado", Toast.LENGTH_LONG).show();
            }
            else{
                Address location = address.get(0);
                mMap = googleMap;
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latlng).title("Endereço de "+nome));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));
                mMap.getMaxZoomLevel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
