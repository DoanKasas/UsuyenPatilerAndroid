package com.example.sokakhayvanlari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firestore.v1.FirestoreGrpc;

public class GirisMenuActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirestoreGrpc firestoreGrpc;
    EditText emailText,sifreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        firebaseAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.emailText);
        sifreText = findViewById(R.id.passText);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(GirisMenuActivity.this,MenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void girisClick(View view){
        String email = emailText.getText().toString();
        String sifre = sifreText.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(GirisMenuActivity.this,MenuActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GirisMenuActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    public void kayitClick(View view){
        String email = emailText.getText().toString();
        String sifre = sifreText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(GirisMenuActivity.this,"Kullanıcı oluşturuldu",Toast.LENGTH_LONG).show();;
                Intent intent = new Intent(GirisMenuActivity.this,MenuActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GirisMenuActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
