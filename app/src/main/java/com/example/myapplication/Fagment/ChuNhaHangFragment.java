package com.example.myapplication.Fagment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Model.TaiKhoan;
import com.example.myapplication.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChuNhaHangFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChuNhaHangFragment extends Fragment {
    List<TaiKhoan> list;
    RecyclerView rcv_ChuNhaHang;


    public ChuNhaHangFragment() {
        // Required empty public constructor
    }

    public static ChuNhaHangFragment newInstance() {
        ChuNhaHangFragment fragment = new ChuNhaHangFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chu_nha_hang, container, false);
        return view;
    }
}