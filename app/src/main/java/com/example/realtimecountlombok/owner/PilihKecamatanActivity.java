package com.example.realtimecountlombok.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.admin.MainManageSuaraActivity;
import com.example.realtimecountlombok.model.SuaraKecamatan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PilihKecamatanActivity extends AppCompatActivity {
    private static final String TAG = "PilihKecamatanActivity";
    Spinner kecamatan;
    TextInputLayout desaLayout, nomorTPSLayout;
    EditText desa, nomorTPS;
    Button konfirmasiButton, logOutButton;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_kecamatan);
        InitializeComponents();
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PilihKecamatanActivity.this, MainManageSuaraActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        konfirmasiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataIsValid(desa.getText().toString(), nomorTPS.getText().toString())) {
                    AmbilDataDesa(desa.getText().toString(), nomorTPS.getText().toString());
                } else {
                    Toast.makeText(PilihKecamatanActivity.this, "Tolong isi semua data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AmbilDataDesa(String dataDesa, String dataNomorTPS) {
        db.collection("SuaraKecamatan").whereEqualTo("namaKecamatan", kecamatan.getSelectedItem().toString())
                .whereEqualTo("desa", dataDesa).whereEqualTo("nomorTPS", dataNomorTPS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Toast.makeText(PilihKecamatanActivity.this, "TPS Belum Memasukkan Data", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            SuaraKecamatan suaraKecamatan = document.toObject(SuaraKecamatan.class);
                            Log.d(TAG,String.valueOf(suaraKecamatan.getPerolehanSuaraCalonPertama()));
                            Intent intent = new Intent(PilihKecamatanActivity.this, EditSuaraOwnerActivity.class);
                            intent.putExtra("SuaraKecamatan", suaraKecamatan);
                            startActivity(intent);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private boolean DataIsValid(String dataDesa, String dataNomorTPS) {
        boolean isValid = false;
        boolean desaValidation = false;
        if (dataDesa.isEmpty()) {
            desaLayout.setError("Tolong pilih desa");
        } else {
            desaValidation = true;
        }

        boolean nomorTPSValidation = false;
        if (dataNomorTPS.isEmpty()) {
            nomorTPSLayout.setError("Tolong pilih TPS");
        } else {
            nomorTPSValidation = true;
        }

        if (desaValidation && nomorTPSValidation) {
            isValid = true;
        }
        return isValid;
    }

    private void InitializeComponents() {
        kecamatan = findViewById(R.id.pilihKecamatanSpinner);
        desa = findViewById(R.id.pilihKecamatanDesa);
        nomorTPS = findViewById(R.id.pilihKecamatanNomorTPS);
        desaLayout = findViewById(R.id.pilihKecamatanDesaLayout);
        nomorTPSLayout = findViewById(R.id.pilihKecamatanNomorTPSLayout);
        konfirmasiButton = findViewById(R.id.pilihKecamatanKonfirmasiButton);
        logOutButton = findViewById(R.id.pilihKecamatanLogOutButton);

        String listKecamatan[] = {"Kec. Praya", "Kec. Praya Tengah", "Kec. Praya Barat", "Kec. Praya Barat Daya", "Kec. Praya Timur",
                "Kec. Pujut", "Kec. Janapria", "Kec. Batukliang", "Kec. Batukliang Utara", "Kec. Jonggat", "Kec. Kopang", "Kec. Pringgarata"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listKecamatan);
        kecamatan.setAdapter(arrayAdapter);
    }
}