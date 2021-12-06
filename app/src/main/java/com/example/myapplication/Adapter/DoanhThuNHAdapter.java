package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.DoanhThuNH;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DoanhThuNHAdapter extends RecyclerView.Adapter<DoanhThuNHAdapter.MyViewHolder> {
    private List<DoanhThuNH> list;
    private Context context;

    public DoanhThuNHAdapter(List<DoanhThuNH> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doanhthunh, parent, false);

        return new DoanhThuNHAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DoanhThuNH dt = list.get(position);

        holder.tvTenNH.setText(dt.getTenNH());
        holder.tvTongDH.setText(dt.getTongDH()+"");
        holder.tvTongDT.setText(formatNumber(dt.getTongDT()));
        holder.tvTenNH.setText(dt.getTenNH());

        if(dt.getHinhAnh().isEmpty()){
            holder.imvHinh.setImageResource(R.drawable.ic_addimage);
        }else Picasso.with(context).load(dt.getHinhAnh()).into(holder.imvHinh);

        if(dt.getDanhGia()<1){
            holder.imvSao2.setVisibility(View.INVISIBLE);
            holder.imvSao3.setVisibility(View.INVISIBLE);
            holder.imvSao4.setVisibility(View.INVISIBLE);
            holder.imvSao5.setVisibility(View.INVISIBLE);
        }else if(dt.getDanhGia()<2){
            holder.imvSao3.setVisibility(View.INVISIBLE);
            holder.imvSao4.setVisibility(View.INVISIBLE);
            holder.imvSao5.setVisibility(View.INVISIBLE);
        }else if(dt.getDanhGia()<3){
            holder.imvSao4.setVisibility(View.INVISIBLE);
            holder.imvSao5.setVisibility(View.INVISIBLE);
        }else if(dt.getDanhGia()<4){
            holder.imvSao5.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenNH, tvTongDH, tvTongDT;
        ImageView imvHinh, imvSao1, imvSao2, imvSao3, imvSao4, imvSao5;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTenNH =itemView.findViewById(R.id.tv_itemTenNHDTNH);
            tvTongDH =itemView.findViewById(R.id.tv_itemTongDHDTNH);
            tvTongDT =itemView.findViewById(R.id.tv_itemTongDTNH);
            imvHinh =  itemView.findViewById(R.id.imv_itemHinhDTNH);
            imvSao1 = itemView.findViewById(R.id.imv_itemSaoDTNH1);
            imvSao2 = itemView.findViewById(R.id.imv_itemSaoDTNH2);
            imvSao3 = itemView.findViewById(R.id.imv_itemSaoDTNH3);
            imvSao4 = itemView.findViewById(R.id.imv_itemSaoDTNH4);
            imvSao5 = itemView.findViewById(R.id.imv_itemSaoDTNH5);
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
