package com.example.zenbodygym;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Utils {

    SimpleDateFormat formattime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

    public Utils(){
        formattime.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
    }

    public long getDay(String end){
        try {
            Date today = new Date();
            today.setHours(0);
            today.setMinutes(0);
            today.setSeconds(0);
            return TimeUnit.DAYS.convert(formattime.parse( end + " 23:59:59").getTime() - today.getTime(), TimeUnit.MILLISECONDS);
        }catch (Exception e){
            return 0;
        }
    }

    public Dialog showAlert(Activity ctx, String msg){
        return showAlert(ctx, "", msg);
    }

    public Dialog showAlert(Activity ctx, String title, String msg){
        Dialog d = new Dialog(ctx);
        d.setContentView(R.layout.activity_dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView ttitle = d.findViewById(R.id.title);
        if (title.isEmpty()){
            ttitle.setVisibility(View.GONE);
        }else {
            ttitle.setText(title);
        }
        ((TextView)d.findViewById(R.id.message)).setText(msg);
        d.findViewById(R.id.btnok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
        return d;
    }

}
