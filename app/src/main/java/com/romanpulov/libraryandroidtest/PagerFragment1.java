package com.romanpulov.libraryandroidtest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;


public class PagerFragment1 extends Fragment {

    public static PagerFragment1 newInstance() {
        return new PagerFragment1();
    }

    public PagerFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager_fragment1, container, false);
    }


}
