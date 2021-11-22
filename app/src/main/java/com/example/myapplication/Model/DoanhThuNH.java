package com.example.myapplication.Model;

public class DoanhThuNH {
    private String maNH;
    private int tongDH;
    private String tenNH;
    private int tongDT;
    private String HinhAnh;
    private Double DanhGia;

    public DoanhThuNH(String maNH, int tongDH, String tenNH, int tongDT, String hinhAnh, Double danhGia) {
        this.maNH = maNH;
        this.tongDH = tongDH;
        this.tenNH = tenNH;
        this.tongDT = tongDT;
        HinhAnh = hinhAnh;
        DanhGia = danhGia;
    }

    public String getMaNH() {
        return maNH;
    }

    public void setMaNH(String maNH) {
        this.maNH = maNH;
    }

    public int getTongDH() {
        return tongDH;
    }

    public void setTongDH(int tongDH) {
        this.tongDH = tongDH;
    }

    public String getTenNH() {
        return tenNH;
    }

    public void setTenNH(String tenNH) {
        this.tenNH = tenNH;
    }

    public int getTongDT() {
        return tongDT;
    }

    public void setTongDT(int tongDT) {
        this.tongDT = tongDT;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public Double getDanhGia() {
        return DanhGia;
    }

    public void setDanhGia(Double danhGia) {
        DanhGia = danhGia;
    }
}
