package com.example.myapplication.Model;

public class MonAnThem {
    private String tenMonThem;
    private int hinhAnh;
    private int checkBox;


    public MonAnThem(String tenMonThem, int hinhAnh, int checkBox) {
        this.tenMonThem = tenMonThem;
        this.hinhAnh = hinhAnh;
        this.checkBox = checkBox;
    }

    public String getTenMonThem() {
        return tenMonThem;
    }

    public void setTenMonThem(String tenMonThem) {
        this.tenMonThem = tenMonThem;
    }

    public int getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(int hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(int checkBox) {
        this.checkBox = checkBox;
    }
}
