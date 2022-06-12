package com.example.zenbodygym;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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

import java.util.Collection;

public class MenuActivity extends AppCompatActivity {
    LinearLayout btn_kartu, btn_jadwal, btn_profil, btn_logout, btn_wa, btn_perpan, btn_perpanpt;
    Dialog myDialog;

    ImageView pic_profil;
    TextView nama_leng, tgl_bayar_selanjutnya, exp_pt;
    Button btn_bayar;
    ImageView pic_bukti;
    TextView btn_batal;
    ScrollView menu;
    Animation app_menu;



    DatabaseReference reference, reference1, reference2, reference3, reference4, reference5, reference6, reference7;
    String USERNAME_KEY="usernamekey";
    String usernamekey="";
    String usernamekey_new = "";
    String train,url,tgl_pembayaran, valid_jad;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));
        getUsernameLocal();
        btn_kartu = findViewById(R.id.btn_kartu);
        btn_perpan = findViewById(R.id.btn_perpan);
        btn_perpanpt = findViewById(R.id.btn_perpanpt);
        btn_wa = findViewById(R.id.btn_wa);
        btn_jadwal = findViewById(R.id.btn_jadwal);
        btn_profil = findViewById(R.id.btn_profil);
        btn_logout = findViewById(R.id.btn_logout);
        tgl_bayar_selanjutnya = findViewById(R.id.tgl_bayar_selanjutnya);
        pic_profil = findViewById(R.id.pic_profil);
        nama_leng = findViewById(R.id.nama_leng);
        exp_pt = findViewById(R.id.exp_pt);
        myDialog = new Dialog(this);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        menu= findViewById(R.id.menu);
        menu.startAnimation(app_menu);

        reference = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
                if (dataSnapshot.child("url_foto").exists()){
                    Picasso.with(MenuActivity.this).load(dataSnapshot.child("url_foto").getValue().toString()).centerCrop().fit().into(pic_profil);
                }else {

                }

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
                tgl_bayar_selanjutnya.setText(dataSnapshot.child("tgl_bayar_selanjutnya").getValue().toString());
                train = dataSnapshot.child("cb_trainer").getValue().toString();
                if (!tgl_bayar_selanjutnya.equals("-")){
                    if (train.equals("Tidak Pakai Personal Trainer")){
                        btn_jadwal.setEnabled(false);
                        btn_wa.setEnabled(false);
                        btn_perpanpt.setEnabled(false);
                    }
              
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

        reference7 = FirebaseDatabase.getInstance().getReference().child("Jadwal").child(usernamekey_new).child("jadwal_selesai");
        reference7.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size = 0;
                if (dataSnapshot.exists()){
                    for (Object d : dataSnapshot.getChildren()) {
                        size++;
                    }
                }
                exp_pt.setText(String.format("%s/12", String.valueOf(size)));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_kartu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(MenuActivity.this, KartuActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_jadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(MenuActivity.this, JadwalActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(MenuActivity.this, ProfilActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(usernamekey, null);
                editor.apply();

                Intent moveIntent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(moveIntent);
                finish();
            }
        });

        btn_wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference6 = FirebaseDatabase.getInstance().getReference().child("No_Wa");
                reference6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String number = dataSnapshot.child("no_wa_pt").getValue().toString();
                        String url = "https://api.whatsapp.com/send?phone="+number;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setPackage("com.whatsapp");
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Utils u = new Utils();
        btn_perpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
            reference2 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pd.dismiss();
                    url = dataSnapshot.child("url_foto").getValue().toString();
                    tgl_pembayaran = dataSnapshot.child("validasi_pembayaran").getValue().toString();
                    long m_day = u.getDay(dataSnapshot.child("tgl_bayar_selanjutnya").getValue(String.class));
                    if ( m_day > 0){
                        u.showAlert(MenuActivity.this, getString(R.string.member_exist, m_day));
                        return;
                    }
                    if(!url.equals("null")){
                        if (tgl_pembayaran.equals("valid")){
                            EditText lama_member;
                            Button btn_perpanjangan;
                            TextView btn_batal, no_id, nama_leng;
                            CheckBox cb_trainer;
                            myDialog.setContentView(R.layout.activity_popup_perpan);

                            btn_batal = (TextView) myDialog.findViewById(R.id.btn_batal);
                            btn_perpanjangan = (Button) myDialog.findViewById(R.id.btn_perpanjang);
                            btn_perpanjangan.setEnabled(false);
                            cb_trainer= myDialog.findViewById(R.id.cb_trainer);

                            no_id = (TextView) myDialog.findViewById(R.id.no_id);
                            nama_leng = (TextView) myDialog.findViewById(R.id.nama_leng);
                            getUsernameLocal();
                            lama_member= myDialog.findViewById(R.id.lama_member);
                            lama_member.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "5")});


                            reference= FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
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

                            btn_batal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.dismiss();
                                }
                            });

                            btn_perpanjangan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reference1 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (cb_trainer.isChecked()){
                                                dataSnapshot.getRef().child("cb_trainer").setValue(cb_trainer.getText().toString());
                                            } else {
                                                dataSnapshot.getRef().child("cb_trainer").setValue("Tidak Pakai Trainer");
                                            }
                                            dataSnapshot.getRef().child("url_foto").setValue("null");
                                            dataSnapshot.getRef().child("tgl_bayar_selanjutnya").setValue("-");
                                            dataSnapshot.getRef().child("validasi_pembayaran").setValue("invalid");

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    reference1 = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
                                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        final String xlama_member = lama_member.getText().toString();
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (xlama_member.isEmpty()) {
                                                Toast.makeText(getApplicationContext(), "Silahkan Lama Member!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                dataSnapshot.getRef().child("lama_member").setValue(lama_member.getText().toString());
                                                Intent moveIntent = new Intent(MenuActivity.this, UploadpembayaranActivity.class);
                                                startActivity(moveIntent);
                                            }



                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }
                        else {
                            myDialog.setContentView(R.layout.activity_popup_menunggukon);

                            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            myDialog.show();

                        }
                    }
                    else if(url.equals("null")) {
                        if (!dataSnapshot.child("bayar_nanti").exists() && tgl_pembayaran.equals("invalid")) {


                            int min, max;
                            EditText lama_member;
                            Button btn_perpanjangan;
                            TextView btn_batal, no_id, nama_leng;
                            CheckBox cb_trainer;
                            myDialog.setContentView(R.layout.activity_popup_perpan);

                            btn_batal = (TextView) myDialog.findViewById(R.id.btn_batal);
                            btn_perpanjangan = (Button) myDialog.findViewById(R.id.btn_perpanjang);
                            btn_perpanjangan.setEnabled(true);
                            cb_trainer = myDialog.findViewById(R.id.cb_trainer);

                            no_id = (TextView) myDialog.findViewById(R.id.no_id);
                            nama_leng = (TextView) myDialog.findViewById(R.id.nama_leng);
                            getUsernameLocal();
                            lama_member = myDialog.findViewById(R.id.lama_member);
                            lama_member.setFilters(new InputFilter[]{new InputFilterMinMax("1", "5")});


                            reference3 = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
                            reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    no_id.setText(dataSnapshot.child("no_id").getValue().toString());
                                    nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Database Error !!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            btn_batal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.dismiss();
                                }
                            });

                            btn_perpanjangan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reference4 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                                    reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (cb_trainer.isChecked()) {
                                                dataSnapshot.getRef().child("cb_trainer").setValue(cb_trainer.getText().toString());
                                            } else {
                                                dataSnapshot.getRef().child("cb_trainer").setValue("Tidak Pakai Personal Trainer");
                                            }
                                            dataSnapshot.getRef().child("url_foto").setValue("null");
                                            dataSnapshot.getRef().child("tgl_bayar_selanjutnya").setValue("-");
                                            dataSnapshot.getRef().child("validasi_pembayaran").setValue("invalid");

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    reference5 = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
                                    reference5.addListenerForSingleValueEvent(new ValueEventListener() {
                                        final String xlama_member = lama_member.getText().toString();

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (xlama_member.isEmpty()) {
                                                Toast.makeText(getApplicationContext(), "Silahkan Lama Member!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dataSnapshot.getRef().child("lama_member").setValue(lama_member.getText().toString());
                                                Intent moveIntent = new Intent(MenuActivity.this, UploadpembayaranActivity.class);
                                                startActivity(moveIntent);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });

                        }
                        else {
                            Intent moveIntent = new Intent(MenuActivity.this, UploadpembayaranActivity.class);
                            startActivity(moveIntent);

                        }
                    }

                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.show();

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    });

        btn_perpanpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
            reference2 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pd.dismiss();
                    url = dataSnapshot.child("url_foto").getValue().toString();
                     String valid = dataSnapshot.child("validasi_pembayaran").getValue().toString();
                    long m_day = u.getDay(dataSnapshot.child("tgl_bayar_selanjutnya").getValue(String.class));
                    if ( m_day < 1){
                        u.showAlert(MenuActivity.this, getString(R.string.member_expired));
                        return;
                    }else if((!dataSnapshot.child("validasi_jadwal").getValue(String.class).equals("Belum Terjadwal") ||
                    !dataSnapshot.child("validasi_pembayaran").getValue(String.class).equals("invalid")) &&
                    !dataSnapshot.child("cb_trainer").getValue(String.class).equals("Tidak Pakai Personal Trainer") &&
                    !dataSnapshot.child("validasi_pembayaran").getValue(String.class).equals("validasipt")
                    ){
                        u.showAlert(MenuActivity.this, getString(R.string.schedule_exist));
                        return;
                    }
                    if(!url.equals("null")) {
                        if (valid.equals("valid")) {

                            int min, max;
                            EditText lama_member;
                            Button btn_perpanjangan;
                            TextView btn_batal, no_id, nama_leng;
                            CheckBox cb_trainer;
                            myDialog.setContentView(R.layout.activity_popup_perpanpt);

                            btn_batal = (TextView) myDialog.findViewById(R.id.btn_batal);
                            btn_perpanjangan = (Button) myDialog.findViewById(R.id.btn_perpanjang);
                            btn_perpanjangan.setEnabled(false);
                            cb_trainer = myDialog.findViewById(R.id.cb_trainer);
                            no_id = (TextView) myDialog.findViewById(R.id.no_id);
                            nama_leng = (TextView) myDialog.findViewById(R.id.nama_leng);
                            getUsernameLocal();

                            reference = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    no_id.setText(dataSnapshot.child("no_id").getValue().toString());
                                    nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Database Error !!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            btn_batal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.dismiss();
                                }
                            });

                            btn_perpanjangan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reference1 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (cb_trainer.isChecked()) {
                                                dataSnapshot.getRef().child("cb_trainer").setValue(cb_trainer.getText().toString());
                                            } else {
                                                dataSnapshot.getRef().child("cb_trainer").setValue("Tidak Pakai Trainer");
                                            }
                                            dataSnapshot.getRef().child("url_foto").setValue("null");
                                            dataSnapshot.getRef().child("validasi_pembayaran").setValue("invalid");

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        } else {
                            myDialog.setContentView(R.layout.activity_popup_menunggukon);

                            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            myDialog.show();

                        }
                    }else if(url.equals("null")) {
                        if (!dataSnapshot.child("bayar_nanti").exists() && valid.equals("invalid")) {


                            int min, max;
                            EditText lama_member;
                            Button btn_perpanjangan;
                            TextView btn_batal, no_id, nama_leng;
                            CheckBox cb_trainer;
                            myDialog.setContentView(R.layout.activity_popup_perpanpt);

                            btn_batal = (TextView) myDialog.findViewById(R.id.btn_batal);
                            btn_perpanjangan = (Button) myDialog.findViewById(R.id.btn_perpanjang);
                            btn_perpanjangan.setEnabled(false);
                            cb_trainer = (CheckBox) myDialog.findViewById(R.id.cb_trainer);
                            cb_trainer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                      @Override
                                                                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                          if (cb_trainer.isChecked()) {
                                                                              btn_perpanjangan.setEnabled(true);
                                                                          } else {
                                                                              btn_perpanjangan.setEnabled(false);
                                                                          }
                                                                      }
                                                                  }
                            );

                            no_id = (TextView) myDialog.findViewById(R.id.no_id);
                            nama_leng = (TextView) myDialog.findViewById(R.id.nama_leng);
                            getUsernameLocal();


                            reference3 = FirebaseDatabase.getInstance().getReference().child("User").child(usernamekey_new);
                            reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    no_id.setText(dataSnapshot.child("no_id").getValue().toString());
                                    nama_leng.setText(dataSnapshot.child("nama_leng").getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Database Error !!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            btn_batal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.dismiss();
                                }
                            });

                            btn_perpanjangan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reference4 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
                                    reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (cb_trainer.isChecked()) {
                                                dataSnapshot.getRef().child("cb_trainer").setValue(cb_trainer.getText().toString());
                                            } else {
                                                dataSnapshot.getRef().child("cb_trainer").setValue("Tidak Pakai Trainer");
                                            }
                                            dataSnapshot.getRef().child("url_foto").setValue("null");
                                            dataSnapshot.getRef().child("validasi_pembayaran").setValue("invalid");
                                            Intent moveIntent = new Intent(MenuActivity.this, PembayaranptActivity.class);
                                            startActivity(moveIntent);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });


                        }else {
                            Intent moveIntent = new Intent(MenuActivity.this, PembayaranptActivity.class);
                            startActivity(moveIntent);

                        }
                    }
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

}
