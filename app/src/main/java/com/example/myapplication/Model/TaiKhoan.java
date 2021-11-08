package com.example.myapplication.Model;

public class TaiKhoan {
    private String MaTK;
    private String HoTen;
    private String MatKhau;
    private String SDT;
    private String DiaChi;
    private int Quyen;
    private String HinhAnh;
    private int SoDu;

    public TaiKhoan(String maTK, String hoTen, String matKhau, String SDT, String diaChi, int quyen, String hinhAnh, int soDu) {
        MaTK = maTK;
        HoTen = hoTen;
        MatKhau = matKhau;
        this.SDT = SDT;
        DiaChi = diaChi;
        Quyen = quyen;
        HinhAnh = hinhAnh;
        SoDu = soDu;
    }

    public String getMaTK() {
        return MaTK;
    }

    public void setMaTK(String maTK) {
        MaTK = maTK;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        MatKhau = matKhau;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public int getQuyen() {
        return Quyen;
    }

    public void setQuyen(int quyen) {
        Quyen = quyen;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public int getSoDu() {
        return SoDu;
    }

    public void setSoDu(int soDu) {
        SoDu = soDu;
    }
}
