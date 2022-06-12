package com.example.zenbodygym;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Progress extends AppCompatActivity {

    List<Entry> motot = new ArrayList<>();
    List<Entry> mlemak = new ArrayList<>();
    List<Entry> bbadan = new ArrayList<>();
    LineChart lineChart;
    List<String> labels = new ArrayList<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        lineChart = findViewById(R.id.lineChart);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateY(1000);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getXAxis().setValueFormatter(new XAxsisVFormat());
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));
        getData();
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("usernamekey",MODE_PRIVATE);
        String uname = sharedPreferences.getString("", "");

        pd.show();
        FirebaseDatabase.getInstance().getReference("Report/" + uname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                int i = 0;
                for (DataSnapshot d : snapshot.getChildren()) {
                    labels.add(d.getKey());
                    motot.add(new Entry(i, d.child("motot").getValue(Float.class)));
                    mlemak.add(new Entry(i, d.child("mlemak").getValue(Float.class)));
                    bbadan.add(new Entry(i, d.child("bbadan").getValue(Float.class)));
                    i++;
                }
                if (i > 0) {
                    if (i < 2){
                        labels.add(0, "Begin");
                        motot.add(0, new Entry(0, 0));
                        mlemak.add(0, new Entry(0, 0));
                        bbadan.add(0, new Entry(0, 0));
                        motot.get(1).setX(1);
                        mlemak.get(1).setX(1);
                        bbadan.get(1).setX(1);
                    }
                    if (labels.size() > 0){
                        LineDataSet ldmotot = new LineDataSet(motot, "Masa Otot");
                        ldmotot.setAxisDependency(YAxis.AxisDependency.LEFT);
                        ldmotot.setHighlightEnabled(true);
                        ldmotot.setLineWidth(2);
                        ldmotot.setColor(Color.RED);
                        ldmotot.setDrawHighlightIndicators(true);
                        ldmotot.setHighLightColor(Color.RED);
                        ldmotot.setValueTextSize(12);
                        ldmotot.setValueTextColor(Color.WHITE);

                        LineDataSet ldmlemak = new LineDataSet(mlemak, "Masa Lemak");
                        ldmlemak.setAxisDependency(YAxis.AxisDependency.LEFT);
                        ldmlemak.setHighlightEnabled(true);
                        ldmlemak.setLineWidth(2);
                        ldmlemak.setColor(Color.GREEN);
                        ldmlemak.setDrawHighlightIndicators(true);
                        ldmlemak.setHighLightColor(Color.GREEN);
                        ldmlemak.setValueTextSize(12);
                        ldmlemak.setValueTextColor(Color.WHITE);
//
                        LineDataSet ldbbadan = new LineDataSet(bbadan, "Berat Badan");
                        ldbbadan.setAxisDependency(YAxis.AxisDependency.LEFT);
                        ldbbadan.setHighlightEnabled(true);
                        ldbbadan.setLineWidth(2);
                        ldbbadan.setColor(Color.BLUE);
                        ldbbadan.setDrawHighlightIndicators(true);
                        ldbbadan.setHighLightColor(Color.BLUE);
                        ldbbadan.setValueTextSize(12);
                        ldbbadan.setValueTextColor(Color.WHITE);

                        lineChart.setData(new LineData(ldmotot, ldmlemak, ldbbadan));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.show();
            }
        });

    }

    class XAxsisVFormat implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
//            return labels.get((int)value);
            return "";
        }
    }
}
