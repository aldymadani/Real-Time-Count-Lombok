package com.example.realtimecountlombok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.realtimecountlombok.model.SuaraKecamatan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class InputSuaraActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    EditText nomorTPS, desa, calonPertama, calonKedua, calonKetiga, calonKeempat, calonKelima, totalSuaraTidakSah, totalDPTTidakHadir;
    TextInputLayout nomorTPSLayout, desaLayout, calonPertamaLayout, calonKeduaLayout, calonKetigaLayout, calonKeempatLayout, calonKelimaLayout, totalSuaraTidakSahLayout, totalDPTTidakHadirLayout;
    Button konfirmasiButton, logOutButton;
    Spinner kecamatan;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String suaraKecamatanId = db.collection("SuaraKecamatan").document().getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_suara);
        componentsInitialization();

        konfirmasiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (informationValidation(nomorTPS.getText().toString(), desa.getText().toString(), calonPertama.getText().toString(), calonKedua.getText().toString(), calonKetiga.getText().toString(), calonKeempat.getText().toString(), calonKelima.getText().toString(), totalSuaraTidakSah.getText().toString(), totalDPTTidakHadir.getText().toString())) {
                    IntializeSuara(kecamatan.getSelectedItem().toString(), nomorTPS.getText().toString(), desa.getText().toString(), calonPertama.getText().toString(), calonKedua.getText().toString(), calonKetiga.getText().toString(), calonKeempat.getText().toString(), calonKelima.getText().toString(), totalSuaraTidakSah.getText().toString(), totalDPTTidakHadir.getText().toString());
                } else {
                    Toast.makeText(InputSuaraActivity.this, "Tolong isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(InputSuaraActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void IntializeSuara(String dataKecamatan, String dataNomorTPS, String dataDesa, String dataCalonPertama, String dataCalonKedua, String dataCalonKetiga, String dataCalonKeempat, String dataCalonKelima, String dataTotalSuaraTidakSah, String dataTotalDPTTidakHadir) {
        SuaraKecamatan suaraKecamatan = new SuaraKecamatan();
        suaraKecamatan.setNamaKecamatan(dataKecamatan);
        suaraKecamatan.setNomorTPS(dataNomorTPS);
        suaraKecamatan.setDesa(dataDesa);
        suaraKecamatan.setPerolehanSuaraCalonPertama(Integer.parseInt(dataCalonPertama));
        suaraKecamatan.setPerolehanSuaraCalonKedua(Integer.parseInt(dataCalonKedua));
        suaraKecamatan.setPerolehanSuaraCalonKetiga(Integer.parseInt(dataCalonKetiga));
        suaraKecamatan.setPerolehanSuaraCalonKeempat(Integer.parseInt(dataCalonKeempat));
        suaraKecamatan.setPerolehanSuaraCalonKelima(Integer.parseInt(dataCalonKelima));
        suaraKecamatan.setTotalSuaraTidakSah(Integer.parseInt(dataTotalSuaraTidakSah));
        suaraKecamatan.setTotalDPTTidakHadir(Integer.parseInt(dataTotalDPTTidakHadir));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        suaraKecamatan.setCreatedBy(user.getEmail());
        db.collection("SuaraKecamatan").document(suaraKecamatanId).set(suaraKecamatan)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        tambahkanTotalSuaraKecamatan(dataKecamatan, Integer.parseInt(dataCalonPertama), Integer.parseInt(dataCalonKedua), Integer.parseInt(dataCalonKetiga), Integer.parseInt(dataCalonKeempat), Integer.parseInt(dataCalonKelima), Integer.parseInt(dataTotalSuaraTidakSah), Integer.parseInt(dataTotalDPTTidakHadir));
                    }
                });
    }

    private void tambahkanTotalSuaraKecamatan(String namaKecamatan, int dataCalonPertama, int dataCalonKedua, int dataCalonKetiga, int dataCalonKeempat, int dataCalonKelima, int dataTotalSuaraTidakSah, int dataTotalDPTTidakHadir) {
        int totalSuara = dataCalonPertama + dataCalonKedua + dataCalonKetiga + dataCalonKeempat + dataCalonKelima;
        WriteBatch batch = db.batch();

        // Update total suara kecamatan
        DocumentReference suaraKecamatanRef = db.collection("TotalSuaraKecamatan").document(namaKecamatan);
        batch.update(suaraKecamatanRef, "totalSuaraKeseluruhan", FieldValue.increment(totalSuara));
        batch.update(suaraKecamatanRef, "totalSuaraCalonPertama", FieldValue.increment(dataCalonPertama));
        batch.update(suaraKecamatanRef, "totalSuaraCalonKedua", FieldValue.increment(dataCalonKedua));
        batch.update(suaraKecamatanRef, "totalSuaraCalonKetiga", FieldValue.increment(dataCalonKetiga));
        batch.update(suaraKecamatanRef, "totalSuaraCalonKeempat", FieldValue.increment(dataCalonKeempat));
        batch.update(suaraKecamatanRef, "totalSuaraCalonKelima", FieldValue.increment(dataCalonKelima));
        batch.update(suaraKecamatanRef, "totalSuaraTidakSah", FieldValue.increment(dataTotalSuaraTidakSah));
        batch.update(suaraKecamatanRef, "totalDPTTidakHadir", FieldValue.increment(dataTotalDPTTidakHadir));

        // Update total suara keseluruhan
        DocumentReference suaraKeseluruhanRef = db.collection("TotalSuaraKeseluruhan").document("TotalKeseluruhan");
        batch.update(suaraKeseluruhanRef, "totalSuaraKeseluruhan", FieldValue.increment(totalSuara));
        batch.update(suaraKeseluruhanRef, "totalSuaraCalonPertama", FieldValue.increment(dataCalonPertama));
        batch.update(suaraKeseluruhanRef, "totalSuaraCalonKedua", FieldValue.increment(dataCalonKedua));
        batch.update(suaraKeseluruhanRef, "totalSuaraCalonKetiga", FieldValue.increment(dataCalonKetiga));
        batch.update(suaraKeseluruhanRef, "totalSuaraCalonKeempat", FieldValue.increment(dataCalonKeempat));
        batch.update(suaraKeseluruhanRef, "totalSuaraCalonKelima", FieldValue.increment(dataCalonKelima));
        batch.update(suaraKeseluruhanRef, "totalSuaraTidakSah", FieldValue.increment(dataTotalSuaraTidakSah));
        batch.update(suaraKeseluruhanRef, "totalDPTTidakHadir", FieldValue.increment(dataTotalDPTTidakHadir));

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(InputSuaraActivity.this, "Total Suara Telah Berhasil Dimasukkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean informationValidation(String dataNomorTPS, String dataDesa, String dataCalonPertama, String dataCalonKedua, String dataCalonKetiga, String dataCalonKeempat, String dataCalonKelima, String dataTotalSuaraTidakSah, String dataTotalDPTTidakHadir) {
        boolean isValid = false;

        boolean noTPSValid = true;
        if (dataNomorTPS.isEmpty()) {
            nomorTPSLayout.setError("Tolong isi informasi nomor TPS");
            noTPSValid = false;
        }

        boolean desaValid = true;
        if (dataDesa.isEmpty()) {
            desaLayout.setError("Tolong isi informasi desa");
            desaValid = false;
        }

        boolean calonPertamaValid = true;
        if (dataCalonPertama.isEmpty()) {
            calonPertamaLayout.setError("Tolong isi total perolehan suara calon pertama");
            calonPertamaValid = false;
        }

        boolean calonKeduaValid = true;
        if (dataCalonKedua.isEmpty()) {
            calonKeduaLayout.setError("Tolong isi total perolehan suara calon kedua");
            calonKeduaValid = false;
        }

        boolean calonKetigaValid = true;
        if (dataCalonKetiga.isEmpty()) {
            calonKetigaLayout.setError("Tolong isi total perolehan suara calon ketiga");
            calonKetigaValid = false;
        }

        boolean calonKeempatValid = true;
        if (dataCalonKeempat.isEmpty()) {
            calonKeempatLayout.setError("Tolong isi total perolehan suara calon keempat");
            calonKeempatValid = false;
        }

        boolean calonKelimaValid = true;
        if (dataCalonKelima.isEmpty()) {
            calonKelimaLayout.setError("Tolong isi total perolehan suara calon kelima");
            calonKelimaValid = false;
        }

        boolean totalSuaraTidakSahValid = true;
        if (dataTotalSuaraTidakSah.isEmpty()) {
            totalSuaraTidakSahLayout.setError("Tolong isi total suara tidak sah");
            totalSuaraTidakSahValid = false;
        }

        boolean totalDPTTidakHadirValid = true;
        if (dataTotalDPTTidakHadir.isEmpty()) {
            totalDPTTidakHadirLayout.setError("Tolong isi total DPT yang tidak hadir");
            totalDPTTidakHadirValid = false;
        }

        if (noTPSValid && desaValid && calonPertamaValid && calonKeduaValid && calonKetigaValid && calonKeempatValid && calonKelimaValid && totalSuaraTidakSahValid && totalDPTTidakHadirValid) {
            isValid = true;
        }

        return isValid;
    }

    private void componentsInitialization() {
        kecamatan = findViewById(R.id.inputSuaraKecamatanSpinner);
        nomorTPS = findViewById(R.id.inputSuaraNomorTPS);
        desa = findViewById(R.id.inputSuaraDesa);
        calonPertama = findViewById(R.id.inputSuaraCalonSatu);
        calonKedua = findViewById(R.id.inputSuaraCalonDua);
        calonKetiga = findViewById(R.id.inputSuaraCalonTiga);
        calonKeempat = findViewById(R.id.inputSuaraCalonEmpat);
        calonKelima = findViewById(R.id.inputSuaraCalonLima);
        totalSuaraTidakSah = findViewById(R.id.inputSuaraTotalSuaraTidakSah);
        totalDPTTidakHadir = findViewById(R.id.inputSuaraTotalSuaraTidakHadir);

        nomorTPSLayout = findViewById(R.id.inputSuaraNomorTPSLayout);
        desaLayout = findViewById(R.id.inputSuaraDesaLayout);
        calonPertamaLayout = findViewById(R.id.inputSuaraCalonSatuLayout);
        calonKeduaLayout = findViewById(R.id.inputSuaraCalonDuaLayout);
        calonKetigaLayout = findViewById(R.id.inputSuaraCalonTigaLayout);
        calonKeempatLayout = findViewById(R.id.inputSuaraCalonEmpatLayout);
        calonKelimaLayout = findViewById(R.id.inputSuaraCalonLimaLayout);
        totalSuaraTidakSahLayout = findViewById(R.id.inputSuaraTotalSuaraTidakSahLayout);
        totalDPTTidakHadirLayout = findViewById(R.id.inputSuaraTotalSuaraTidakHadirLayout);

        konfirmasiButton = findViewById(R.id.inputSuaraKonfirmasiButton);
        logOutButton = findViewById(R.id.inputSuaraLogOut);

        nomorTPS.setOnFocusChangeListener(this);
        desa.setOnFocusChangeListener(this);
        calonPertama.setOnFocusChangeListener(this);
        calonKedua.setOnFocusChangeListener(this);
        calonKetiga.setOnFocusChangeListener(this);
        calonKeempat.setOnFocusChangeListener(this);
        calonKelima.setOnFocusChangeListener(this);
        totalSuaraTidakSah.setOnFocusChangeListener(this);
        totalDPTTidakHadir.setOnFocusChangeListener(this);

        String listKecamatan[] = {"Kec. Praya", "Kec. Praya Tengah", "Kec. Praya Barat", "Kec. Praya Barat Daya", "Kec. Praya Timur",
                "Kec. Pujut", "Kec. Janapria", "Kec. Batukliang", "Kec. Batukliang Utara", "Kec. Jonggat", "Kec. Kopang", "Kec. Pringgarata"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listKecamatan);
        kecamatan.setAdapter(arrayAdapter);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.inputSuaraNomorTPS:
                nomorTPSLayout.setErrorEnabled(false);
                break;
            case R.id.inputSuaraDesa:
                desaLayout.setErrorEnabled(false);
                break;
            case R.id.inputSuaraCalonSatu:
                calonPertamaLayout.setErrorEnabled(false);
                break;
            case R.id.inputSuaraCalonDua:
                calonKeduaLayout.setErrorEnabled(false);
                break;
            case R.id.inputSuaraCalonTiga:
                calonKetigaLayout.setErrorEnabled(false);
                break;
            case R.id.inputSuaraCalonEmpat:
                calonKeempatLayout.setErrorEnabled(false);
                break;
            case R.id.inputSuaraCalonLima:
                calonKelimaLayout.setErrorEnabled(false);
                break;
        }
    }
}