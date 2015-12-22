package com.carlise.dribbble.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlise.dribbble.bucket.BucketsActivity;
import com.carlise.dribbble.dribleSdk.AuthUtil;
import com.carlise.dribbble.dribleSdk.DriRegInfo;
import com.carlise.dribbble.dribleSdk.data.DribleUser;
import com.carlise.dribbble.shot.ShotsActivity;
import com.carlise.dribbble.users.UserInfoActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengxin on 15/12/21.
 */
public class HomeActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    private HomeFragment leftFragment, midFragment, rightFragment;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SimpleDraweeView avatar;
    private TextView name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_home);

        initView();
        initPager();
        showUserInfo();
    }

    private void initView() {

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                SharedPreferences shared;
                Intent intent = null;
                switch (menuItem.getItemId()) {
                    case R.id.item_liked_shots:
                        shared = getSharedPreferences(LoginActivity.ACCOUNT_INFO_MEM, Context.MODE_PRIVATE);
                        String like_url = shared.getString(LoginActivity.ACCOUNT_USER_LIKE_URL, null);
                        intent = new Intent(HomeActivity.this, ShotsActivity.class);
                        intent.putExtra(ShotsActivity.SHOTS_URL, like_url);
                        intent.putExtra(ShotsActivity.SHOTS_TITLE_EXTRA, "My liked");
                        intent.putExtra(ShotsActivity.CALL_FROM, "like");
                        break;
                    case R.id.item_buckets:
                        shared = getSharedPreferences(LoginActivity.ACCOUNT_INFO_MEM, Context.MODE_PRIVATE);
                        String buckets_url = shared.getString(LoginActivity.ACCOUNT_USER_BUCKETS_URL, null);
                        intent = new Intent(HomeActivity.this, BucketsActivity.class);
                        intent.putExtra(BucketsActivity.BUCKET_URL, buckets_url);
                        intent.putExtra(BucketsActivity.BUCKET_TITLE, "My buckets");
                        break;
                    case R.id.item_dev:
                        intent = new Intent(HomeActivity.this, AboutActivity.class);
                        break;
                }
                drawerLayout.closeDrawers();
                startActivity(intent);
                return true;
            }
        });

        View headerLayout = navigationView.inflateHeaderView(R.layout.drawer_header);

        avatar = (SimpleDraweeView) headerLayout.findViewById(R.id.left_menu_avatar_img);
        name = (TextView) headerLayout.findViewById(R.id.left_menu_name);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

    }

    private void initPager() {
        leftFragment = new HomeFragment();
        Bundle leftBundle = new Bundle();
        leftBundle.putInt(HomeFragment.TAB_INDEX_FIELD, 0);
        leftFragment.setArguments(leftBundle);

        midFragment = new HomeFragment();
        Bundle midBundle = new Bundle();
        midBundle.putInt(HomeFragment.TAB_INDEX_FIELD, 1);
        midFragment.setArguments(midBundle);

        rightFragment = new HomeFragment();
        Bundle rigBundle = new Bundle();
        rigBundle.putInt(HomeFragment.TAB_INDEX_FIELD, 2);
        rightFragment.setArguments(rigBundle);

        fragmentList.add(leftFragment);
        fragmentList.add(midFragment);
        fragmentList.add(rightFragment);

        List<String> titles = new ArrayList<>();
        titles.add("Top Shots");
        titles.add("Latest");
        titles.add("Animation");

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, titles);

        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);

    }

    private void showUserInfo() {
        final DribleUser user = AuthUtil.getMe(this);

        if (user != null && !TextUtils.isEmpty(user.getAvatar_url())) {
            Uri avatarUri = Uri.parse(user.getAvatar_url());
            avatar.setImageURI(avatarUri);

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, user.getId());
                    startActivity(intent);
                }
            });
        }

        if (user != null && !TextUtils.isEmpty(user.getName())) {
            name.setText(user.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent searchIntent = new Intent(Intent.ACTION_VIEW);
                searchIntent.setData(Uri.parse(DriRegInfo.DRIBLE_SEARCH_URL));
                startActivity(searchIntent);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }
    }
}
