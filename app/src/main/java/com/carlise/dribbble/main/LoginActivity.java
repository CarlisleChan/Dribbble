package com.carlise.dribbble.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlise.dribbble.BuildConfig;
import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;
import com.carlise.dribbble.utils.AuthUtil;
import com.carlise.dribbble.utils.PreferenceKey;
import com.carlisle.model.AuthRequest;
import com.carlisle.model.AuthResult;
import com.carlisle.model.DribleUser;
import com.carlisle.provider.ApiFactory;
import com.carlisle.provider.Domain;
import com.carlisle.tools.SPUtils;
import com.google.gson.Gson;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chengxin on 16/1/7.
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private WebView loginWeb;

    private ProgressBar progressBar;

    private TextView oading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "into login");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        setupWeb();
    }

    private void initView() {
        loginWeb = (WebView) findViewById(R.id.login_web);
        loginWeb.clearCache(true);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        progressBar.setVisibility(View.INVISIBLE);
        oading = (TextView) findViewById(R.id.login_fetch_user);
        oading.setVisibility(View.INVISIBLE);
    }

    private void setupWeb() {

        String accessToken = SPUtils.getSharedPreference(this).getString(PreferenceKey.DRIBLE_TOKEN_FIELD, null);

        if (TextUtils.isEmpty(accessToken)) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Login: accessToken is null, need authorization");
            }
            progressBar.setVisibility(View.VISIBLE);
            loginWeb.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith(Domain.DRIBLE_CALL_BACK)) {
                        String returnCode = null;
                        if (url.indexOf("code=") != -1) {
                            // save the code str
                            returnCode = getCodeFromUrl(url);

                        }

                        if (!TextUtils.isEmpty(returnCode)) {
                            requestForAccessToken(returnCode);
                        } else {
                            Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        }

                        //finish(); // return last acitivty
                        //don't go redirect url
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            loginWeb.loadUrl(Domain.get(Domain.DomainType.LOGIN));
            Log.i(TAG, Domain.get(Domain.DomainType.LOGIN));
        } else {
            if (BuildConfig.DEBUG) {
                Toast.makeText(LoginActivity.this, "already login", Toast.LENGTH_SHORT).show();
            }
            onCompleteAuth();
        }
    }

    private String getCodeFromUrl(String url) {
        int startIndex = url.indexOf("code=") + "code=".length();
        int endIndex = url.indexOf("&state");
        String code = url.substring(startIndex, endIndex);
        Log.i(TAG, "code=" + code);
        return code;
    }

    private void requestForAccessToken(String returnCode) {
        AuthRequest authRequest = new AuthRequest();

        authRequest.clientId = Domain.DRIBLE_CLIENT_ID;
        authRequest.clientSecret = Domain.DRIBLE_SECRET;
        authRequest.code = returnCode;
        authRequest.state = Domain.state;

        ApiFactory.getDomainApi().auth(authRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AuthResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(AuthResult authResult) {
                        Log.i(TAG, new Gson().toJson(authResult));

                        SPUtils.getSharedPreference(LoginActivity.this).edit().putString(PreferenceKey.DRIBLE_TOKEN_FIELD, authResult.accessToken).commit();

                        Toast.makeText(LoginActivity.this, "Authorization success", Toast.LENGTH_LONG).show();
                        CookieManager.getInstance().removeAllCookie();

                        onCompleteAuth();
                    }
                });

    }

    private void onCompleteAuth() {
        fetchUserInfo();
        oading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int count = 5;

    private void fetchUserInfo() {
        ApiFactory.getDribleApi().fetchUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DribleUser>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "fetch user info again! count " + count);
                        if (count > 0) {
                            count--;
                            fetchUserInfo();
                        } else {
                            oading.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Please exit app and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(DribleUser dribleUser) {
                        SPUtils.saveString(LoginActivity.this, PreferenceKey.DRIBLE_USER_INFO, new Gson().toJson(dribleUser));
                        AuthUtil.goHome(LoginActivity.this);
                    }
                });
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle("授权");
    }

}
