package com.example.myapplication.Model;

public class GioHang {

    private String MaGH;
    private String MaTK;


    public GioHang(String maGH, String maTK) {
        MaGH = maGH;
        MaTK = maTK;
    }

    public String getMaGH() {
        return MaGH;
    }

    public void setMaGH(String maGH) {
        MaGH = maGH;
    }

    public String getMaTK() {
        return MaTK;
    }

    public void setMaTK(String maTK) {
        MaTK = maTK;
    }
}
