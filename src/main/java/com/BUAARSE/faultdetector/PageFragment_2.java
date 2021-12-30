package com.BUAARSE.faultdetector;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class PageFragment_2 extends Fragment {

    private LinearLayout Bt1;
    private LinearLayout Bt2;
    private ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bt1 = getActivity().findViewById(R.id.button_f21);
        Bt2 = getActivity().findViewById(R.id.button_f22);
        imageView = getActivity().findViewById(R.id.image_f1);

        DisplayMetrics dm = getResources().getDisplayMetrics();
//        Float density = dm.density;
        int width = dm.widthPixels;
//        System.out.println(width);
        int height = width*363/820;
        ViewGroup.LayoutParams params1 = imageView.getLayoutParams();
//        ViewGroup.LayoutParams params2 = Bt1.getLayoutParams();
//        ViewGroup.LayoutParams params3 = Bt2.getLayoutParams();
        params1.height = height;
//        int mBtHeight = (int)(((width*160/density-80)/3+40)*density/160);
//        params2.height = mBtHeight;
//        params3.height = mBtHeight;
//        imageView.setLayoutParams(params1);
//        Bt1.setLayoutParams(params2);
//        Bt2.setLayoutParams(params3);

        Bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act1 = new Intent(getContext(), MainFirstActivity.class);
                startActivity(act1);
                getActivity().overridePendingTransition(R.animator.down_in, R.animator.on_out);
            }
        });
        Bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act2 = new Intent(getContext(), MainSecondActivity.class);
                startActivity(act2);
                getActivity().overridePendingTransition(R.animator.down_in, R.animator.on_out);
            }
        });
    }
}
