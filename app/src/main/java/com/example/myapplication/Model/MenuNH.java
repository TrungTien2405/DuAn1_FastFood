package com.example.myapplication.Model;

public class MenuNH {
    private String maMenuNH;
    private String tenMenuNH;
    private String hinhAnh;

    public MenuNH(String maMenuNH, String tenMenuNH, String hinhAnh) {
        this.maMenuNH = maMenuNH;
        this.tenMenuNH = tenMenuNH;
        this.hinhAnh = hinhAnh;
    }

    public String getMaMenuNH() {
        return maMenuNH;
    }

    public void setMaMenuNH(String maMenuNH) {
        this.maMenuNH = maMenuNH;
    }

    public String getTenMenuNH() {
        return tenMenuNH;
    }

    public void setTenMenuNH(String tenMenuNH) {
        this.tenMenuNH = tenMenuNH;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
