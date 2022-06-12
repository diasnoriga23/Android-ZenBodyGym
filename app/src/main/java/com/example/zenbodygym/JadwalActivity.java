package com.example.zenbodygym;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class JadwalActivity extends AppCompatActivity implements View.OnClickListener {

    TextView ming1_tgl_ke1, ming1_jam_ke1, ming1_tgl_ke2, ming1_jam_ke2, ming1_tgl_ke3, ming1_jam_ke3,
            ming2_tgl_ke1, ming2_jam_ke1, ming2_tgl_ke2, ming2_jam_ke2, ming2_tgl_ke3, ming2_jam_ke3,
            ming3_tgl_ke1, ming3_jam_ke1, ming3_tgl_ke2, ming3_jam_ke2, ming3_tgl_ke3, ming3_jam_ke3,
            ming4_tgl_ke1, ming4_jam_ke1, ming4_tgl_ke2, ming4_jam_ke2, ming4_tgl_ke3, ming4_jam_ke3;

    DatabaseReference reference, reference1, reference2, reference3, reference4;
    String USERNAME_KEY="usernamekey";
    String usernamekey="";
    String usernamekey_new = "";
    String jdl;
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);
        getUsernameLocal();

        ming1_tgl_ke1=findViewById(R.id.ming1_tgl_ke1);
        ming1_jam_ke1=findViewById(R.id.ming1_jam_ke1);
        ming1_tgl_ke2=findViewById(R.id.ming1_tgl_ke2);
        ming1_jam_ke2=findViewById(R.id.ming1_jam_ke2);
        ming1_tgl_ke3=findViewById(R.id.ming1_tgl_ke3);
        ming1_jam_ke3=findViewById(R.id.ming1_jam_ke3);

        ming2_tgl_ke1=findViewById(R.id.ming2_tgl_ke1);
        ming2_jam_ke1=findViewById(R.id.ming2_jam_ke1);
        ming2_tgl_ke2=findViewById(R.id.ming2_tgl_ke2);
        ming2_jam_ke2=findViewById(R.id.ming2_jam_ke2);
        ming2_tgl_ke3=findViewById(R.id.ming2_tgl_ke3);
        ming2_jam_ke3=findViewById(R.id.ming2_jam_ke3);

        ming3_tgl_ke1=findViewById(R.id.ming3_tgl_ke1);
        ming3_jam_ke1=findViewById(R.id.ming3_jam_ke1);
        ming3_tgl_ke2=findViewById(R.id.ming3_tgl_ke2);
        ming3_jam_ke2=findViewById(R.id.ming3_jam_ke2);
        ming3_tgl_ke3=findViewById(R.id.ming3_tgl_ke3);
        ming3_jam_ke3=findViewById(R.id.ming3_jam_ke3);

        ming4_tgl_ke1=findViewById(R.id.ming4_tgl_ke1);
        ming4_jam_ke1=findViewById(R.id.ming4_jam_ke1);
        ming4_tgl_ke2=findViewById(R.id.ming4_tgl_ke2);
        ming4_jam_ke2=findViewById(R.id.ming4_jam_ke2);
        ming4_tgl_ke3=findViewById(R.id.ming4_tgl_ke3);
        ming4_jam_ke3=findViewById(R.id.ming4_jam_ke3);

        findViewById(R.id.btn_backjadwal).setOnClickListener(this);
        findViewById(R.id.btnprogress).setOnClickListener(this);
        myDialog = new Dialog(this);

        TextView[] tgl = new TextView[]{
                ming1_tgl_ke1,
                ming1_tgl_ke2,
                ming1_tgl_ke3,
                ming2_tgl_ke1,
                ming2_tgl_ke2,
                ming2_tgl_ke3,
                ming3_tgl_ke1,
                ming3_tgl_ke2,
                ming3_tgl_ke3,
                ming4_tgl_ke1,
                ming4_tgl_ke2,
                ming4_tgl_ke3
        };
        TextView[] jamel = new TextView[]{
                ming1_jam_ke1,
                ming1_jam_ke2,
                ming1_jam_ke3,
                ming2_jam_ke1,
                ming2_jam_ke2,
                ming2_jam_ke3,
                ming3_jam_ke1,
                ming3_jam_ke2,
                ming3_jam_ke3,
                ming4_jam_ke1,
                ming4_jam_ke2,
                ming4_jam_ke3
        };

        reference4 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(usernamekey_new);
        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jdl = dataSnapshot.child("validasi_jadwal").getValue().toString();
                if(jdl.equals("Belum Terjadwal")){

                    for (int a = 0; a < tgl.length; a++) {
                        tgl[a].setText("-");
                        jamel[a].setText("-");
                    }

                }else if(jdl.equals("Sudah Terjadwal")) {
                    FirebaseDatabase.getInstance().getReference().child("Jadwal").child(usernamekey_new).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot jadwaldone = snapshot.child("jadwal_selesai");
                            for (int m=0; m<4; m++){
                                int mingind = m*3;
                                int mingad1 = m+1;
                                for (int hari=0; hari < 3; hari++){
                                    int hariad1 = hari+1;
                                    DataSnapshot minggu = snapshot.child("minggu_ke_" + mingad1);
                                    tgl[mingind+hari].setText(minggu.child("ming" + mingad1 + "_tgl_ke"+hariad1).getValue().toString());
                                    jamel[mingind+hari].setText(minggu.child("ming" + mingad1 + "_jam_ke"+hariad1).getValue().toString());
                                    if (jadwaldone.child("ming" + mingad1 + "_tgl_ke"+hariad1).exists()){
                                        if (jadwaldone.child("ming" + mingad1 + "_tgl_ke"+hariad1).child("status").getValue(String.class).equals("Hadir")){
                                            tgl[mingind+hari].setTextColor(getResources().getColor(R.color.hadir));
                                            jamel[mingind+hari].setTextColor(getResources().getColor(R.color.hadir));
                                        }else {
                                            tgl[mingind+hari].setTextColor(getResources().getColor(R.color.tidakhadir));
                                            jamel[mingind+hari].setTextColor(getResources().getColor(R.color.tidakhadir));
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error !!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_backjadwal){
            Intent moveIntent = new Intent(this, MenuActivity.class);
            startActivity(moveIntent);
        }else {
            startActivity(new Intent(this, Progress.class));
        }
    }
}