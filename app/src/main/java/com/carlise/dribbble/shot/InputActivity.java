package com.carlise.dribbble.shot;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseToolsBarActivity;
import com.carlisle.model.CommentRequest;
import com.carlisle.model.PushCommentResult;
import com.carlisle.provider.ApiFactory;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chengxin on 16/1/7.
 */
public class InputActivity extends BaseToolsBarActivity {

    public static final String INPUT_NAME = "input_extra";
    public static final String SHOT_ID = "shot_id";

    private String name;
    private int shotId;

    private TextView navPublish;
    private EditText inputText;

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        name = getIntent().getStringExtra(INPUT_NAME);
        shotId = getIntent().getIntExtra(SHOT_ID, 1);
        if (TextUtils.isEmpty(name)) {
            name = "Input";
        }
        initView();
    }

    private void initView() {
        navPublish = (TextView) findViewById(R.id.toolbar_right_text_action);
        inputText = (EditText) findViewById(R.id.input_edit);
        progress = (ProgressBar) findViewById(R.id.input_progress);
        progress.setVisibility(View.INVISIBLE);

        if (inputText.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        navPublish.setText("Publish");
        navPublish.setTextColor(getResources().getColor(R.color.pretty_green));
        navPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputText.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(InputActivity.this, "comment is empty", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    requestPublishCom(input);
                }

            }
        });
    }

    private void requestPublishCom(String input) {
        CommentRequest request = new CommentRequest();
        request.body = input;

        ApiFactory.getDribleApi().pushComment(shotId, request, new Callback<PushCommentResult>() {
            @Override
            public void success(PushCommentResult pushCommentResult, Response response) {
                Toast.makeText(InputActivity.this, "comment success", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 403) {
                    Toast.makeText(InputActivity.this, "only Player can comment", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InputActivity.this, "errors: " + error.getResponse().getStatus(), Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.INVISIBLE);
            }
        });

        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle("Input");
        toolbar.setNavigationIcon(R.mipmap.cancel);
    }
}
