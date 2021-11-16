package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.GioHang;
import com.example.myapplication.R;

import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {
    public List<GioHang> list;
    public Context context;

    public GioHangAdapter(List<GioHang> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public GioHangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_giohang, parent, false);
        return new GioHangAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = list.get(position);

        holder.tv_MaGH.setText(gioHang.getMaGH());
        holder.tv_TenMonThem.setText(gioHang.getTenMonThem());
        holder.tv_SoLuong.setText(gioHang.getSoLuong());
        holder.tv_TongTien.setText(gioHang.getTongTien());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imv_hinh, img_CongSoLuong, img_TruSoLuong;
        CheckBox chk_xoaItemGH;
        TextView tv_MaGH, tv_MaMA, tv_MaTK, tv_SoLuong, tv_TenMonThem, tv_TongTien;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_hinh = itemView.findViewById(R.id.imv_itemLoaiNH);
//            tv_TenMonGH = itemView.findViewById(R.id.tv_TenMonGH);
            tv_TenMonThem = itemView.findViewById(R.id.tv_TenMonThemGH);
            tv_TongTien = itemView.findViewById(R.id.tv_TongTienGH);
            tv_SoLuong = itemView.findViewById(R.id.tv_SoLuongGH);
            img_CongSoLuong = itemView.findViewById(R.id.img_CongSoLuongGH);
            img_TruSoLuong = itemView.findViewById(R.id.img_TruSoLuongGH);
            chk_xoaItemGH = itemView.findViewById(R.id.chk_XoaItemGH);
        }
    }
}
