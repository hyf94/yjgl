package com.sucsoft.yjgl.gqt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.sucsoft.yjgl.R;

public class CallFragment extends Fragment {
    private static CallEngine callEngine = GQTHelper.getInstance().getCallEngine();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call,container,false);

        return view;
    }


}
