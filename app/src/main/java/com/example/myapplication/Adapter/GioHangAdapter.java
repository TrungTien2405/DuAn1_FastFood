package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fagment.GioHangFragment;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {
    public List<GioHangCT> list;
    public Context context;
    private GioHangFragment fragment;


    public GioHangAdapter(List<GioHangCT> list, Context context, GioHangFragment fragment) {
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
        GioHangCT gioHangCT = list.get(position);

        holder.tv_TenMonThem.setText(gioHangCT.getTenMonThem());
        holder.tv_SoLuong.setText(gioHangCT.getSoLuong()+"");
        holder.tv_giaGH.setText((formatNumber( gioHangCT.getGiaMA() * gioHangCT.getSoLuong())) +"");
        holder.tv_tenMon.setText(gioHangCT.getTenMA());

        if(gioHangCT.getHinhAnh().isEmpty()){
            holder.imv_hinh.setImageResource(R.drawable.ic_addimage);
        }else Picasso.with(context).load(gioHangCT.getHinhAnh()).resize(2048, 1600).centerCrop().onlyScaleDown().into(holder.imv_hinh);


        //Nh???n Checkbox ch???n m??n ??n, t??ng t???ng gi?? m??n ??n
        holder.chk_chonItemGH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int soLuong = gioHangCT.getSoLuong() + 1;
                if(isChecked && soLuong>0){
                    fragment.checkedGioHang(position, gioHangCT.getGiaMA() * gioHangCT.getSoLuong());
                }else{
                    fragment.uncheckedGioHang(position, gioHangCT.getGiaMA() * gioHangCT.getSoLuong());
                    fragment.tinhTongTien();
                }
            }
        });

        //Nh???n t??ng s??? l?????ng m??n ??n
        holder.imv_CongSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soLuong = gioHangCT.getSoLuong() + 1;

                if(soLuong<=100) {
                    holder.tv_SoLuong.setText(soLuong + "");
                    holder.tv_giaGH.setText((formatNumber(soLuong * gioHangCT.getGiaMA())));
                    fragment.updateSoLuongGH(gioHangCT.getMaGHCT(), soLuong, position);

                    if (holder.chk_chonItemGH.isChecked()) {
                        fragment.TongTienGH += gioHangCT.getGiaMA();
                        fragment.tvTongTienGH.setText(formatNumber(fragment.TongTienGH));
                    }
                }
            }
        });

        //Nh???n gi???m s??? l?????ng m??n ??n
        holder.imv_TruSoLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soLuong = gioHangCT.getSoLuong() - 1;

                if(soLuong>0) {
                    holder.tv_SoLuong.setText(soLuong + "");
                    holder.tv_giaGH.setText((formatNumber(soLuong * gioHangCT.getGiaMA())));
                    fragment.updateSoLuongGH(gioHangCT.getMaGHCT(), soLuong, position);

                    if (holder.chk_chonItemGH.isChecked()) {
                        fragment.TongTienGH -= gioHangCT.getGiaMA();
                        fragment.tvTongTienGH.setText(formatNumber(fragment.TongTienGH));
                    }
                }
            }
        });
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
            imv_hinh = itemView.findViewById(R.id.imv_itemHinhGH);
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

    // ?????nh d???ng sang s??? ti???n
    private String formatNumber(int number){
        // t???o 1 NumberFormat ????? ?????nh d???ng s??? theo ti??u chu???n c???a n?????c Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }

    public void updateData(List<GioHangCT> viewModels) {
        list.clear();
        list.addAll(viewModels);
        notifyDataSetChanged();
    }
}
