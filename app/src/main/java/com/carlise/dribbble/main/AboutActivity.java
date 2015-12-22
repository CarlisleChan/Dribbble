package com.carlise.dribbble.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;

/**
 * Created by zhanglei on 15/8/7.
 */
public class AboutActivity extends BaseActivity {

    private Button mTitle, mLine1, mLine2, mLine3, mLine4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle("关于");
    }
}