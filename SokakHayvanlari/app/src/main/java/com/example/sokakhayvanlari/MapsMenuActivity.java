package com.example.sokakhayvanlari;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsMenuActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    String adres = "";
    String hayvanTürü;
    String kulubeDurum;
    CheckBox checkKedi;
    CheckBox checkKopek;
    TextView hayvanSayi;
    TextView adresBilgi;
    RadioButton evVar;
    RadioButton evYok;
    RadioGroup kulubeGrup;
    Double lat=0.0,lng=0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_ekle);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        checkKedi = findViewById(R.id.checkKedi);
        checkKopek = findViewById(R.id.checkKopek);
        hayvanSayi = findViewById(R.id.textHayvanSayi);
        adresBilgi = findViewById(R.id.textAdres);
        evVar = findViewById(R.id.radioVar);
        evYok = findViewById(R.id.radioYok);
        kulubeGrup = findViewById(R.id.radioKulube);

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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
        }
        mMap.setOnMapLongClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length >0){
            if (requestCode ==1 ){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,50,locationListener);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        lat =latLng.latitude;
        lng =latLng.longitude;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList != null && addressList.size()>0){
                if (addressList.get(0).getThoroughfare() != null){
                    adres += addressList.get(0).getThoroughfare();
                    if(addressList.get(0).getSubThoroughfare() != null){
                        adres += addressList.get(0).getSubThoroughfare();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(adres));
    }
    public void onPatiEkle(View view){
        int buttonId = kulubeGrup.getCheckedRadioButtonId();
        if (adres !=null ){
            if (checkKopek.isChecked() && checkKedi.isChecked()){
                hayvanTürü = "Kedi ve Köpek";
            }
            else if (checkKopek.isChecked()){
                hayvanTürü ="Köpek";
            }
            else if (checkKedi.isChecked()){
                hayvanTürü = "Kedi";
            }
            switch (buttonId){
                case R.id.radioVar :{kulubeDurum="Barınacak Yer Var";break;}
                case R.id.radioYok :{kulubeDurum="Barınacak Yer Yok";break;}
            }
            HashMap<String,Object> patiData = new HashMap<>();
            patiData.put("adres",adres.toString());
            patiData.put("konumx",lat.doubleValue());
            patiData.put("konumY",lng.doubleValue());
            patiData.put("hayvanTuru",hayvanTürü.toString());
            patiData.put("hayvanSayisi",hayvanSayi.getText().toString());
            patiData.put("evDurum",kulubeDurum.toString());
            patiData.put("adresAciklama",adresBilgi.getText().toString());
            Date zaman = new Date();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            patiData.put("zaman", df.format(zaman));
            firebaseFirestore.collection("Patiler").add(patiData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Intent intent = new Intent(MapsMenuActivity.this,MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MapsMenuActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}


