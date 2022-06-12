package com.example.zenbodygym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfilActivity extends AppCompatActivity {
    Button btn_editprof;
    ImageView btn_backprofil, pic_profil;
    TextView username, nama_leng, email, no_wa;
    ScrollView profil;
    Animation app_menu;

    DatabaseReference reference;
    String USERNAME_KEY="usernamekey";
    String usernamekey="";
    String usernamekey_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        getUsernameLocal();
        btn_editprof = findViewById(R.id.btn_editprof);
        btn_backprofil = findViewById(R.id.btn_backprofil);

        username = findViewById(R.id.username);
        nama_leng = findViewById(R.id.nama_leng);
        email = findViewById(R.id.email);
        no_wa = findViewById(R.id.no_wa);
        pic_profil = findViewById(R.id.pic_profil);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        profil= findViewById(R.id.profil);
        profil.startAnimation(app_menu);


        reference = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue().toString());
                nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                no_wa.setText(dataSnapshot.child("no_wa").getValue().toString());
                if (dataSnapshot.child("url_foto").exists()){
                    Picasso.with(ProfilActivity.this).load(dataSnapshot.child("url_foto").getValue().toString()).centerCrop().fit().into(pic_profil);
                }else {

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_backprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(ProfilActivity.this, MenuActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_editprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(ProfilActivity.this, EditprofActivity.class);
                startActivity(moveIntent);
            }
        });

    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
    }

}
