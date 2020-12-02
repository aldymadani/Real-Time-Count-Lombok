package com.example.realtimecountlombok;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class TotalSuaraKecamatanActivity extends AppCompatActivity {

    AnyChartView chartKecamatan;
    Pie pie;
    ListenerRegistration countListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_suara_kecamatan);

        String namaKecamatan = getIntent().getStringExtra("namaKecamatan");

        chartKecamatan = findViewById(R.id.totalSuaraKecamatanChart);
        setUpPieChart(namaKecamatan);

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(TotalSuaraKecamatanActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpPieChart(String namaKecamatan) {
        pie = AnyChart.pie();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("TotalSuaraKecamatan").document(namaKecamatan);
        countListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Calon Pertama", value.getLong("totalSuaraCalonPertama")));
                    data.add(new ValueDataEntry("Calon Kedua", value.getLong("totalSuaraCalonKedua")));
                    data.add(new ValueDataEntry("Calon Ketiga", value.getLong("totalSuaraCalonKetiga")));
                    data.add(new ValueDataEntry("Calon Keempat", value.getLong("totalSuaraCalonKeempat")));
                    data.add(new ValueDataEntry("Calon Kelima", value.getLong("totalSuaraCalonKelima")));
                    data.add(new ValueDataEntry("Total Suara Tidak Sah", value.getLong("totalSuaraTidakSah")));
                    data.add(new ValueDataEntry("Total DPT Tidak Hadir", value.getLong("totalDPTTidakHadir")));
                    pie.title("Real Time Count 2021");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Total Suara")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);
                    pie.data(data);
                } else {
                    Toast.makeText(TotalSuaraKecamatanActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        pie.data(data);

        chartKecamatan.setChart(pie);
    }
}