package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.ThanhToan;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ThanhToanAdapter extends RecyclerView.Adapter<ThanhToanAdapter.MyViewHolder> {
    private List<GioHangCT> list;
    private Context context;

    public ThanhToanAdapter(List<GioHangCT> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thanhtoan, parent, false);
        return new ThanhToanAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHangCT tt = list.get(position);

        holder.tvTenMA.setText(tt.getTenMA());
        holder.tvGiaMA.setText(tt.getGiaMA()+"");
        holder.tvPhiVC.setText("20,000");
        holder.tvTongSoTien.setText((formatNumber( tt.getGiaMA() * tt.getSoLuong())) +"");
        holder.tvSoLuongMA.setText(tt.getSoLuong()+"");
        holder.tvTenMonThem.setText(tt.getTenMonThem());
        holder.tvSLTongSoTien.setText("Tổng số tiền(" + tt.getSoLuong() + " sản phẩm):");

        if(tt.getHinhAnh().isEmpty()){
            holder.imvMa.setImageResource(R.drawable.ic_addimage);
        }else Picasso.with(context).load(tt.getHinhAnh()).resize(2048, 1600).centerCrop().onlyScaleDown().into(holder.imvMa);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMA, tvTenMonThem, tvGiaMA, tvSoLuongMA, tvTongSoTien, tvSLTongSoTien, tvPhiVC;
        ImageView imvMa;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGiaMA = itemView.findViewById(R.id.tv_itemGiaMAThanhToanFra);
            tvTenMA = itemView.findViewById(R.id.tv_itemTenMAThanhToanFra);
            tvTenMonThem = itemView.findViewById(R.id.tv_itemTenMonThemThanhToanFra);
            tvSoLuongMA = itemView.findViewById(R.id.tv_itemSoLuongMAThanhToanFra);
            tvTongSoTien = itemView.findViewById(R.id.tv_itemTongSoTienSPThanhToan);
            tvSLTongSoTien = itemView.findViewById(R.id.tv_itemSLTongSoTien1SPThanhToan);
            tvPhiVC = itemView.findViewById(R.id.tv_itemPhiVCThanhToanFrag);
            imvMa = itemView.findViewById(R.id.imv_itemHinhThanhToan);
        }
    }

    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }
}
