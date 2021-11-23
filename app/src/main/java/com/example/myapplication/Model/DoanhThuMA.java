package com.example.myapplication.Model;

public class DoanhThuMA {
    private String maMA;
    private String tenMA;
    private String hinhAnh;
    private int luotMua;
    private int tongDT;

    public DoanhThuMA(String maMA, String tenMA, String hinhAnh, int luotMua, int tongDT) {
        this.maMA = maMA;
        this.tenMA = tenMA;
        this.hinhAnh = hinhAnh;
        this.luotMua = luotMua;
        this.tongDT = tongDT;
    }

    public String getMaMA() {
        return maMA;
    }

    public void setMaMA(String maMA) {
        this.maMA = maMA;
    }

    public String getTenMA() {
        return tenMA;
    }

    public void setTenMA(String tenMA) {
        this.tenMA = tenMA;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getLuotMua() {
        return luotMua;
    }

    public void setLuotMua(int luotMua) {
        this.luotMua = luotMua;
    }

    public int getTongDT() {
        return tongDT;
    }

    public void setTongDT(int tongDT) {
        this.tongDT = tongDT;
    }
}
