package com.example.myapplication.Model;

public class GioHang {
    String MaGH;
    String MaMA;
    String MaTK;
    int SoLuong;
    String TenMonThem;
    int TongTien;

    public GioHang(String maGH, String maMA, String maTK, int soLuong, String tenMonThem, int tongTien) {
        MaGH = maGH;
        MaMA = maMA;
        MaTK = maTK;
        SoLuong = soLuong;
        TenMonThem = tenMonThem;
        TongTien = tongTien;
    }

    public String getMaGH() {
        return MaGH;
    }

    public void setMaGH(String maGH) {
        MaGH = maGH;
    }

    public String getMaMA() {
        return MaMA;
    }

    public void setMaMA(String maMA) {
        MaMA = maMA;
    }

    public String getMaTK() {
        return MaTK;
    }

    public void setMaTK(String maTK) {
        MaTK = maTK;
    }

    public int getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(int soLuong) {
        SoLuong = soLuong;
    }

    public String getTenMonThem() {
        return TenMonThem;
    }

    public void setTenMonThem(String tenMonThem) {
        TenMonThem = tenMonThem;
    }

    public int getTongTien() {
        return TongTien;
    }

    public void setTongTien(int tongTien) {
        TongTien = tongTien;
    }
}
