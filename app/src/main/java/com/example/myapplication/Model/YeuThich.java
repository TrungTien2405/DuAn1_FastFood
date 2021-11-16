package com.example.myapplication.Model;

public class YeuThich {
    private String MaNH;
    private String MaTK;
    private String MaYT;

    public YeuThich(String maNH, String maTK, String maYT) {
        MaNH = maNH;
        MaTK = maTK;
        MaYT = maYT;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getMaTK() {
        return MaTK;
    }

    public void setMaTK(String maTK) {
        MaTK = maTK;
    }

    public String getMaYT() {
        return MaYT;
    }

    public void setMaYT(String maYT) {
        MaYT = maYT;
    }
}
