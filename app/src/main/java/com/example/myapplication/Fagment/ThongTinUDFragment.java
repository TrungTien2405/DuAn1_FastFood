package com.example.myapplication.Fagment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;


public class ThongTinUDFragment extends Fragment {
    ImageView imgbackTTUD;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_thong_tin_u_d, container, false);
        imgbackTTUD = v.findViewById(R.id.imgBackTTUD);

        imgbackTTUD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_out, R.anim.fade_out, R.anim.fade_in, R.anim.slide_in)
                        .replace(R.id.nav_FrameFragment, new CaiDatFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }
}