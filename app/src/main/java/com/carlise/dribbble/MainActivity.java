package com.carlise.dribbble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.carlise.dribbble.activities.HomeActivity;
import com.carlise.dribbble.activities.InputActivity;
import com.carlise.dribbble.activities.LoginActivity;
import com.carlise.dribbble.activities.OneShotInListActivity;
import com.carlise.dribbble.activities.ShotDetailActivity;
import com.carlise.dribbble.activities.UserInfoActivity;
import com.carlise.dribbble.activities.UsersActivity;
import com.carlise.dribbble.activities.WithActionBarActivity;
import com.carlise.dribbble.dribleSdk.AuthUtil;

public class MainActivity extends Activity {
    private Button mLoginBtn;
    private Button mGetUser;
    private Button mGoAction;
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
        mGoAction = (Button) findViewById(R.id.to_action);
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
        mGoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WithActionBarActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(0, 0);
    }
}
