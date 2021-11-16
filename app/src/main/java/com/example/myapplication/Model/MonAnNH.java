package com.example.myapplication.Model;

public class MonAnNH {
    private String MaMA;
    private String MaNH;
    private String MaMenuNH;
    private String TenMon;
    private String ChiTiet;
    private int Gia;
    private String HinhAnh;

    public MonAnNH(String maMA, String maNH, String maMenuNH, String tenMon, String chiTiet, int gia, String hinhAnh) {
        MaMA = maMA;
        MaNH = maNH;
        MaMenuNH = maMenuNH;
        TenMon = tenMon;
        ChiTiet = chiTiet;
        Gia = gia;
        HinhAnh = hinhAnh;
    }

    public String getMaMA() {
        return MaMA;
    }

    public void setMaMA(String maMA) {
        MaMA = maMA;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getMaMenuNH() {
        return MaMenuNH;
    }

    public void setMaMenuNH(String maMenuNH) {
        MaMenuNH = maMenuNH;
    }

    public String getTenMon() {
        return TenMon;
    }

    public void setTenMon(String tenMon) {
        TenMon = tenMon;
    }

    public String getChiTiet() {
        return ChiTiet;
    }

    public void setChiTiet(String chiTiet) {
        ChiTiet = chiTiet;
    }

    public int getGia() {
        return Gia;
    }

    public void setGia(int gia) {
        Gia = gia;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }
}
