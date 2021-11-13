package com.example.myapplication.Model;

public class NhaHang {
    private String MaNH;
    private String MaLoaiNH;
    private String MaTK;
    private String TenNH;
    private String ThoiGian;
    private int PhiVanChuyen;
    private String HinhAnh;
    private Double DanhGia;
    private String MaDG;
    private String MaYT;

    public NhaHang(String maNH, String maLoaiNH, String maTK, String tenNH, String thoiGian, int phiVanChuyen, String hinhAnh, Double danhGia, String maDG, String maYT) {
        MaNH = maNH;
        MaLoaiNH = maLoaiNH;
        MaTK = maTK;
        TenNH = tenNH;
        ThoiGian = thoiGian;
        PhiVanChuyen = phiVanChuyen;
        HinhAnh = hinhAnh;
        DanhGia = danhGia;
        MaDG = maDG;
        MaYT = maYT;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getMaLoaiNH() {
        return MaLoaiNH;
    }

    public void setMaLoaiNH(String maLoaiNH) {
        MaLoaiNH = maLoaiNH;
    }

    public String getMaTK() {
        return MaTK;
    }

    public void setMaTK(String maTK) {
        MaTK = maTK;
    }

    public String getTenNH() {
        return TenNH;
    }

    public void setTenNH(String tenNH) {
        TenNH = tenNH;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }

    public int getPhiVanChuyen() {
        return PhiVanChuyen;
    }

    public void setPhiVanChuyen(int phiVanChuyen) {
        PhiVanChuyen = phiVanChuyen;
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

    public String getMaDG() {
        return MaDG;
    }

    public void setMaDG(String maDG) {
        MaDG = maDG;
    }

    public String getMaYT() {
        return MaYT;
    }

    public void setMaYT(String maYT) {
        MaYT = maYT;
    }
}
