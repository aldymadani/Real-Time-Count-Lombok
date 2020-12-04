package com.example.realtimecountlombok.general;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
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
import com.example.realtimecountlombok.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TotalSuaraActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    Pie pie;
    ListenerRegistration countListener;
    Button suaraTidakSah, totalDPTTidakHadir, totalSuaraMasuk, persentaseSuaraMasuk, totalTPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_suara);
        anyChartView = findViewById(R.id.totalSuaraChart);
        setUpPieChart();

        suaraTidakSah = findViewById(R.id.totalSuaraKeseluruhanSuaraTidakSah);
        totalDPTTidakHadir = findViewById(R.id.totalSuaraKeseluruhanSuaraDPTTidakHadir);
        totalSuaraMasuk = findViewById(R.id.totalSuaraKeseluruhanMasuk);
        persentaseSuaraMasuk = findViewById(R.id.totalSuaraKeseluruhanPersentaseSuaraMasuk);
        totalTPS = findViewById(R.id.totalSuaraKeseluruhanTPS);

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(TotalSuaraActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpPieChart() {
        pie = AnyChart.pie();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("TotalSuaraKeseluruhan").document("TotalKeseluruhan");
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

                    suaraTidakSah.setText(String.valueOf(value.getLong("totalSuaraTidakSah")));
                    totalDPTTidakHadir.setText(String.valueOf(value.getLong("totalDPTTidakHadir")));
                    double dataTotalSuaraMasuk = value.getLong("totalSuaraCalonPertama") + value.getLong("totalSuaraCalonKedua") +
                            value.getLong("totalSuaraCalonKetiga") + value.getLong("totalSuaraCalonKeempat") +
                            value.getLong("totalSuaraCalonKelima") + value.getLong("totalSuaraTidakSah") +
                            value.getLong("totalDPTTidakHadir");
                    double dataPersentaseSuaraMasuk = (dataTotalSuaraMasuk / value.getLong("totalPemilih").doubleValue()) * 100;
                    DecimalFormat df = new DecimalFormat("#.##");
                    String formattedDataPersentaseSuaraMasuk = df.format(dataPersentaseSuaraMasuk);
                    totalSuaraMasuk.setText(String.valueOf((int) dataTotalSuaraMasuk));
                    persentaseSuaraMasuk.setText(formattedDataPersentaseSuaraMasuk + "%");
                    totalTPS.setText(String.valueOf(value.getLong("totalTPS")));
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
                    Toast.makeText(TotalSuaraActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        anyChartView.setChart(pie);
    }
}