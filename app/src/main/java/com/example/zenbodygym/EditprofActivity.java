package com.example.zenbodygym;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditprofActivity extends AppCompatActivity {

    Button btn_tambah;
    EditText nama_leng, no_wa, email, password;
    ImageView pic_editprofil, btn_backedprof;
    Dialog myDialog;
    ScrollView edit_profil;
    Animation app_menu;

    Uri photo_loc;
    Integer photo_max = 1;
    DatabaseReference reference;
    StorageReference storage;

    String USERNAME_KEY = "usernamekey";
    String usernamekey = "";
    String usernamekey_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprof);
        getUsernameLocal();

        reference = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
        storage = FirebaseStorage.getInstance().getReference().child("Photouser").child(usernamekey_new);

        nama_leng=findViewById(R.id.nama_leng);
        no_wa=findViewById(R.id.no_wa);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        pic_editprofil = findViewById(R.id.pic_editprofil);
        btn_tambah = findViewById(R.id.btn_tambah);
        btn_backedprof = findViewById(R.id.btn_backedprof);
        myDialog = new Dialog(this);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        edit_profil= findViewById(R.id.edit_profil);
        edit_profil.startAnimation(app_menu);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
                no_wa.setText(dataSnapshot.child("no_wa").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                password.setText(dataSnapshot.child("password").getValue().toString());
                if (dataSnapshot.child("url_foto").exists()){
                    Picasso.with(EditprofActivity.this).load(dataSnapshot.child("url_foto").getValue().toString()).centerCrop().fit().into(pic_editprofil);
                }else {

                }
//                Picasso.with(EditprofActivity.this).load(dataSnapshot.child("url_foto").getValue().toString())
//                        .centerCrop().fit().into(pic_editprofil);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });


        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findphoto();
            }
        });

        btn_backedprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveIntent = new Intent(EditprofActivity.this, ProfilActivity.class);
                startActivity(moveIntent);
            }
        });

    }

    String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void findphoto(){
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic,photo_max);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_loc = data.getData();
            Picasso.with(this).load(photo_loc).fit().centerCrop().into(pic_editprofil);
        }
    }

    public void ShowPopupEdprof(View v) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("nama_leng").setValue(nama_leng.getText().toString());
                dataSnapshot.getRef().child("no_wa").setValue(no_wa.getText().toString());
                dataSnapshot.getRef().child("email").setValue(email.getText().toString());
                dataSnapshot.getRef().child("password").setValue(password.getText().toString());
                if (photo_loc != null){
                    final StorageReference storageReference1 = storage.child(System.currentTimeMillis() +"."+ getFileExtension(photo_loc));
                    storageReference1.putFile(photo_loc).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uri_photo = uri.toString();
                                    reference.getRef().child("url_foto").setValue(uri_photo);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    myDialog.setContentView(R.layout.activity_popup_edprof);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent moveIntent = new Intent(EditprofActivity.this, MenuActivity.class);
                                            startActivity(moveIntent);

                                        }
                                    }, 1000);

                                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    myDialog.show();

                                }
                            });
                        }
                    });
                }else{
                    myDialog.setContentView(R.layout.activity_popup_edprof);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent moveIntent = new Intent(EditprofActivity.this, MenuActivity.class);
                            startActivity(moveIntent);

                        }
                    }, 1000);

                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
    }

}