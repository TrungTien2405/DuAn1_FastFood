package com.example.myapplication.Model;

public class DanhGiaNH {
    private String MaDanhGia;
    private String MaNH;
    private int LuotDG;
    private int TongDG;

    public DanhGiaNH(String maDanhGia, String maNH, int luotDG, int tongDG) {
        MaDanhGia = maDanhGia;
        MaNH = maNH;
        LuotDG = luotDG;
        TongDG = tongDG;
    }

    public String getMaDanhGia() {
        return MaDanhGia;
    }

    public void setMaDanhGia(String maDanhGia) {
        MaDanhGia = maDanhGia;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public int getLuotDG() {
        return LuotDG;
    }

    public void setLuotDG(int luotDG) {
        LuotDG = luotDG;
    }

    public int getTongDG() {
        return TongDG;
    }

    public void setTongDG(int tongDG) {
        TongDG = tongDG;
    }
}
