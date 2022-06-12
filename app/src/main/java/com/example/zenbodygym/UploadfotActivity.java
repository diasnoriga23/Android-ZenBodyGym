package com.example.zenbodygym;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class UploadfotActivity extends AppCompatActivity {
    Button btn_daftarsekarang, btn_tambah;
    TextView btn_uploadnanti;
    LinearLayout text_uploadfoto, gam, linearLayout1, linearLayout7;
    Animation app_menu;

    Integer photo_max = 1;
    ImageView pic_profil, btn_backupfoto;
    Uri photo_loc;

    DatabaseReference reference, reference1;
    StorageReference storage;

    String USERNAME_KEY= "usernamekey";
    String usernamekey = "";
    String usernamekey_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadfot);
        getUsernameLocal();
        btn_daftarsekarang = findViewById(R.id.btn_daftarsekarang);
        btn_uploadnanti = findViewById(R.id.btn_uploadnanti);
        btn_tambah = findViewById(R.id.btn_tambah);
        pic_profil = findViewById(R.id.pic_profil);
        btn_backupfoto = findViewById(R.id.btn_backupfoto);

        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        text_uploadfoto= findViewById(R.id.text_uploadfoto);
        gam= findViewById(R.id.gam);
        linearLayout1= findViewById(R.id.linearLayout1);
        linearLayout7= findViewById(R.id.linearLayout7);
        text_uploadfoto.startAnimation(app_menu);
        gam.startAnimation(app_menu);
        linearLayout1.startAnimation(app_menu);
        linearLayout7.startAnimation(app_menu);

        btn_uploadnanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference1 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("url_foto").setValue("null");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent moveIntent = new Intent(UploadfotActivity.this, UploadpembayaranActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_daftarsekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
                storage = FirebaseStorage.getInstance().getReference().child("Photouser").child(usernamekey_new);
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
                                    reference1 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child("url_foto").setValue("null");

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Intent moveIntent = new Intent(UploadfotActivity.this, UploadpembayaranActivity.class);
                                    startActivity(moveIntent);
                                }
                            });
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"Pilih Foto !",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findphoto();
            }
        });

        btn_backupfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(UploadfotActivity.this, DaftarActivity.class);
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
            Picasso.with(this).load(photo_loc).fit().centerCrop().into(pic_profil);
        }
    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
    }
}