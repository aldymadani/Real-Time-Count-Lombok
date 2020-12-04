package com.example.realtimecountlombok.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.general.LoginActivity;
import com.example.realtimecountlombok.model.SuaraKecamatan;
import com.example.realtimecountlombok.util.constant.RequestCodeConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditSuaraActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = "EditSuaraActivity";

    EditText nomorTPS, desa, calonPertama, calonKedua, calonKetiga, calonKeempat, calonKelima, totalSuaraTidakSah, totalDPTTidakHadir;
    TextInputLayout nomorTPSLayout, desaLayout, calonPertamaLayout, calonKeduaLayout, calonKetigaLayout, calonKeempatLayout, calonKelimaLayout, totalSuaraTidakSahLayout, totalDPTTidakHadirLayout;
    Button konfirmasiButton, logOutButton;
    Spinner kecamatan;
    ImageView buktiSuaraImage;
    boolean imageHasChange;
    Bitmap bitmap;
    String buktiSuratSuaraURL;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    SuaraKecamatan suaraKecamatanOld = new SuaraKecamatan();
    SuaraKecamatan suaraKecamatanNew = new SuaraKecamatan();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suara);
        Intent intent = getIntent();
        suaraKecamatanOld = intent.getParcelableExtra("suaraKecamatan");
        componentsInitialization();
        konfirmasiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (suaraKecamatanOld.getEditLimit() == 2) {
                    Toast.makeText(EditSuaraActivity.this, "Anda telah melewati limit untuk mengedit suara", Toast.LENGTH_SHORT).show();
                } else if (informationValidation(calonPertama.getText().toString(), calonKedua.getText().toString(), calonKetiga.getText().toString(), calonKeempat.getText().toString(), calonKelima.getText().toString(), totalSuaraTidakSah.getText().toString(), totalDPTTidakHadir.getText().toString())) {
                    if (imageHasChange) {
                        handleUpload(bitmap);
                    } else {
                        UpdateSuaraKecamatan();
                    }
                }
            }
        });

        buktiSuaraImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditSuaraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCodeConstant.GALLERY_PICK);
                } else {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, RequestCodeConstant.GALLERY_PICK);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodeConstant.GALLERY_PICK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RequestCodeConstant.GALLERY_PICK);
            } else {
                Toast.makeText(this, "Media Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeConstant.GALLERY_PICK) {
            switch (resultCode) {
                case RESULT_OK:
                    Uri ImageURI = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageURI);
                        buktiSuaraImage.setImageBitmap(bitmap);
                        imageHasChange = true;
                    } catch (IOException e) {
                        Log.e("UpdateEventFragment", e.getLocalizedMessage());
                        e.printStackTrace();
                    }
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        final StorageReference reference = FirebaseStorage.getInstance().getReference().child(kecamatan.getSelectedItem().toString()).child(kecamatan.getSelectedItem().toString() + " " + nomorTPS.getText().toString() + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "OnSuccess: " + uri);
                        buktiSuratSuaraURL = uri.toString();
                        Log.d(TAG, buktiSuratSuaraURL);
                        UpdateSuaraKecamatan();
                    }
                });
    }

    private void UpdateSuaraKecamatan() {
        int totalSuaraBerubah = (suaraKecamatanNew.getPerolehanSuaraCalonPertama() - suaraKecamatanOld.getPerolehanSuaraCalonPertama()) + (suaraKecamatanNew.getPerolehanSuaraCalonKedua() - suaraKecamatanOld.getPerolehanSuaraCalonKedua()) +
                (suaraKecamatanNew.getPerolehanSuaraCalonKetiga() - suaraKecamatanOld.getPerolehanSuaraCalonKetiga()) + (suaraKecamatanNew.getPerolehanSuaraCalonKeempat() - suaraKecamatanOld.getPerolehanSuaraCalonKeempat()) +
                (suaraKecamatanNew.getPerolehanSuaraCalonKelima() - suaraKecamatanOld.getPerolehanSuaraCalonKelima()) + (suaraKecamatanNew.getTotalSuaraTidakSah() - suaraKecamatanOld.getTotalSuaraTidakSah()) +
                (suaraKecamatanNew.getTotalDPTTidakHadir() - suaraKecamatanOld.getTotalDPTTidakHadir());

        WriteBatch batch = db.batch();

        Log.d(TAG,suaraKecamatanOld.getSuaraKecamatanId());
        DocumentReference suaraKecamatanRef = db.collection("SuaraKecamatan").document(suaraKecamatanOld.getSuaraKecamatanId());
        // Document edit limit
        batch.update(suaraKecamatanRef, "editLimit", FieldValue.increment(1));
        if (imageHasChange) {
            batch.update(suaraKecamatanRef, "imageURL", buktiSuratSuaraURL);
        }

        if (!suaraKecamatanNew.IsSame(suaraKecamatanOld)) {
            // Update suara kecamatan
            batch.update(suaraKecamatanRef, "perolehanSuaraCalonPertama", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonPertama() - suaraKecamatanOld.getPerolehanSuaraCalonPertama()));
            batch.update(suaraKecamatanRef, "perolehanSuaraCalonKedua", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKedua() - suaraKecamatanOld.getPerolehanSuaraCalonKedua()));
            batch.update(suaraKecamatanRef, "perolehanSuaraCalonKetiga", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKetiga() - suaraKecamatanOld.getPerolehanSuaraCalonKetiga()));
            batch.update(suaraKecamatanRef, "perolehanSuaraCalonKeempat", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKeempat() - suaraKecamatanOld.getPerolehanSuaraCalonKeempat()));
            batch.update(suaraKecamatanRef, "perolehanSuaraCalonKelima", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKelima() - suaraKecamatanOld.getPerolehanSuaraCalonKelima()));
            batch.update(suaraKecamatanRef, "totalSuaraTidakSah", FieldValue.increment(suaraKecamatanNew.getTotalSuaraTidakSah() - suaraKecamatanOld.getTotalSuaraTidakSah()));
            batch.update(suaraKecamatanRef, "totalDPTTidakHadir", FieldValue.increment(suaraKecamatanNew.getTotalDPTTidakHadir() - suaraKecamatanOld.getTotalDPTTidakHadir()));

            // Update suara kecamatan keseluruhan
            DocumentReference suaraKecamatanKeseluruhanRef = db.collection("TotalSuaraKecamatan").document(suaraKecamatanOld.getNamaKecamatan());
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraKeseluruhan", FieldValue.increment(totalSuaraBerubah));
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraCalonPertama", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonPertama() - suaraKecamatanOld.getPerolehanSuaraCalonPertama()));
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraCalonKedua", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKedua() - suaraKecamatanOld.getPerolehanSuaraCalonKedua()));
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraCalonKetiga", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKetiga() - suaraKecamatanOld.getPerolehanSuaraCalonKetiga()));
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraCalonKeempat", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKeempat() - suaraKecamatanOld.getPerolehanSuaraCalonKeempat()));
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraCalonKelima", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKelima() - suaraKecamatanOld.getPerolehanSuaraCalonKelima()));
            batch.update(suaraKecamatanKeseluruhanRef, "totalSuaraTidakSah", FieldValue.increment(suaraKecamatanNew.getTotalSuaraTidakSah() - suaraKecamatanOld.getTotalSuaraTidakSah()));
            batch.update(suaraKecamatanKeseluruhanRef, "totalDPTTidakHadir", FieldValue.increment(suaraKecamatanNew.getTotalDPTTidakHadir() - suaraKecamatanOld.getTotalDPTTidakHadir()));

            // Update suara keseluruhan
            DocumentReference suaraKeseluruhanRef = db.collection("TotalSuaraKeseluruhan").document("TotalKeseluruhan");
            batch.update(suaraKeseluruhanRef, "totalSuaraKeseluruhan", FieldValue.increment(totalSuaraBerubah));
            batch.update(suaraKeseluruhanRef, "totalSuaraCalonPertama", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonPertama() - suaraKecamatanOld.getPerolehanSuaraCalonPertama()));
            batch.update(suaraKeseluruhanRef, "totalSuaraCalonKedua", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKedua() - suaraKecamatanOld.getPerolehanSuaraCalonKedua()));
            batch.update(suaraKeseluruhanRef, "totalSuaraCalonKetiga", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKetiga() - suaraKecamatanOld.getPerolehanSuaraCalonKetiga()));
            batch.update(suaraKeseluruhanRef, "totalSuaraCalonKeempat", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKeempat() - suaraKecamatanOld.getPerolehanSuaraCalonKeempat()));
            batch.update(suaraKeseluruhanRef, "totalSuaraCalonKelima", FieldValue.increment(suaraKecamatanNew.getPerolehanSuaraCalonKelima() - suaraKecamatanOld.getPerolehanSuaraCalonKelima()));
            batch.update(suaraKeseluruhanRef, "totalSuaraTidakSah", FieldValue.increment(suaraKecamatanNew.getTotalSuaraTidakSah() - suaraKecamatanOld.getTotalSuaraTidakSah()));
            batch.update(suaraKeseluruhanRef, "totalDPTTidakHadir", FieldValue.increment(suaraKecamatanNew.getTotalDPTTidakHadir() - suaraKecamatanOld.getTotalDPTTidakHadir()));
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditSuaraActivity.this, "Suara telah berhasil diupdate", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditSuaraActivity.this, MainManageSuaraActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private boolean informationValidation(String dataCalonPertama, String dataCalonKedua, String dataCalonKetiga, String dataCalonKeempat, String dataCalonKelima, String dataTotalSuaraTidakSah, String dataTotalDPTTidakHadir) {
        boolean isValid = false;

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

        if (calonPertamaValid && calonKeduaValid && calonKetigaValid && calonKeempatValid && calonKelimaValid && totalSuaraTidakSahValid && totalDPTTidakHadirValid) {
            suaraKecamatanNew.setPerolehanSuaraCalonPertama(Integer.parseInt(dataCalonPertama));
            suaraKecamatanNew.setPerolehanSuaraCalonKedua(Integer.parseInt(dataCalonKedua));
            suaraKecamatanNew.setPerolehanSuaraCalonKetiga(Integer.parseInt(dataCalonKetiga));
            suaraKecamatanNew.setPerolehanSuaraCalonKeempat(Integer.parseInt(dataCalonKeempat));
            suaraKecamatanNew.setPerolehanSuaraCalonKelima(Integer.parseInt(dataCalonKelima));
            suaraKecamatanNew.setTotalSuaraTidakSah(Integer.parseInt(dataTotalSuaraTidakSah));
            suaraKecamatanNew.setTotalDPTTidakHadir(Integer.parseInt(dataTotalDPTTidakHadir));
            Log.d(TAG, String.valueOf(suaraKecamatanNew.getTotalDPTTidakHadir()));
            Log.d(TAG, String.valueOf(suaraKecamatanOld.getTotalDPTTidakHadir()));
            boolean isSame = suaraKecamatanNew.IsSame(suaraKecamatanOld);
            if (!isSame || imageHasChange) {
                isValid = true;
            } else {
                Toast.makeText(this, "Tidak ada perubahan data", Toast.LENGTH_SHORT).show();
            }
        }

        return isValid;
    }

    private void componentsInitialization() {
        kecamatan = findViewById(R.id.editSuaraKecamatanSpinner);
        nomorTPS = findViewById(R.id.editSuaraNomorTPS);
        desa = findViewById(R.id.editSuaraDesa);
        calonPertama = findViewById(R.id.editSuaraCalonSatu);
        calonKedua = findViewById(R.id.editSuaraCalonDua);
        calonKetiga = findViewById(R.id.editSuaraCalonTiga);
        calonKeempat = findViewById(R.id.editSuaraCalonEmpat);
        calonKelima = findViewById(R.id.editSuaraCalonLima);
        totalSuaraTidakSah = findViewById(R.id.editSuaraTotalSuaraTidakSah);
        totalDPTTidakHadir = findViewById(R.id.editSuaraTotalSuaraTidakHadir);

        nomorTPSLayout = findViewById(R.id.editSuaraNomorTPSLayout);
        desaLayout = findViewById(R.id.editSuaraDesaLayout);
        calonPertamaLayout = findViewById(R.id.editSuaraCalonSatuLayout);
        calonKeduaLayout = findViewById(R.id.editSuaraCalonDuaLayout);
        calonKetigaLayout = findViewById(R.id.editSuaraCalonTigaLayout);
        calonKeempatLayout = findViewById(R.id.editSuaraCalonEmpatLayout);
        calonKelimaLayout = findViewById(R.id.editSuaraCalonLimaLayout);
        totalSuaraTidakSahLayout = findViewById(R.id.editSuaraTotalSuaraTidakSahLayout);
        totalDPTTidakHadirLayout = findViewById(R.id.editSuaraTotalSuaraTidakHadirLayout);

        konfirmasiButton = findViewById(R.id.editSuaraKonfirmasiButton);
        logOutButton = findViewById(R.id.editSuaraLogOut);

        buktiSuaraImage = findViewById(R.id.editSuaraBuktiSuratSuaraImage);

        nomorTPS.setOnFocusChangeListener(this);
        desa.setOnFocusChangeListener(this);
        calonPertama.setOnFocusChangeListener(this);
        calonKedua.setOnFocusChangeListener(this);
        calonKetiga.setOnFocusChangeListener(this);
        calonKeempat.setOnFocusChangeListener(this);
        calonKelima.setOnFocusChangeListener(this);
        totalSuaraTidakSah.setOnFocusChangeListener(this);
        totalDPTTidakHadir.setOnFocusChangeListener(this);

        String listKecamatan[] = {suaraKecamatanOld.getNamaKecamatan()};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listKecamatan);
        kecamatan.setAdapter(arrayAdapter);
        nomorTPS.setText(suaraKecamatanOld.getNomorTPS());
        desa.setText(suaraKecamatanOld.getDesa());
        calonPertama.setText(String.valueOf(suaraKecamatanOld.getPerolehanSuaraCalonPertama()));
        calonKedua.setText(String.valueOf(suaraKecamatanOld.getPerolehanSuaraCalonKedua()));
        calonKetiga.setText(String.valueOf(suaraKecamatanOld.getPerolehanSuaraCalonKetiga()));
        calonKeempat.setText(String.valueOf(suaraKecamatanOld.getPerolehanSuaraCalonKeempat()));
        calonKelima.setText(String.valueOf(suaraKecamatanOld.getPerolehanSuaraCalonKelima()));
        totalSuaraTidakSah.setText(String.valueOf(suaraKecamatanOld.getTotalSuaraTidakSah()));
        totalDPTTidakHadir.setText(String.valueOf(suaraKecamatanOld.getTotalDPTTidakHadir()));
        Picasso.get().load(suaraKecamatanOld.getImageURL()).error(R.drawable.ic_baseline_error_outline_24).into(buktiSuaraImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
//                imageEventPhotoProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
//                imageEventPhotoProgressBar.setVisibility(View.GONE);
                Toast.makeText(EditSuaraActivity.this, "Error in loading the image", Toast.LENGTH_SHORT).show();
            }
        });

        nomorTPS.setFocusableInTouchMode(false);
        nomorTPS.setFocusable(false);
        nomorTPS.setClickable(false);

        desa.setFocusableInTouchMode(false);
        desa.setFocusable(false);
        desa.setClickable(false);

        imageHasChange = false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}