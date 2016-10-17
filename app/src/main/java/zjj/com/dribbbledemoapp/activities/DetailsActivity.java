package zjj.com.dribbbledemoapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.applications.AppController;
import zjj.com.dribbbledemoapp.base.BaseActivity;
import zjj.com.dribbbledemoapp.domains.Shot;
import zjj.com.dribbbledemoapp.utils.Constants;
import zjj.com.dribbbledemoapp.utils.DateTimeUtils;

public class DetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_detail_image;
    private Toolbar toolbar;
    private FloatingActionButton fab_like;

    private CircleImageView iv_user_avatar;
    private TextView tv_user_name;
    private TextView tv_update_time;
    private TextView tv_description;
    private Handler mainHandler;
    private Shot shot;
    private boolean isLiked;
    private Button btn_comment;

    @Override
    public void initView() {
        setContentView(R.layout.activity_details);
        mainHandler = new Handler(Looper.getMainLooper());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_detail_image = (ImageView) findViewById(R.id.iv_detail_image);
        iv_user_avatar = (CircleImageView) findViewById(R.id.iv_user_avatar);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_update_time = (TextView) findViewById(R.id.tv_update_time);
        fab_like = (FloatingActionButton) findViewById(R.id.fab_like);
        tv_description = (TextView) findViewById(R.id.tv_description);
        btn_comment = (Button) findViewById(R.id.btn_comment);

        setSupportActionBar(toolbar);
    }

    @Override
    public void initListener() {
        fab_like.setOnClickListener(this);
        tv_user_name.setOnClickListener(this);
        iv_user_avatar.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id", 0);
            AppController.getInstance().enqueueGetRequest(
                    new String[]{Constants.SHOTS, String.valueOf(id)}
                    , null,
                    "details",
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String body = response.body().string();
                            shot = new Gson().fromJson(body, Shot.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayData(shot);
                                }
                            });
                        }
                    });
        }

    }
    private void displayData(Shot body) {
        setTitle(body.getTitle());
//        Glide.with(this).load(body.getImages().getHidpi()).asBitmap().into(iv_detail_image);
//        Glide.with(this).load(body.getUser().getAvatar_url()).asBitmap().into(iv_user_avatar);
        Picasso.with(this).load(body.getImages().getHidpi()).error(R.drawable.placeholder).tag("image").into(iv_detail_image);
        Picasso.with(this).load(body.getUser().getAvatar_url()).error(R.drawable.default_avatar).tag("image").into(iv_user_avatar);
        tv_user_name.setText(body.getUser().getName());
        Spanned html = Html.fromHtml(body.getDescription());
        if (!TextUtils.isEmpty(html))
            tv_description.setText(html);

        String dateTime = body.getUpdated_at();
        if (!TextUtils.isEmpty(dateTime)) {
            tv_update_time.setText(DateTimeUtils.parseDateTime(dateTime));
        }

        //默认就是这样显示
//        fab_like.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
//        fab_like.setImageResource(R.drawable.ic_like);

//        GET /shots/:id/like
        checkIfLike(String.valueOf(body.getId()));

    }

    private void checkIfLike(String id) {
        AppController.getInstance().enqueueGetRequest(new String[]{Constants.SHOTS, String.valueOf(id), Constants.LIKE},
                null,
                "check",
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int code = response.code();
                        String body = response.body().string();
                        if (code == 200 && body.contains("id")) {
                            setFabLiked(true);
                        } else {
                            setFabLiked(false);
                        }
                    }
                });
    }

    private void setFabLiked(final boolean flag) {
        isLiked = flag;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    fab_like.setBackgroundTintList(getResources().getColorStateList(R.color.liked));
                    fab_like.setImageResource(R.drawable.ic_liked);
                } else {
                    fab_like.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    fab_like.setImageResource(R.drawable.ic_unlike);
                }
            }
        });
    }

    /**
     * 发送Fab点击Like请求
     */
    private void enqueueLikeRequest() {
        if (!isLiked) {
            AppController.getInstance().enqueuePostRequest(
                    new String[]{Constants.SHOTS, String.valueOf(shot.getId()), Constants.LIKE},
                    "like",
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            int code = response.code();
                            if (code == 201) {
                                String body = response.body().string();
                                if (body.contains("id")) {
                                    setFabLiked(true);
                                }
                            }
                        }
                    });
        } else {
            AppController.getInstance().enqueueDeleteRequest(
                    new String[]{Constants.SHOTS, String.valueOf(shot.getId()), Constants.LIKE},
                    "unlike",
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            int code = response.code();
                            if (code == 204) {
                                setFabLiked(false);
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_like:
                enqueueLikeRequest();
                break;
            case R.id.tv_user_name:
                startUserActivity();
                break;
            case R.id.iv_user_avatar:
                startUserActivity();
                break;
            case R.id.btn_comment:
                startCommentActivity();
                break;
        }
    }


    private void startUserActivity() {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("userId", shot.getUser().getId());
        startActivity(intent);
    }

    private void startCommentActivity() {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("shotsId", String.valueOf(shot.getId()));
        intent.putExtra("shotsTitle", shot.getTitle());
        startActivity(intent);
    }


    @Override
    protected void cancelRequestsOnStop() {
        //取消准备进行&正在进行的details请求
        List<Call> calls = AppController.getClient().dispatcher().queuedCalls();
        for (Call call : calls) {
            if ("details".equals(call.request().tag()) || "check".equals(call.request().tag())) {
                call.cancel();
            }
        }
        calls = AppController.getClient().dispatcher().runningCalls();
        for (Call call : calls) {
            if ("details".equals(call.request().tag()) || "check".equals(call.request().tag())) {
                call.cancel();
            }
        }
        //取消图片下载请求
        Picasso.with(this).cancelTag("image");
    }
}
