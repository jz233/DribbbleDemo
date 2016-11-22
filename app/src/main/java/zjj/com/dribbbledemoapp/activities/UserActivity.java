package zjj.com.dribbbledemoapp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.adapters.base.CommonAdapter;
import zjj.com.dribbbledemoapp.adapters.base.CommonViewHolder;
import zjj.com.dribbbledemoapp.applications.AppController;
import zjj.com.dribbbledemoapp.base.BaseActivity;
import zjj.com.dribbbledemoapp.domains.Shot;
import zjj.com.dribbbledemoapp.domains.User;
import zjj.com.dribbbledemoapp.utils.Constants;

public class UserActivity extends BaseActivity {


    private Toolbar toolbar;
    private CircleImageView iv_user_avatar;
    private User user;
    private boolean loadSuccess;
    private TextView tv_name;
    private TextView tv_user_location;
    private TextView tv_user_bio;
    private RecyclerView rv_shots_list;
    private String userId;
    private Shot[] shots;
    private ArrayList<Shot> shotsList;
    private String name;
    private ActionBar actionBar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_user);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv_user_avatar = (CircleImageView) findViewById(R.id.iv_user_avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_user_location = (TextView) findViewById(R.id.tv_user_location);
        tv_user_bio = (TextView) findViewById(R.id.tv_user_bio);
        rv_shots_list = (RecyclerView) findViewById(R.id.rv_shots_list);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = String.valueOf(intent.getIntExtra("userId", 0));
            name = intent.getStringExtra("name");
            actionBar.setTitle(name);

            AppController.getInstance().enqueueGetRequest(
                    new String[]{Constants.USERS, userId},
                    "users",
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body = response.body().string();
                            user = new Gson().fromJson(body, User.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayData();
                                }
                            });
                        }
                    });
        }

    }

    private void displayData() {
        loadImage();
        loadInfo();
        loadShots();
    }

    private void loadImage() {
        if (!loadSuccess) {
            Picasso.with(this).load(user.getAvatar_url())
                    .error(R.drawable.default_avatar).tag("image")
                    .into(iv_user_avatar, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            loadSuccess = true;
                            Toast.makeText(context, "load success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError() {
                            loadSuccess = false;
                            Picasso.with(context).cancelRequest(iv_user_avatar);
                            Toast.makeText(context, "load error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadInfo() {
        tv_name.setText(user.getName());
        tv_user_location.setText(user.getLocation());
        tv_user_bio.setText(user.getBio());
    }

    private void loadShots() {
        AppController.getInstance().enqueueGetRequest(
                new String[]{Constants.USERS, userId, Constants.SHOTS},
                Constants.USERS_SHOTS,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body =  response.body().string();
                        shots = new Gson().fromJson(body, Shot[].class);
                        shotsList = new ArrayList<>(Arrays.asList(shots));
                        Log.d("UserActivity", body);
                        displayUserShotsList();
                    }
                }
        );
    }

    private void displayUserShotsList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rv_shots_list.setLayoutManager(new GridLayoutManager(UserActivity.this, 2));
                rv_shots_list.setAdapter(new CommonAdapter<Shot>(context, R.layout.item_users_shot_cardview, shotsList) {
                    @Override
                    protected void convert(CommonViewHolder holder, Shot shot) {
                        holder.setImageUrl(R.id.shots_thumb, shot.getImages().getTeaser());
                        holder.setText(R.id.shots_title, shot.getTitle());
                        holder.setText(R.id.shots_views_count, String.valueOf(shot.getViews_count()));
                        holder.setText(R.id.shots_comments_count, String.valueOf(shot.getComments_count()));
                        holder.setText(R.id.shots_likes_count, String.valueOf(shot.getLikes_count()));
                    }
                });

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void cancelRequestsOnStop() {

    }
}
