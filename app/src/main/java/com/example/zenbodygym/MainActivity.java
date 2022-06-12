package com.example.zenbodygym;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button btn_daf, btn_masuk;
    EditText username, password;
    DatabaseReference reference;
    Dialog myDialog;
    Animation app_menu;
    LinearLayout login;


    String USERNAME_KEY="usernamekey";
    String usernamekey="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_daf = findViewById(R.id.btn_daf);
        btn_masuk = findViewById(R.id.btn_masuk);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        myDialog = new Dialog(this);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);

        login= findViewById(R.id.login);

        login.startAnimation(app_menu);

        btn_daf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(MainActivity.this, DaftarActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String xusername = username.getText().toString();
                final String xpassword = password.getText().toString();

                if (xusername.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Silahkan Isi Username !", Toast.LENGTH_SHORT).show();
                }
                else { if (xpassword.isEmpty()){

                    Toast.makeText(getApplicationContext(), "Silahkan Isi Password !", Toast.LENGTH_SHORT).show();
                }

                else {
                    reference = FirebaseDatabase.getInstance().getReference().child("User").child(xusername);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String passwordFromFirebase = dataSnapshot.child("password").getValue().toString();
                                if (xpassword.equals(passwordFromFirebase)){

                                    SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(usernamekey, username.getText().toString());
                                    editor.apply();

                                    Intent moveIntent = new Intent(MainActivity.this, MenuActivity.class);
                                    startActivity(moveIntent);
                                    finish();

                                    Toast.makeText(getApplicationContext(), "Selamat Datang :)", Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Password Salah !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Username Salah !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                }

            }
        });
    }

}