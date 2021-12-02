package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fagment.ChuNhaHangFragment;
import com.example.myapplication.Model.TaiKhoan;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChuNhaHangAdapter extends RecyclerView.Adapter<ChuNhaHangAdapter.MyViewHolder> {
     List<TaiKhoan> list;
     Context context;
     ChuNhaHangFragment fragment;

    public ChuNhaHangAdapter(List<TaiKhoan> list, Context context, ChuNhaHangFragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ChuNhaHangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_taikhoan, parent, false);
        return new ChuNhaHangAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaiKhoan taiKhoan = list.get(position);

        holder.tvItemHoTenTK.setText(taiKhoan.getHoTen());
        holder.tvItemSoDTTK.setText((taiKhoan.getSDT()+""));
        holder.tvItemDiaChiTK.setText(taiKhoan.getDiaChi());

        holder.tvXoaItemTK.setVisibility(View.INVISIBLE);
        try {
            if (taiKhoan.getHinhAnh().isEmpty()) {
                holder.imgv_ItemHinhTK.setImageResource(R.drawable.avatar);
            } else Picasso.with(context).load(taiKhoan.getHinhAnh()).resize(1000, 1000).centerCrop().onlyScaleDown().into(holder.imgv_ItemHinhTK);
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.tvXoaItemTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gọi fragment lấy hàm dialog xóa bên ChuHangFragment, lấy ra mã cần xóa
                fragment.dialogXoaChuNhaHang(taiKhoan.getMaTK());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgv_ItemHinhTK;
        TextView tvItemHoTenTK, tvXoaItemTK, tvItemSoDTTK, tvItemDiaChiTK;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgv_ItemHinhTK = itemView.findViewById(R.id.imgv_ItemHinhTK);
            tvItemHoTenTK = itemView.findViewById(R.id.tvItemHoTenTK);
            tvXoaItemTK = itemView.findViewById(R.id.tvXoaItemTK);
            tvItemSoDTTK = itemView.findViewById(R.id.tvItemSoDTTK);
            tvItemDiaChiTK = itemView.findViewById(R.id.tvItemDiaChiTK);
        }
    }
}
