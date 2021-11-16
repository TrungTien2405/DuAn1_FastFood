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

import com.example.myapplication.Fagment.GioHangFragment;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {
    public List<GioHang> list;
    public Context context;
    private GioHangFragment fragment;


    public GioHangAdapter(List<GioHang> list, Context context, GioHangFragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
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

        holder.tv_TenMonThem.setText(gioHang.getTenMonThem());
        holder.tv_SoLuong.setText(gioHang.getSoLuong()+"");
        holder.tv_giaGH.setText((gioHang.getGiaMA() * gioHang.getSoLuong()) +"");
        holder.tv_tenMon.setText(gioHang.getTenMA());

        if(gioHang.getHinhAnh().isEmpty()){
            holder.imv_hinh.setImageResource(R.drawable.im_food);
        }else Picasso.with(context).load(gioHang.getHinhAnh()).into(holder.imv_hinh);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imv_hinh, imv_CongSoLuong, imv_TruSoLuong;
        CheckBox chk_chonItemGH;
        TextView  tv_tenMon, tv_MaTK, tv_SoLuong, tv_TenMonThem, tv_giaGH;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_hinh = itemView.findViewById(R.id.imv_itemLoaiNH);
//            tv_TenMonGH = itemView.findViewById(R.id.tv_TenMonGH);
            tv_TenMonThem = itemView.findViewById(R.id.tv_itemTenMonThemGH);
            tv_tenMon = itemView.findViewById(R.id.tv_itemTenMonGH);
            tv_giaGH = itemView.findViewById(R.id.tv_itemGiaGH);
            tv_SoLuong = itemView.findViewById(R.id.tv_itemSoLuongGH);
            imv_CongSoLuong = itemView.findViewById(R.id.img_itemCongSoLuongGH);
            imv_TruSoLuong = itemView.findViewById(R.id.img_itemTruSoLuongGH);
            chk_chonItemGH = itemView.findViewById(R.id.chk_ItemChonGH);
        }
    }
}
