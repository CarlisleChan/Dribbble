package com.carlise.dribbble.main;

import android.os.Bundle;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;
import com.carlise.dribbble.utils.AuthUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthUtil.checkIfLogin(this);
        setContentView(R.layout.activity_main);
    }

}
