package zjj.com.dribbbledemoapp.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.applications.AppController;
import zjj.com.dribbbledemoapp.base.BaseActivity;
import zjj.com.dribbbledemoapp.base.CommonAdapter;
import zjj.com.dribbbledemoapp.base.CommonViewHolder;
import zjj.com.dribbbledemoapp.domains.Comment;
import zjj.com.dribbbledemoapp.utils.Constants;
import zjj.com.dribbbledemoapp.utils.DateTimeUtils;

public class CommentActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView rv_comment_list;
    private List<Comment> cmtList;
    private CommonAdapter<Comment> adapter;
    private String shotsId;
    private String shotsTitle;

    @Override
    public void initView() {
        setContentView(R.layout.activity_comment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        rv_comment_list = (RecyclerView) findViewById(R.id.rv_comment_list);
        rv_comment_list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        shotsId = getIntent().getStringExtra("shotsId");
        shotsTitle = getIntent().getStringExtra("shotsTitle");
        getSupportActionBar().setTitle(shotsTitle);

        if (!TextUtils.isEmpty(shotsId)) {
            AppController.getInstance().enqueueGetRequest(
                    new String[]{Constants.SHOTS, shotsId, Constants.COMMENTS},
                    null, "comments",
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body = response.body().string();
                            Comment[] comments = new Gson().fromJson(body, Comment[].class);
                            cmtList = new ArrayList<>(Arrays.asList(comments));
                            displayData();
                        }
                    });
        }
    }


    private void displayData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new CommonAdapter<Comment>(context, R.layout.item_comment_cardview, cmtList) {
                    @Override
                    protected void convert(final CommonViewHolder holder, final Comment comment) {
                        holder.setCircleImageUrl(R.id.iv_user_avatar, comment.getUser().getAvatar_url());
                        holder.setText(R.id.tv_user_name, comment.getUser().getUsername());
                        holder.setText(R.id.tv_comment_body, Html.fromHtml(comment.getBody()));
                        holder.setText(R.id.tv_comment_date, DateTimeUtils.parseDateTime(comment.getUpdated_at()));
                        holder.setText(R.id.tv_comment_liked_count, String.valueOf(comment.getLikes_count()));
                        //设置comment是否like
                        final String cmtId = String.valueOf(comment.getId());
                        checkIfLike(shotsId, cmtId, holder);

                        holder.setOnViewClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });


                    }
                };
                rv_comment_list.setAdapter(adapter);
            }
        });
    }

    private void setCommentLike(boolean isLiked, String shotId, String cmtId, final CommonViewHolder holder) {
        if (isLiked) {
            AppController.getInstance().enqueuePostRequest(
                    new String[]{Constants.SHOTS, shotId, Constants.COMMENTS, cmtId},
                    Constants.REQ_TAG_LIKE_COMMENT,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() == 201 && response.body().string().contains("id")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_like);
                                    }
                                });
                            }
                        }
                    }
            );
        }else{
            AppController.getInstance().enqueueDeleteRequest(new String[]{Constants.SHOTS, shotId, Constants.COMMENTS, cmtId},
                    Constants.REQ_TAG_LIKE_COMMENT,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() == 204) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_unlike);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private boolean checkIfLike(String shotId, String cmtId, final CommonViewHolder holder) {
        String[] patterns = {Constants.SHOTS, shotId, Constants.COMMENTS, cmtId, Constants.LIKE};

        AppController.getInstance().enqueueGetRequest(
                patterns, null, Constants.REQ_TAG_CHECK_IF_LIKE_COMMENT,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int code = response.code();
                        final boolean liked = (code == 200) || (response.body().string().contains("id"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(liked)
                                    holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_like);
                                else
                                    holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_unlike);
                            }
                        });
                    }
                }
        );
        return false;
    }

    @Override
    protected void cancelRequestsOnStop() {

    }
}
