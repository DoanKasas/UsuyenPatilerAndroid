package com.example.sokakhayvanlari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cikis_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cikis){
            firebaseAuth.signOut();

            Intent intent = new Intent(MenuActivity.this, GirisMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        firebaseAuth = firebaseAuth.getInstance();
    }

    public void onHaritaEkle(View view){
        Intent intent = new Intent(MenuActivity.this, MapsMenuActivity.class);
        startActivity(intent);
    }

    public void onHaritaGrountule(View view){
        Intent intent = new Intent(MenuActivity.this,MapsGorActivity.class);
        startActivity(intent);
    }
    public void onHakkimizda(View view){
        Intent intent = new Intent(MenuActivity.this,HakkimizdaActivity.class);
        startActivity(intent);
    }


}
