package com.example.zenbodygym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zenbodygym.Model.PTModel;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PembayaranptActivity extends AppCompatActivity {

    TextView btn_uptf, no_id, nama_leng, text_tagihan, text_atasnama, text_norek, text_bank, text_pt, text_harga_pt;
    Button btn_bayar_nanti;
    LinearLayout btn_upkam;
    CheckBox cb_trainer;
    ScrollView pembayaran;
    Animation app_menu;
    Spinner mSpinner, spinnerPT;

    Uri photo_loc;
    Integer photo_max = 1;
    ImageView pic_bukti, btn_backpembayaran;
    Dialog myDialog;

    DatabaseReference reference, reference1, reference2, reference3, reference4, reference5, reference6,reference7, reference8;
    StorageReference storage;

    String USERNAME_KEY="usernamekey";
    String usernamekey="";
    String usernamekey_new = "";
    String PT,LM,temppt,templm,tagihan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpembayaran);
        getUsernameLocal();
        no_id = findViewById(R.id.no_id);
        nama_leng = findViewById(R.id.nama_leng);
        btn_uptf = findViewById(R.id.btn_uptf);
        btn_bayar_nanti = findViewById(R.id.btn_bayar_nanti);
        btn_upkam = findViewById(R.id.btn_upkam);
        pic_bukti = findViewById(R.id.pic_bukti);
        btn_backpembayaran = findViewById(R.id.btn_backpembayaran);
        text_tagihan = findViewById(R.id.text_tagihan);
        text_atasnama = findViewById(R.id.text_atasnama);
        text_norek = findViewById(R.id.text_norek);
        text_bank = findViewById(R.id.text_bank);
        text_pt = findViewById(R.id.text_pt);
        text_harga_pt = findViewById(R.id.text_harga_pt);
        myDialog = new Dialog(this);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        pembayaran= findViewById(R.id.pembayaran);
        pembayaran.startAnimation(app_menu);
        findViewById(R.id.txtVoucher).setVisibility(View.GONE);

        spinnerPT = findViewById(R.id.selectpt);
        ArrayAdapter<PTModel> ptAdapter = new ArrayAdapter<PTModel>(PembayaranptActivity.this, android.R.layout.simple_spinner_item);
        ptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPT.setAdapter(ptAdapter);

        FirebaseDatabase.getInstance().getReference().child("Admin")
                .orderByChild("hak_akses")
                .equalTo("Personal Trainer")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ptAdapter.clear();
                        for (DataSnapshot snap : snapshot.getChildren())
                            ptAdapter.add(new PTModel(snap.getKey(), snap.child("nama_admin").getValue().toString()));
                        ptAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        reference4 = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
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

        reference8 = FirebaseDatabase.getInstance().getReference().child("Rekening");
        reference8.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> Rekening = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("bank").getValue(String.class);
                    Rekening.add(areaName);
                }

                mSpinner=findViewById(R.id.select);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(PembayaranptActivity.this, android.R.layout.simple_spinner_item, Rekening);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(areasAdapter);

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        if(selectedItem!= null)
                        {

                            reference = FirebaseDatabase.getInstance().getReference().child("Rekening").child(selectedItem);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    text_norek.setText(dataSnapshot.child("no_rek").getValue().toString());
                                    text_atasnama.setText(dataSnapshot.child("nama_rek").getValue().toString());
                                    text_bank.setText(dataSnapshot.child("bank").getValue().toString());



                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
//
                        }
                    } // to close the onItemSelected
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        reference2 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PT = dataSnapshot.child("cb_trainer").getValue().toString();
                if(PT.equals("Pakai Personal Trainer")){
                    temppt = "pakai_pt";

                }else{
                    temppt = "tanpa_pt";
                }
                text_pt.setText(dataSnapshot.child("cb_trainer").getValue().toString());

                reference3 = FirebaseDatabase.getInstance().getReference().child("Paket_Pt").child(temppt);
                reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tagihan = dataSnapshot.child("harga").getValue().toString();
                        text_harga_pt.setText("Rp."+currencyFormat(tagihan));
                        text_tagihan.setText("Rp."+currencyFormat(tagihan));

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
                    }
                });

                reference7 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                reference7.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("total_harga").setValue(tagihan.toString());



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_backpembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(PembayaranptActivity.this, MenuActivity.class);
                startActivity(moveIntent);
                finish();
            }
        });

        btn_uptf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findphoto();
            }
        });

        btn_upkam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findphoto();
            }
        });

        btn_bayar_nanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference5 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                reference5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("url_foto").setValue("null");
                        dataSnapshot.getRef().child("validasi_pembayaran").setValue("valid");
                        dataSnapshot.getRef().child("bayar_nanti").setValue(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent moveIntent = new Intent(PembayaranptActivity.this, MenuActivity.class);
                startActivity(moveIntent);
                finish();
            }
        });

    }

    public void ShowPopupKir(View v) {
        reference = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
        storage = FirebaseStorage.getInstance().getReference().child("Photopembayaran").child(usernamekey_new);
        if (photo_loc != null){
            final StorageReference storageReference1 = storage.child(System.currentTimeMillis() +"."+ getFileExtension(photo_loc));
            storageReference1.putFile(photo_loc).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uri_photo = uri.toString();
                            FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new).child("trainer")
                                    .setValue(((PTModel)spinnerPT.getSelectedItem()).username);
                            reference.getRef().child("url_foto").setValue(uri_photo);
                            reference.getRef().child("validasi_pembayaran").setValue("validasipt");
                            reference.child("bayar_nanti").removeValue();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            myDialog.setContentView(R.layout.activity_popup_pempros);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent moveIntent = new Intent(PembayaranptActivity.this, MenuActivity.class);
                                    startActivity(moveIntent);

                                }
                            }, 1000);

                            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            myDialog.show();
                        }
                    });
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"Upload Foto Pembayaran !",Toast.LENGTH_SHORT).show();
        }
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
            Picasso.with(this).load(photo_loc).fit().centerCrop().into(pic_bukti);
        }

    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(Double.parseDouble(amount));
    }

}