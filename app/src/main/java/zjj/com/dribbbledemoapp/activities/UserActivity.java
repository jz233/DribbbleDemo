package zjj.com.dribbbledemoapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.base.BaseActivity;

public class UserActivity extends BaseActivity {


    @Override
    public void initView() {
        setContentView(R.layout.activity_user);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            int userId = intent.getIntExtra("userId", 0);
            Toast.makeText(this, userId+"", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void cancelRequestsOnStop() {

    }
}
