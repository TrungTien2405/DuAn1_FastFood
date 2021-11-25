package com.example.myapplication.Fagment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

public class MonAnCTFragment extends Fragment {


    public MonAnCTFragment() {
        // Required empty public constructor
    }

    public static MonAnCTFragment newInstance(String param1, String param2) {
        MonAnCTFragment fragment = new MonAnCTFragment();
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
        return inflater.inflate(R.layout.fragment_mon_an_c_t, container, false);
    }
}