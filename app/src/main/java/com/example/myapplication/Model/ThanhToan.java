package com.example.myapplication.Model;

public class ThanhToan {
    private String tenMonAn;
    private String tenMonThem;
    private String hihAnh;
    private int giaMA;
    private int soLuongMA;
    private int phiVanChuyen;
    private int tongTienMA;

    public ThanhToan(String tenMonAn, String tenMonThem, String hihAnh, int giaMA, int soLuongMA, int phiVanChuyen, int tongTienMA) {
        this.tenMonAn = tenMonAn;
        this.tenMonThem = tenMonThem;
        this.hihAnh = hihAnh;
        this.giaMA = giaMA;
        this.soLuongMA = soLuongMA;
        this.phiVanChuyen = phiVanChuyen;
        this.tongTienMA = tongTienMA;
    }

    public String getTenMonAn() {
        return tenMonAn;
    }

    public void setTenMonAn(String tenMonAn) {
        this.tenMonAn = tenMonAn;
    }

    public String getTenMonThem() {
        return tenMonThem;
    }

    public void setTenMonThem(String tenMonThem) {
        this.tenMonThem = tenMonThem;
    }

    public String getHihAnh() {
        return hihAnh;
    }

    public void setHihAnh(String hihAnh) {
        this.hihAnh = hihAnh;
    }

    public int getGiaMA() {
        return giaMA;
    }

    public void setGiaMA(int giaMA) {
        this.giaMA = giaMA;
    }

    public int getSoLuongMA() {
        return soLuongMA;
    }

    public void setSoLuongMA(int soLuongMA) {
        this.soLuongMA = soLuongMA;
    }

    public int getPhiVanChuyen() {
        return phiVanChuyen;
    }

    public void setPhiVanChuyen(int phiVanChuyen) {
        this.phiVanChuyen = phiVanChuyen;
    }

    public int getTongTienMA() {
        return tongTienMA;
    }

    public void setTongTienMA(int tongTienMA) {
        this.tongTienMA = tongTienMA;
    }
}
