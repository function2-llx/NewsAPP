package com.java.luolingxiao.fragment;

import androidx.fragment.app.Fragment;

import com.java.luolingxiao.DataRepository;
import com.java.luolingxiao.DeFaultActivity;

import java.util.Objects;

public class DefaultFragment extends Fragment {
    protected DataRepository getDataRepository() {
        return ((DeFaultActivity) Objects.requireNonNull(getActivity())).getDataRepository();
    }
}
