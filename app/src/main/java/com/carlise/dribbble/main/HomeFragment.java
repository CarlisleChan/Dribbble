package com.carlise.dribbble.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.carlise.dribbble.R;
import com.carlise.dribbble.shot.ShotDetailActivity;
import com.carlise.dribbble.shot.ShotListAdapter;
import com.carlise.dribbble.dribleSdk.AuthUtil;
import com.carlise.dribbble.dribleSdk.DriRegInfo;
import com.carlise.dribbble.dribleSdk.data.DribleShot;
import com.carlise.dribbble.utils.Log;
import com.carlise.dribbble.utils.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanglei on 15/7/27.
 */
public class HomeFragment extends Fragment {
    public static final String TAB_INDEX_FIELD = "tab.index";
    private static final int RETRY_COUNT = 5;

    private int mIndex;
    private SwipeRefreshLayout mRefreshLayout;
    private ListView mList;
    private RelativeLayout mFooter;
    private ProgressBar mFootProgress;

    private ShotListAdapter mListAdapter;
    private ArrayList<DribleShot> mShotList;

    private HashMap<Integer, Integer> leftItemsH = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> midItemsH = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> rigItemsH = new HashMap<Integer, Integer>();
    private int mLastScrollY;

    private Runnable mTimeOut = new Runnable() {
        @Override
        public void run() {
            mRefreshLayout.setRefreshing(false);
        }
    };

    private HashMap<String, String> mRelatedLinks;
    private boolean mCanScroll = true;
    private boolean mCanLoadMore = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mIndex = getArguments().getInt(TAB_INDEX_FIELD);

        if (mList == null) {
            mRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.home_content_fragment, container, false);
            mList = (ListView) mRefreshLayout.findViewById(R.id.home_content_fragment);

