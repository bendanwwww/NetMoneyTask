package com.vovo.netmoneytask;

/**
 * @Auther: liuzeheng@zhihu.com
 * @Date: 2024/11/17
 * @Description:
 */

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class XhsFragment extends Fragment {

    public XhsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.xhs_project, container, false);
    }
}
