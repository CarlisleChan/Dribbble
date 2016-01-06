package com.carlise.dribbble.main;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carlise.dribbble.BuildConfig;
import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;
import com.carlise.dribbble.utils.AuthUtil;
import com.carlise.dribbble.utils.NetworkHandler;
import com.carlisle.model.AuthRequest;
import com.carlisle.model.AuthResult;
import com.carlisle.model.DribleUser;
import com.carlisle.provider.ApiFactory;
import com.carlisle.provider.DriRegInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by zhanglei on 15/7/23.
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String DRIBLE_MEM = "com.tuesda.watch.drible.mem";
    public static final String DRIBLE_TOKEN_FIELD = "com.tuesda.watch.dirbbble.token";

    public static final String ACCOUNT_INFO_MEM = "com.tuesda.watch.account.mem";
    public static final String ACCOUNT_USER_ID = "com.tuesda.watch.account.user.id";
    public static final String ACCOUNT_NAME = "com.tuesda.watch.account.user.name";
    public static final String ACCOUNT_USER_AVATAR_URL = "com.tuesda.watch.account.user.avatar.url";
    public static final String ACCOUNT_USER_LIKE_URL = "com.tuesda.watch.account.user.like.url";
    public static final String ACCOUNT_USER_BUCKETS_URL = "com.tuesda.watch.account.user.buckets.url";

    private WebView mLoginWeb;

    private SharedPreferences mDribleShare;

    private ProgressBar mProgress;

    private TextView mLoading;

    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "into login");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        setupWeb();
    }

    private void initView() {
        mLoginWeb = (WebView) findViewById(R.id.login_web);
        mLoginWeb.clearCache(true);
        mProgress = (ProgressBar) findViewById(R.id.login_progress);
        mProgress.setVisibility(View.INVISIBLE);
        mLoading = (TextView) findViewById(R.id.login_fetch_user);
        mLoading.setVisibility(View.INVISIBLE);
    }

    private void setupWeb() {

        mQueue = Volley.newRequestQueue(this);
        mDribleShare = getSharedPreferences(DRIBLE_MEM, Context.MODE_PRIVATE);

        String accessToken = mDribleShare.getString(DRIBLE_TOKEN_FIELD, null);

        if (TextUtils.isEmpty(accessToken)) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Login: accessToken is null, need authorization");
            }
            mProgress.setVisibility(View.VISIBLE);
            mLoginWeb.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith(DriRegInfo.DRIBLE_CALL_BACK)) {
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
                    mProgress.setVisibility(View.INVISIBLE);
                }
            });

            mLoginWeb.loadUrl(DriRegInfo.DRIBLE_LOGIN_URL);
            Log.i(TAG, DriRegInfo.DRIBLE_LOGIN_URL);
        } else {
//            if (Log.DBG) {
//                Toast.makeText(LoginActivity.this, "already login", Toast.LENGTH_SHORT).show();
//            }
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

        authRequest.clientId = DriRegInfo.DRIBLE_CLIENT_ID;
        authRequest.clientSecret = DriRegInfo.DRIBLE_SECRET;
        authRequest.code = returnCode;
        authRequest.state = DriRegInfo.mState;

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

                SharedPreferences.Editor editor = mDribleShare.edit();
                editor.putString(DRIBLE_TOKEN_FIELD, authResult.accessToken);
                editor.commit();

                Toast.makeText(LoginActivity.this, "Authorization success", Toast.LENGTH_LONG).show();
                CookieManager.getInstance().removeAllCookie();

                onCompleteAuth();
            }
        });

    }

    private void onCompleteAuth() {

//        if (AuthUtil.hasUserInfo(this)) {
//            AuthUtil.goHome(this);
//        } else {
        fetchUserInfo();
        mLoading.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int count = 30;

    private void fetchUserInfo() {
        final String accessToken = AuthUtil.getAccessToken(this);
        String url = DriRegInfo.REQUEST_MY_INFO;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseInfo(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "fetch user info again! count " + count);
                if (count > 0) {
                    count--;
                    fetchUserInfo();
                } else {
                    mLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Please exit app and try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(DriRegInfo.REQUEST_HEAD_AUTH_FIELD, DriRegInfo.REQUEST_HEAD_BEAR + accessToken);
                params.putAll(super.getHeaders());
                return params;
            }
        };

        NetworkHandler.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void parseInfo(JSONObject json) {
        DribleUser me = new DribleUser(json);
        SharedPreferences sharedMe = getSharedPreferences(ACCOUNT_INFO_MEM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedMe.edit();
        editor.putInt(ACCOUNT_USER_ID, me.getId());
        editor.putString(ACCOUNT_NAME, me.getName());
        editor.putString(ACCOUNT_USER_AVATAR_URL, me.getAvatar_url());
        editor.putString(ACCOUNT_USER_LIKE_URL, me.getLikes_url());
        editor.putString(ACCOUNT_USER_BUCKETS_URL, me.getBuckets_url());
        editor.commit();

        AuthUtil.goHome(this);
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle("授权");
    }

}
