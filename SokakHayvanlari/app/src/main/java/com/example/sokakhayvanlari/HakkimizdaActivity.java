package com.example.sokakhayvanlari;

import androidx.appcompat.app.AppCompatActivity;

import android.location.SettingInjectorService;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class HakkimizdaActivity extends AppCompatActivity {
    private TextView textview_kedi,textview_kopek;
    private ImageView image_kedi,image_kopek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hakkimizda);
        image_kedi = findViewById(R.id.img_kedi);
        image_kopek = findViewById(R.id.img_kopek);
        textview_kedi = findViewById(R.id.txt_kedi);
        textview_kopek = findViewById(R.id.txt_kopek);
        textview_kedi.setVisibility(View.GONE);
        textview_kopek.setVisibility(View.GONE);

    }

    public void kedigoruntule(View view){
        textview_kedi.setVisibility(View.VISIBLE);
        textview_kopek.setVisibility(View.GONE);
    }
    public void kopekgoruntule(View view){
        textview_kedi.setVisibility(View.GONE);
        textview_kopek.setVisibility(View.VISIBLE);
    }
}
