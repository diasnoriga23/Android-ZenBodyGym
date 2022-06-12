package com.example.zenbodygym;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class DaftarActivity extends AppCompatActivity {

    EditText username,nama_leng, no_wa, email, password, lama_member;
    Button bt_datepicker;
    CheckBox cb_trainer;
    ImageView btn_backprof;
    Animation app_menu;
    LinearLayout daftar;

    int min, max;
    Integer id=1;

    String no_id;


//    int no_id =  new Random().nextInt((max1 - min1) + 1) + min1;

    DatabaseReference reference,reference1,referenceuser;

    String USERNAME_KEY= "usernamekey";
    String usernamekey = "";

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private TextView tvDateResult;
    private Button btDatePicker, btn_daftar;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        username=findViewById(R.id.username);
        nama_leng=findViewById(R.id.nama_leng);
        bt_datepicker=findViewById(R.id.bt_datepicker);
        no_wa=findViewById(R.id.no_wa);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        cb_trainer=findViewById(R.id.cb_trainer);
        app_menu = AnimationUtils.loadAnimation(this, R.anim.app_menu);
        daftar= findViewById(R.id.daftar);
        daftar.startAnimation(app_menu);

        btn_daftar = findViewById(R.id.btn_daftar);
        btn_backprof=findViewById(R.id.btn_backprof);

        lama_member=findViewById(R.id.lama_member);
        lama_member.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "5")});

        pd = new ProgressDialog(this);
        pd.setMessage("Mohon tunggu ...");
        pd.setCancelable(false);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    pd.show();
                    btn_daftar.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference("User").orderByChild("email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            pd.dismiss();
                            btn_daftar.setEnabled(true);
                            if (snapshot.hasChildren()){
                                email.setText("");
                                email.setError("Email sudah digunakan");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            pd.dismiss();
                            btn_daftar.setEnabled(true);
                        }
                    });
                }
            }
        });



        btn_backprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveIntent = new Intent(DaftarActivity.this, MainActivity.class);
                startActivity(moveIntent);
            }
        });

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(usernamekey, username.getText().toString());
                editor.apply();

                final String xusername = username.getText().toString();
                final String xnama_leng = nama_leng.getText().toString();
                final String xbt_datepicker = bt_datepicker.getText().toString();
                final String xno_wa = no_wa.getText().toString();
                final String xemail = email.getText().toString();
                final String xpassword = password.getText().toString();
                final String xlama_member = lama_member.getText().toString();

                referenceuser = FirebaseDatabase.getInstance().getReference().child("User").child(username.getText().toString());
                referenceuser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (xusername.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Silahkan Isi Username !", Toast.LENGTH_SHORT).show();
                        }
                        else { if (dataSnapshot.exists()){
                            Toast.makeText(getApplicationContext(), "Username Sudah Dipakai !", Toast.LENGTH_SHORT).show();
                        }
                        else { if (xnama_leng.isEmpty()){

                            Toast.makeText(getApplicationContext(), "Silahkan Isi Nama Lengkap !", Toast.LENGTH_SHORT).show();
                        }
                        else { if (xbt_datepicker.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Silahkan Isi Tanggal Lahir !", Toast.LENGTH_SHORT).show();
                        }
                        else { if (xno_wa.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Silahkan Isi Nomor Whatshapp !", Toast.LENGTH_SHORT).show();
                        }
                        else {if (xemail.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Silahkan Isi Email !", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (xpassword.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Silahkan Isi Password!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (xlama_member.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Silahkan Lama Member!", Toast.LENGTH_SHORT).show();
                                }else {

                                    Random rand = new Random();
                                    int abcd = rand.nextInt(10000);
                                    id=abcd;
                                    no_id="MB"+id.toString();

                                    reference = FirebaseDatabase.getInstance().getReference().child("User").child(username.getText().toString());
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child("no_id").setValue(no_id);
                                            dataSnapshot.getRef().child("username").setValue(username.getText().toString());
                                            dataSnapshot.getRef().child("nama_leng").setValue(nama_leng.getText().toString());
                                            dataSnapshot.getRef().child("bt_datepicker").setValue(bt_datepicker.getText().toString());
                                            dataSnapshot.getRef().child("no_wa").setValue(no_wa.getText().toString());
                                            dataSnapshot.getRef().child("email").setValue(email.getText().toString());
                                            dataSnapshot.getRef().child("password").setValue(password.getText().toString());
                                            dataSnapshot.getRef().child("lama_member").setValue(lama_member.getText().toString());

                                            reference1 = FirebaseDatabase.getInstance().getReference().child("Pembayaran").child(username.getText().toString());
                                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().child("tgl_bayar_selanjutnya").setValue("-");
                                                    dataSnapshot.getRef().child("validasi_pembayaran").setValue("valid");
                                                    dataSnapshot.getRef().child("validasi_jadwal").setValue("Belum Terjadwal");
                                                    dataSnapshot.getRef().child("total_harga").setValue("");
                                                    dataSnapshot.getRef().child("url_foto").setValue("null");
                                                    if (cb_trainer.isChecked()) {
                                                        dataSnapshot.getRef().child("cb_trainer").setValue(cb_trainer.getText().toString());
                                                    } else {
                                                        dataSnapshot.getRef().child("cb_trainer").setValue("Tidak Pakai Personal Trainer");
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Intent moveIntent = new Intent(DaftarActivity.this, UploadfotActivity.class);
                                    startActivity(moveIntent);
                                }
                            }
                        }
                        }
                        }
                        }
                        }
                        }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        /**
         * Kita menggunakan format tanggal dd-MM-yyyy
         * jadi nanti tanggal nya akan diformat menjadi
         * misalnya 01-12-2017
         */
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tvDateResult = (TextView) findViewById(R.id.bt_datepicker);
        btDatePicker = (Button) findViewById(R.id.bt_datepicker);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

    }

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                tvDateResult.setText(""+dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
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
