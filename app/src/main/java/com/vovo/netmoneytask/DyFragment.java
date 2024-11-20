package com.vovo.netmoneytask;

/**
 * @Auther: liuzeheng@zhihu.com
 * @Date: 2024/11/17
 * @Description:
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.vovo.netmoneytask.controller.DyController;

public class DyFragment extends Fragment {

    private DyController dyController;

    public DyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dy_project, container, false);
        // 抖音
        dyController = new DyController(view.getContext(), view);
        dyController.init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次应用进入前台时都会调用
        dyController.reflushButtonClick();
    }
}

