package com.example.zenbodygym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class KartuActivity extends AppCompatActivity {
    ImageView btn_kartu;
    TextView no_id, nama_leng, berlaku_hingga;
    ScrollView kartu;
    Animation app_menu;

    DatabaseReference reference, reference1;

    String USERNAME_KEY="usernamekey";
    String usernamekey="";
    String usernamekey_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kartu);
        getUsernameLocal();
        btn_kartu = findViewById(R.id.btn_kartu);
        no_id = findViewById(R.id.no_id);
        nama_leng = findViewById(R.id.nama_leng);
        berlaku_hingga = findViewById(R.id.berlaku_hingga);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        kartu= findViewById(R.id.kartu);
        kartu.startAnimation(app_menu);


        reference = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                no_id.setText(dataSnapshot.child("no_id").getValue().toString());
                nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

        reference1 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                berlaku_hingga.setText(dataSnapshot.child("tgl_bayar_selanjutnya").getValue().toString());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_kartu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveIntent = new Intent(KartuActivity.this, MenuActivity.class);
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
