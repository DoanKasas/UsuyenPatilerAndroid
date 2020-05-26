package com.example.sokakhayvanlari;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsGorActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FirebaseFirestore firebaseFirestore;
    Button patile;
    TextView bilgi;
    TextView hayvanSayi;
    TextView evDurum;
    TextView adres;
    TextView hayvanCesit;

    Double lat;
    Double lng;
    String hayvanEvDurum;
    String hayvanSay;
    String adresAciklama;
    String hayvanTuru;
    String zaman;
    LatLng yerler;
    LatLng yemis;
    Double yerimx;
    Double yerimY;
    String id;
    String dokumanId;

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
        hayvanSayi=findViewById(R.id.textHaySay);
        adres=findViewById(R.id.textAdres);
        hayvanCesit=findViewById(R.id.textTur);
        evDurum =findViewById(R.id.textEvDurum);

        firebaseFirestore = FirebaseFirestore.getInstance(); // Firebase bağlantısı için
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
        LatLng turkey = new LatLng(38.9536173,35.3123577);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey,5));
        mMap.setOnMarkerClickListener(this);
    }

    public void getDataFromFirestore(){  // Firestoreden verileri çekme
        CollectionReference collectionReference = firebaseFirestore.collection("Patiler"); //Koleksiyon Adı
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()){ // Döküman Içeriği
                   Map<String,Object> data = snapshot.getData();
                   zaman = (String) data.get("zaman").toString();
                   Date bugün = new Date();
                   DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                   if(df.format(bugün).equals(zaman)){ // Firebasedeki zaman değeleri bugüne eşit olanlar
                       lat = (Double) data.get("konumx");
                       lng = (Double) data.get("konumY");
                       yerler= new LatLng(lat, lng); //Haritadaki konumlarını alıyoruz
                       MarkerOptions options = new MarkerOptions().position(yerler).title("En Son Patilenen Zaman").snippet(zaman).draggable(false);
                       options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                       mMap.addMarker(options);
                   }
                   else{
                       lat = (Double) data.get("konumx");
                       lng = (Double) data.get("konumY");
                       yemis = new LatLng(lat, lng);
                       mMap.addMarker(new MarkerOptions().position(yemis).title("En Son Patilenen Zaman").snippet(zaman).draggable(true));
                   }
               }
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) { //Marker tıklandığında gerçekleşecekler
        CollectionReference collectionReference = firebaseFirestore.collection("Patiler");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) { //
                    Map<String, Object> data = snapshot.getData();
                    yerimx = (Double) data.get("konumx");
                    yerimY = (Double) data.get("konumY");
                    hayvanEvDurum = (String) data.get("evDurum");
                    hayvanSay = (String) data.get("hayvanSayisi");
                    adresAciklama = (String) data.get("adresAciklama");
                    hayvanTuru = (String) data.get("hayvanTuru");
                    id = data.get("id").toString();
                    if (marker.isDraggable() == true) {
                        if (marker.getPosition().latitude == yerimx && marker.getPosition().longitude == yerimY){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
                            bilgi.setText("Patileyebilirmisin :( Uzun zamandır patilenmeye ihtiyacımız var");
                            hayvanSayi.setText("Hayvan Sayisi: "+hayvanSay);
                            evDurum.setText("Baraka Durumu: "+hayvanEvDurum);
                            adres.setText("Adres Açıklaması: "+adresAciklama);
                            hayvanCesit.setText("Patiler: "+hayvanTuru);
                            dokumanId=id;
                            patile.setVisibility(View.VISIBLE);
                        }
                    } else if (marker.isDraggable() == false) {
                        if (marker.getPosition().latitude == yerimx && marker.getPosition().longitude == yerimY) {
                            bilgi.setText("Yakın Zamanda Patilendim diğer dostlarıma yardım et" + "\n" + "          Bizi unutmadığın için Teşekkür Ederiz :)");
                            hayvanSayi.setText("Hayvan Sayisi: " + hayvanSay);
                            evDurum.setText("Baraka Durumu: " + hayvanEvDurum);
                            adres.setText("Adres Açıklaması: " + adresAciklama);
                            hayvanCesit.setText("Patiler: " + hayvanTuru);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10));
                            patile.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        return false;
    }

    public void onPatile(View view){// Yemek verilen hayvanın bilgilerini güncelleme
        String docPath=((dokumanId)+"Pati");
        DocumentReference documentReference = firebaseFirestore.collection("Patiler").document(docPath);
        HashMap<String,Object> gunceldata = new HashMap<>();
        Date zamann = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        gunceldata.put("zaman", df.format(zamann));
        documentReference.update(gunceldata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) { //Bilgiler Güncelleniyor..
                Intent intent = new Intent(MapsGorActivity.this,MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
