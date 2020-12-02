package com.example.realtimecountlombok.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SuaraKecamatan {
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
    private @ServerTimestamp Date timestamp;

    public SuaraKecamatan() {
        // Empty constructor
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
