package com.example.sokakhayvanlari;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Map;

public class MapsGorActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore firebaseFirestore;
    Double lat;
    Double lng;
    String hayvanDurum;
    String hayvanSay;
    String bilgiler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_gor);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        getDataFromFirestore();
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
    }

    public void getDataFromFirestore(){
        CollectionReference collectionReference = firebaseFirestore.collection("Patiler");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()){
                   Map<String,Object> data = snapshot.getData();
                   lat = (Double) data.get("konumx");
                   lng = (Double) data.get("konumY");
                   hayvanDurum = (String) data.get("evDurum");
                   hayvanSay = (String) data.get("hayvanSayisi");
                   LatLng yerler = new LatLng(lat, lng);
                   bilgiler = ("Ev Durumu: "+ hayvanDurum+ "\n" +"Hayvan Sayısı: "+ hayvanSay);
                   mMap.addMarker(new MarkerOptions().position(yerler).title("Bilgiler").snippet(hayvanSay + "\n" + hayvanDurum));
               }
            }
        });
    }
}
