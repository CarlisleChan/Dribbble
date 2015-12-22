package com.carlise.dribbble.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;
import com.carlise.dribbble.dribleSdk.AuthUtil;
import com.carlise.dribbble.shot.InputActivity;
import com.carlise.dribbble.shot.OneShotInListActivity;
import com.carlise.dribbble.shot.ShotDetailActivity;
import com.carlise.dribbble.users.UserInfoActivity;
import com.carlise.dribbble.users.UsersActivity;

public class MainActivity extends BaseActivity {
    private Button mLoginBtn;
    private Button mGetUser;
    private Button mGoHome;
    private Button mGoOneShot;
    private Button mGoShotDetail;
    private Button mGoInput;
    private Button mGoUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthUtil.checkIfLogin(this);

        setContentView(R.layout.activity_main);

        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mGetUser = (Button) findViewById(R.id.get_user);
        mGoHome = (Button) findViewById(R.id.to_home);
        mGoOneShot = (Button) findViewById(R.id.to_one_shot);
        mGoShotDetail = (Button) findViewById(R.id.to_shot_detail);
        mGoInput = (Button) findViewById(R.id.to_input);
        mGoUsers = (Button) findViewById(R.id.to_users);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearSharedPref();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mGetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        mGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mGoOneShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OneShotInListActivity.class);
                startActivity(intent);
            }
        });

        mGoShotDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShotDetailActivity.class);
                startActivity(intent);
            }
        });

        mGoInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        mGoUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });
    }

}
