package com.java.luolingxiao.fragment;

import androidx.fragment.app.Fragment;

import com.java.luolingxiao.DataRepository;
import com.java.luolingxiao.DefaultActivity;

import java.util.Objects;

public class DefaultFragment extends Fragment {
    protected DataRepository getDataRepository() {
        return ((DefaultActivity) Objects.requireNonNull(getActivity())).getDataRepository();
    }

    protected boolean isSaveTrafficMode() {
        return ((DefaultActivity)getActivity()).isSaveTrafficMode();
    }

    public DefaultFragment() {}
}