            mFooter = (RelativeLayout) inflater.inflate(R.layout.footer_home_list, null, false);
            AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.home_footer_height));
            mFooter.setLayoutParams(footParams);
            mList.addFooterView(mFooter);
            mFootProgress = (ProgressBar) mFooter.findViewById(R.id.footer_progress);

            mList.setDivider(null);
            mList.setFriction(ViewConfiguration.getScrollFriction() /** 0.7f*/);

            String url = genRequestUrl();
            requestForList(url, true);

            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });

            mList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int index = getArguments().getInt(TAB_INDEX_FIELD);
                    int scrollHeight = 0;
                    if (mList.getChildAt(0) != null) {
                        switch (index) {
                            case 0:
                                leftItemsH.put(firstVisibleItem, mList.getChildAt(0).getHeight());
                                break;
                            case 1:
                                midItemsH.put(firstVisibleItem, mList.getChildAt(0).getHeight());
                                break;
                            case 2:
                                rigItemsH.put(firstVisibleItem, mList.getChildAt(0).getHeight());
                                break;
                        }


                        HashMap<Integer, Integer> heights = index == 0 ? leftItemsH : index == 1 ? midItemsH : rigItemsH;
                        for (int i = 0; i < firstVisibleItem; i++) {
                            if (heights.containsKey(i)) {
                                scrollHeight += heights.get(i);
                            } else { // This should not occur,
                                Log.e("This should not occur, you can slow down scroll speed to fix it");
                                heights.put(i, heights.get(i - 1));
                                scrollHeight += heights.get(i);
                            }
                        }
                        scrollHeight += -mList.getChildAt(0).getTop();
                    }

                    if (onScrollListListener != null && mCanScroll) {
                        onScrollListListener.onListScroll(scrollHeight - mLastScrollY);
                    }
                    mLastScrollY = scrollHeight;
                    if (firstVisibleItem + visibleItemCount == totalItemCount && !mList.canScrollVertically(1)
                            && mList.getAdapter() != null && mCanLoadMore) {
                        mCanLoadMore = false;
//                        Toast.makeText(getActivity(), "foot comming! index: " + mIndex, Toast.LENGTH_LONG).show();
                        if (mRelatedLinks == null ||
                                TextUtils.isEmpty(mRelatedLinks.get("next"))) {
                            requestForList(genRequestUrl(), true);
                        } else {
                            requestForList(mRelatedLinks.get("next"), false);
                        }


                    }
                }
            });
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    String url = genRequestUrl();
                    requestForList(url, true);
                }
            });
            mRefreshLayout.setColorSchemeResources(R.color.pretty_blue,
                    R.color.pretty_green);

        }

        return mRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private OnScrollListListener onScrollListListener;

    public void setOnScrollListListener(OnScrollListListener listener) {
        onScrollListListener = listener;
    }

    public interface OnScrollListListener {
        void onListScroll(int scrollDisY);
    }

    private void requestForList(String url, final boolean isFirst) {
        if (Log.DBG) {
            Log.i("request url: " + url);
        }
        final String accessToken = AuthUtil.getAccessToken(getActivity());
        if (TextUtils.isEmpty(accessToken)) {
            mRefreshLayout.setRefreshing(false);
            return;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mRefreshLayout.setRefreshing(false);
                        parseResponse(response, isFirst);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefreshLayout.setRefreshing(false);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "errors", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(DriRegInfo.REQUEST_HEAD_AUTH_FIELD, DriRegInfo.REQUEST_HEAD_BEAR + accessToken);
                params.putAll(super.getHeaders());
                return params;
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                Log.i("response headers: " + response.headers);
                mRelatedLinks = genNextUrl(response.headers.get(DriRegInfo.RESPONSE_HEADER_LINK));
                return super.parseNetworkResponse(response);
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000, RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        NetworkHandler.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);

        if (!isFirst) {
            mFootProgress.setVisibility(View.VISIBLE);
        }

        new Handler().removeCallbacks(mTimeOut);
        new Handler().postDelayed(mTimeOut, 10000);
    }

    private void parseResponse(JSONArray response, boolean isFirst) {
        try {
            if (mShotList == null) {
                mShotList = new ArrayList<DribleShot>();
            }
            if (mListAdapter == null) {
                mListAdapter = new ShotListAdapter(getActivity(), mShotList);
                mList.setAdapter(mListAdapter);
                mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            Intent intent = new Intent(getActivity(), ShotDetailActivity.class);
                            intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, mShotList.get(position - 1).getId());
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                        }
                    }
                });
            }
            if (isFirst) {
                mShotList.clear();
            }
            for (int i = 0; i < response.length(); i++) {
                DribleShot shot = new DribleShot((JSONObject) response.get(i));
                mShotList.add(shot);
            }
            mListAdapter.notifyDataSetChanged();
            mCanLoadMore = true;
            mFootProgress.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mRefreshLayout.setRefreshing(false);
    }

    private String genRequestUrl() {
        String url = DriRegInfo.REQUEST_ONE_SHOT_URL;
        switch (mIndex) {
            case 0:
                // url += ("?" + "page=2");
                break;
            case 1:
                url += ("?" + DriRegInfo.REQUEST_SHOTS_FIELD_SORT + "=" + DriRegInfo.REQUEST_SORT_RECENT);
                break;
            case 2:
                url += ("?" + DriRegInfo.REQUEST_SHOTS_FIELD_LIST + "=" + DriRegInfo.REQUEST_LIST_ANIMATED
                        + "&" + DriRegInfo.REQUEST_SHOTS_FIELD_SORT + "=" + DriRegInfo.REQUEST_SORT_RECENT);
                break;
        }
        return url;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private HashMap<String, String> genNextUrl(String link) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (TextUtils.isEmpty(link)) {
            return null;
        }
        String[] links = link.split(",");
        for (int i = 0; i < links.length; i++) {
            String str = links[i];
            String url = str.substring(str.indexOf("<") + 1, str.lastIndexOf(">"));
            String flag = str.substring(str.indexOf("rel=\"") + 5, str.lastIndexOf("\""));
            result.put(flag, url);
        }
        return result;
    }

}