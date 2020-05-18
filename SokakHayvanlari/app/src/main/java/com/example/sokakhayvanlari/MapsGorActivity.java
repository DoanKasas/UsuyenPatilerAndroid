package com.example.sokakhayvanlari;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MapsGorActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FirebaseFirestore firebaseFirestore;
    Double lat;
    Double lng;
    String hayvanDurum;
    String hayvanSay;
    String bilgiler;
    String zaman;
    Button patile;
    TextView bilgi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_gor);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        patile=findViewById(R.id.patile);
        bilgi=findViewById(R.id.bilgi);
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
        mMap.setOnMarkerClickListener(this);
    }

    public void getDataFromFirestore(){
        CollectionReference collectionReference = firebaseFirestore.collection("Patiler");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()){
                   Map<String,Object> data = snapshot.getData();
                   zaman = (String) data.get("zaman").toString();
                   Date bugün = new Date();
                   DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                   if(df.format(bugün).equals(zaman)){
                       lat = (Double) data.get("konumx");
                       lng = (Double) data.get("konumY");
                       hayvanDurum = (String) data.get("evDurum");
                       hayvanSay = (String) data.get("hayvanSayisi");
                       LatLng yerler = new LatLng(lat, lng);
                       MarkerOptions options = new MarkerOptions().position(yerler).title(zaman).draggable(false);
                       options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                       mMap.addMarker(options);
                   }
                   else{
                       lat = (Double) data.get("konumx");
                       lng = (Double) data.get("konumY");
                       hayvanDurum = (String) data.get("evDurum");
                       hayvanSay = (String) data.get("hayvanSayisi");
                       LatLng yerler = new LatLng(lat, lng);
                       mMap.addMarker(new MarkerOptions().position(yerler).title("En Son Patilenen Zaman").snippet(zaman).draggable(true));
                   }
               }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isDraggable()==true){
            bilgi.setText("Yakın Zamanda Patilendim diğer dostlarıma yardım et"+"\n"+"          Bizi unutmadığın için Teşekkür Ederiz :)");
            patile.setVisibility(View.INVISIBLE);
        }
        else if (marker.isDraggable()==false)
        {
            bilgi.setText("Patileyebilirmisin :( Uzun zamandır patilenmeye ihtiyacımız var");
            patile.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public void onPatile(){

    }
}
