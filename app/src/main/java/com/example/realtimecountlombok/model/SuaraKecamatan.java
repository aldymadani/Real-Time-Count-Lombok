package com.example.realtimecountlombok.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SuaraKecamatan implements Parcelable {
    private String namaKecamatan;
    private String nomorTPS;
    private String desa;
    private int perolehanSuaraCalonPertama;
    private int perolehanSuaraCalonKedua;
    private int perolehanSuaraCalonKetiga;
    private int perolehanSuaraCalonKeempat;
    private int perolehanSuaraCalonKelima;
    private int totalSuaraTidakSah;
    private int totalDPTTidakHadir;
    private String createdBy;
    private String imageURL;
    private String suaraKecamatanId;
    private int editLimit;
    private @ServerTimestamp Date timestamp;

    public SuaraKecamatan() {
        // Empty constructor
    }

    public boolean IsSame(SuaraKecamatan suaraKecamatan) {
        boolean isSame = true;
        if (perolehanSuaraCalonPertama != suaraKecamatan.getPerolehanSuaraCalonPertama()) {
            isSame = false;
        }

        if (perolehanSuaraCalonKedua != suaraKecamatan.getPerolehanSuaraCalonKedua()) {
            isSame = false;
        }

        if (perolehanSuaraCalonKetiga != suaraKecamatan.getPerolehanSuaraCalonKetiga()) {
            isSame = false;
        }

        if (perolehanSuaraCalonKeempat != suaraKecamatan.getPerolehanSuaraCalonKeempat()) {
            isSame = false;
        }

        if (perolehanSuaraCalonKelima != suaraKecamatan.getPerolehanSuaraCalonKelima()) {
            isSame = false;
        }

        if (totalSuaraTidakSah != suaraKecamatan.getTotalSuaraTidakSah()) {
            isSame = false;
        }

        if (totalDPTTidakHadir != suaraKecamatan.getTotalDPTTidakHadir()) {
            isSame = false;
        }
        return isSame;
    }

    public String getNamaKecamatan() {
        return namaKecamatan;
    }

    public void setNamaKecamatan(String namaKecamatan) {
        this.namaKecamatan = namaKecamatan;
    }

    public String getNomorTPS() {
        return nomorTPS;
    }

    public void setNomorTPS(String nomorTPS) {
        this.nomorTPS = nomorTPS;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public int getPerolehanSuaraCalonPertama() {
        return perolehanSuaraCalonPertama;
    }

    public void setPerolehanSuaraCalonPertama(int perolehanSuaraCalonPertama) {
        this.perolehanSuaraCalonPertama = perolehanSuaraCalonPertama;
    }

    public int getPerolehanSuaraCalonKedua() {
        return perolehanSuaraCalonKedua;
    }

    public void setPerolehanSuaraCalonKedua(int perolehanSuaraCalonKedua) {
        this.perolehanSuaraCalonKedua = perolehanSuaraCalonKedua;
    }

    public int getPerolehanSuaraCalonKetiga() {
        return perolehanSuaraCalonKetiga;
    }

    public void setPerolehanSuaraCalonKetiga(int perolehanSuaraCalonKetiga) {
        this.perolehanSuaraCalonKetiga = perolehanSuaraCalonKetiga;
    }

    public int getPerolehanSuaraCalonKeempat() {
        return perolehanSuaraCalonKeempat;
    }

    public void setPerolehanSuaraCalonKeempat(int perolehanSuaraCalonKeempat) {
        this.perolehanSuaraCalonKeempat = perolehanSuaraCalonKeempat;
    }

    public int getPerolehanSuaraCalonKelima() {
        return perolehanSuaraCalonKelima;
    }

    public void setPerolehanSuaraCalonKelima(int perolehanSuaraCalonKelima) {
        this.perolehanSuaraCalonKelima = perolehanSuaraCalonKelima;
    }

    public int getTotalSuaraTidakSah() {
        return totalSuaraTidakSah;
    }

    public void setTotalSuaraTidakSah(int totalSuaraTidakSah) {
        this.totalSuaraTidakSah = totalSuaraTidakSah;
    }

    public int getTotalDPTTidakHadir() {
        return totalDPTTidakHadir;
    }

    public void setTotalDPTTidakHadir(int totalDPTTidakHadir) {
        this.totalDPTTidakHadir = totalDPTTidakHadir;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSuaraKecamatanId() {
        return suaraKecamatanId;
    }

    public void setSuaraKecamatanId(String suaraKecamatanId) {
        this.suaraKecamatanId = suaraKecamatanId;
    }

    public int getEditLimit() {
        return editLimit;
    }

    public void setEditLimit(int editLimit) {
        this.editLimit = editLimit;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    protected SuaraKecamatan(Parcel in) {
        namaKecamatan = in.readString();
        nomorTPS = in.readString();
        desa = in.readString();
        perolehanSuaraCalonPertama = in.readInt();
        perolehanSuaraCalonKedua = in.readInt();
        perolehanSuaraCalonKetiga = in.readInt();
        perolehanSuaraCalonKeempat = in.readInt();
        perolehanSuaraCalonKelima = in.readInt();
        totalSuaraTidakSah = in.readInt();
        totalDPTTidakHadir = in.readInt();
        createdBy = in.readString();
        imageURL = in.readString();
        suaraKecamatanId = in.readString();
        editLimit = in.readInt();
    }

    public static final Creator<SuaraKecamatan> CREATOR = new Creator<SuaraKecamatan>() {
        @Override
        public SuaraKecamatan createFromParcel(Parcel in) {
            return new SuaraKecamatan(in);
        }

        @Override
        public SuaraKecamatan[] newArray(int size) {
            return new SuaraKecamatan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(namaKecamatan);
        parcel.writeString(nomorTPS);
        parcel.writeString(desa);
        parcel.writeInt(perolehanSuaraCalonPertama);
        parcel.writeInt(perolehanSuaraCalonKedua);
        parcel.writeInt(perolehanSuaraCalonKetiga);
        parcel.writeInt(perolehanSuaraCalonKeempat);
        parcel.writeInt(perolehanSuaraCalonKelima);
        parcel.writeInt(totalSuaraTidakSah);
        parcel.writeInt(totalDPTTidakHadir);
        parcel.writeString(createdBy);
        parcel.writeString(imageURL);
        parcel.writeString(suaraKecamatanId);
        parcel.writeInt(editLimit);
    }

}
