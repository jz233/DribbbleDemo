package zjj.com.dribbbledemoapp.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

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
        String shotsId = getIntent().getStringExtra("shotsId");
        String shotsTitle = getIntent().getStringExtra("shotsTitle");
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new CommonAdapter<Comment>(context, R.layout.item_comment_cardview, cmtList) {
                                        @Override
                                        protected void convert(CommonViewHolder holder, Comment comment) {
                                            holder.setCircleImageUrl(R.id.iv_user_avatar, comment.getUser().getAvatar_url());
                                            holder.setText(R.id.tv_user_name, comment.getUser().getUsername());
                                            holder.setText(R.id.tv_comment_body, Html.fromHtml(comment.getBody()));
                                            holder.setText(R.id.tv_comment_date, DateTimeUtils.parseDateTime(comment.getUpdated_at()));
                                            holder.setText(R.id.tv_comment_liked_count, String.valueOf(comment.getLikes_count()));

                                        }
                                    };
                                    rv_comment_list.setAdapter(adapter);
                                }
                            });
                        }
                    });
        }
    }

    @Override
    protected void cancelRequestsOnStop() {

    }
}
