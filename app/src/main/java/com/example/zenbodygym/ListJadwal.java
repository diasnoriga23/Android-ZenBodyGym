package com.example.zenbodygym;

public class ListJadwal {
    String nama_leng,username,alamat;

    public ListJadwal() {
    }

    public ListJadwal(String nama_leng, String username, String alamat) {
        this.nama_leng = nama_leng;
        this.username = username;
        this.alamat = alamat;

    }

    public String getNama_leng() {
        return nama_leng;
    }

    public void setNama_leng(String nama_leng) {
        this.nama_leng = nama_leng;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}