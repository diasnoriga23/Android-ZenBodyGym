package com.example.zenbodygym;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Voucher extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog pd;
    EditText voucher;
    Button btngunakan;
    TextView tos;
    String username = "0";
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    Utils u = new Utils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));
        pd.setCancelable(false);
        findViewById(R.id.btncheckvoucher).setOnClickListener(this);
        btngunakan = findViewById(R.id.btnUseVoucher);
        tos = findViewById(R.id.txttos);
        btngunakan.setOnClickListener(this);
        voucher = findViewById(R.id.txtVoucher);
        Bundle ex = getIntent().getExtras();
        voucher.setText(ex.getString("code"));
        username = ex.getString("username", "0");
    }

    String kode_voucher = "";
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btncheckvoucher){
            pd.show();
            db.getReference("Pembayaran/" + username).addListenerForSingleValueEvent(new ValueEventListener() {
                long day_member = 0;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("start_member").exists()){
                        try {
                            day_member = u.getDay(snapshot.child("start_member").getValue(String.class));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    db.getReference("Voucher/" + voucher.getText()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pd.dismiss();
                            try {
                                if (snapshot.child("expired").exists() && !snapshot.child("expired").getValue(String.class).equals("-")){
                                    if (u.getDay(snapshot.child("expired").getValue(String.class)) < 0){
                                        voucherGagal("Voucher Expired");
                                        return;
                                    }
                                }
                                if (snapshot.child("persen").exists()){
                                    boolean condterm = false;
                                    switch (snapshot.child("op").getValue(String.class)){
                                        case "all":
                                            condterm = true;
                                            break;
                                        case "new":
                                            condterm = day_member == 0;
                                            break;
                                        case "long":
                                            condterm = day_member > 30;
                                            break;
                                    }
                                    boolean persen = snapshot.child("persen").getValue(String.class).equals("1");
                                    Long valori = snapshot.child("value").getValue(Long.class);
                                    float val = valori != null ? valori : 0;
                                    kode_voucher = voucher.getText().toString();
                                    if (condterm){
                                        Intent i = new Intent();
                                        i.putExtra("code", kode_voucher);
                                        i.putExtra("value", val);
                                        i.putExtra("percent", persen);
                                        setResult(Activity.RESULT_OK, i);
                                    }else {
                                        setResult(Activity.RESULT_CANCELED);
                                        voucherGagal("Anda tidak memenuhi syarat untuk menggunakan voucher ini", false);
                                    }
                                    tos.setText(snapshot.child("tos").getValue(String.class));
                                    btngunakan.setEnabled(true);
                                    Toast.makeText(Voucher.this, "Voucher berhasil dipilih", Toast.LENGTH_SHORT).show();
                                }else {
                                    voucherGagal("Voucher tidak ditemukan");
                                }
                            }catch (Exception e){
                                voucherGagal("Voucher gagal digunakan", false);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            pd.dismiss();
                            voucherGagal("Di batalkan");
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    pd.dismiss();
                }
            });
        }else if (v.getId() == R.id.btnUseVoucher){
            if (kode_voucher.isEmpty()){
                voucherGagal("Tidak ada voucher digunakan");
            }
            finish();
        }
    }

    private void voucherGagal(String msg){{
        voucherGagal(msg, true);
    }}

    private void voucherGagal(String msg, boolean clear){
        btngunakan.setEnabled(false);
        kode_voucher = "";
        if (clear)
            voucher.setText("");
        tos.setText("");
        setResult(Activity.RESULT_CANCELED);
        Toast.makeText(Voucher.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(Activity.RESULT_CANCELED);
    }
}
