package zjj.com.dribbbledemoapp.activities;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.applications.AppController;
import zjj.com.dribbbledemoapp.adapters.base.CommonAdapter;
import zjj.com.dribbbledemoapp.adapters.base.CommonViewHolder;
import zjj.com.dribbbledemoapp.base.EventBusActivity;
import zjj.com.dribbbledemoapp.base.SimpleCallback;
import zjj.com.dribbbledemoapp.domains.Comment;
import zjj.com.dribbbledemoapp.events.CommentEvent;
import zjj.com.dribbbledemoapp.listeners.OnUpdateUIListener;
import zjj.com.dribbbledemoapp.utils.Constants;
import zjj.com.dribbbledemoapp.utils.DateTimeUtils;

public class CommentActivity extends EventBusActivity {

    private Toolbar toolbar;
    private RecyclerView rv_comment_list;
    private List<Comment> cmtList;
    private CommonAdapter<Comment> adapter;
    private String shotsId;
    private String shotsTitle;
    private SwipeRefreshLayout swipe_refresh;

    @Override
    public void initView() {
        setContentView(R.layout.activity_comment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        shotsId = getIntent().getStringExtra("shotsId");
        shotsTitle = getIntent().getStringExtra("shotsTitle");
        actionBar.setTitle(shotsTitle);

        rv_comment_list = (RecyclerView) findViewById(R.id.rv_comment_list);
        rv_comment_list.setLayoutManager(new LinearLayoutManager(this));

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void initListener() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    @Override
    public void initData() {
        swipe_refresh.setRefreshing(true);
        if (!TextUtils.isEmpty(shotsId)) {
            AppController.getInstance().enqueueGetRequest(
                    new String[]{Constants.SHOTS, shotsId, Constants.COMMENTS},
                    "comments",
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
                swipe_refresh.setRefreshing(false);
                adapter = new CommonAdapter<Comment>(context, R.layout.item_comment_cardview, cmtList) {
                    @Override
                    protected void convert(final CommonViewHolder holder, final Comment comment) {
                        holder.setCircleImageUrl(R.id.iv_user_avatar, comment.getUser().getAvatar_url());
                        holder.setText(R.id.tv_name, comment.getUser().getUsername());
                        holder.setText(R.id.tv_comment_body, Html.fromHtml(comment.getBody()));
                        holder.setText(R.id.tv_comment_date, DateTimeUtils.parseDateTime(comment.getUpdated_at()));
                        holder.setText(R.id.tv_comment_liked_count, String.valueOf(comment.getLikes_count()));
                        //设置comment是否like
                        final String cmtId = String.valueOf(comment.getId());
                        checkIfLike(shotsId, cmtId, holder, new OnUpdateUIListener<Boolean>() {
                            @Override
                            public void updateUI(final Boolean result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result) {
                                            holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_like);
                                            holder.setOnViewClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                    AlertDialog dialog = builder.setItems(new String[]{"Unlike this comment", "dummy"}, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    }).create();
                                                    dialog.show();
                                                }
                                            });
                                        } else {
                                            holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_unlike);
                                            holder.setOnViewClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                    AlertDialog dialog = builder.setItems(new String[]{"Like this comment", "dummy"}, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    }).create();
                                                    dialog.show();
                                                }
                                            });
                                        }
                                    }
                                });
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
        } else {
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

    private void checkIfLike(String shotId, final String cmtId, final CommonViewHolder holder, final OnUpdateUIListener listener) {
        String[] patterns = {Constants.SHOTS, shotId, Constants.COMMENTS, cmtId, Constants.LIKE};

        AppController.getInstance().enqueueGetRequest(
                patterns, Constants.REQ_TAG_CHECK_IF_LIKE_COMMENT,
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
                                if (liked)
                                    holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_like);
                                else
                                    holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_unlike);
                            }
                        });
                        holder.setOnViewClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new CommentEvent(cmtId, liked, holder));
                            }
                        });
                    }
                }
        );
    }

    @Override
    protected void cancelRequestsOnStop() {

    }

    /**
     * 处理comment事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleCommentEvent(CommentEvent event) {
        final boolean like = event.like;
        final String cmtId = event.cmtId;
        final CommonViewHolder holder = event.holder;

        String[] item = like ? new String[]{"Unlike this comment"} : new String[]{"Like this comment"};
        //显示Like/unlike this comment
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] patterns = {Constants.SHOTS, shotsId, Constants.COMMENTS, cmtId, Constants.LIKE};
                        if (like) {
                            AppController.getInstance().enqueueDeleteRequest(patterns, Constants.REQ_TAG_UNLIKE_COMMENT,
                                    new SimpleCallback() {
                                        @Override
                                        protected void handleResponse(Response response) throws IOException {
                                            if (response.code() == 204)
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_unlike);
                                                    }
                                                });
                                        }
                                    });
                        } else {
                            AppController.getInstance().enqueuePostRequest(patterns, Constants.REQ_TAG_LIKE_COMMENT,
                                    new SimpleCallback() {
                                        @Override
                                        protected void handleResponse(Response response) throws IOException {
                                            if (response.code() == 201 && response.body().string().contains("id"))
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                            holder.setImageResource(R.id.iv_like_comment, R.drawable.ic_like);
                                                    }
                                                });
                                        }
                                    });
                        }
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (android.R.id.home == itemId) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
