package com.example.myapplication.Model;

public class LoaiNhaHang {
    private String MaLoaiNH;
    private String TenLoaiNH;
    private String HinhAnh;

    public LoaiNhaHang(String maLoaiNH, String tenLoaiNH, String hinhAnh) {
        MaLoaiNH = maLoaiNH;
        TenLoaiNH = tenLoaiNH;
        HinhAnh = hinhAnh;
    }

    public String getMaLoaiNH() {
        return MaLoaiNH;
    }

    public void setMaLoaiNH(String maLoaiNH) {
        MaLoaiNH = maLoaiNH;
    }

    public String getTenLoaiNH() {
        return TenLoaiNH;
    }

    public void setTenLoaiNH(String tenLoaiNH) {
        TenLoaiNH = tenLoaiNH;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }
}
