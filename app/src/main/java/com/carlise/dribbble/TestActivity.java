package com.carlise.dribbble;

import android.os.Bundle;

import com.carlise.dribbble.application.BaseToolsBarActivity;
import com.carlise.dribbble.utils.AuthUtil;

public class TestActivity extends BaseToolsBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthUtil.checkIfLogin(this);
        setContentView(R.layout.activity_test);
    }

}
