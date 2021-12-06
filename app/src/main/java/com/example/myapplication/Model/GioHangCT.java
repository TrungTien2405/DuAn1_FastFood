package com.example.myapplication.Model;

import java.io.Serializable;

public class GioHangCT implements Serializable {
    private String MaGH;
    private String MaGHCT;
    private String MaMA;
    private String MaTK;
    private int SoLuong;
    private int giaMA;
    private String tenMA;
    private String TenMonThem;
    private String ThoiGian;
    private int TrangThai;
    private String HinhAnh;
    private Boolean TrangThaiCheckbox;
    private long tongGiaDH;


    public GioHangCT(String maGH, String maGHCT, String maMA, String maTK, int soLuong, int giaMA, String tenMA, String tenMonThem, String thoiGian, int trangThai, String hinhAnh, Boolean trangThaiCheckbox, long tongGiaDH) {
        MaGH = maGH;
        MaGHCT = maGHCT;
        MaMA = maMA;
        MaTK = maTK;
        SoLuong = soLuong;
        this.giaMA = giaMA;
        this.tenMA = tenMA;
        TenMonThem = tenMonThem;
        ThoiGian = thoiGian;
        TrangThai = trangThai;
        HinhAnh = hinhAnh;
        TrangThaiCheckbox = trangThaiCheckbox;
        this.tongGiaDH = tongGiaDH;
    }

    public long getTongGiaDH() {
        return tongGiaDH;
    }

    public void setTongGiaDH(long tongGiaDH) {
        this.tongGiaDH = tongGiaDH;
    }

    public String getMaGH() {
        return MaGH;
    }

    public void setMaGH(String maGH) {
        MaGH = maGH;
    }

    public String getMaGHCT() {
        return MaGHCT;
    }

    public void setMaGHCT(String maGHCT) {
        MaGHCT = maGHCT;
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

    public int getGiaMA() {
        return giaMA;
    }

    public void setGiaMA(int giaMA) {
        this.giaMA = giaMA;
    }

    public String getTenMA() {
        return tenMA;
    }

    public void setTenMA(String tenMA) {
        this.tenMA = tenMA;
    }

    public String getTenMonThem() {
        return TenMonThem;
    }

    public void setTenMonThem(String tenMonThem) {
        TenMonThem = tenMonThem;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }

    public int getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(int trangThai) {
        TrangThai = trangThai;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public Boolean getTrangThaiCheckbox() {
        return TrangThaiCheckbox;
    }

    public void setTrangThaiCheckbox(Boolean trangThaiCheckbox) {
        TrangThaiCheckbox = trangThaiCheckbox;
    }
}
