package zjj.com.dribbbledemoapp.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        initView();
        initListener();
        initData();

    }

    public abstract void initView();
    public abstract void initListener();
    public abstract void initData();


    @Override
    protected void onStop() {
        super.onStop();
        cancelRequestsOnStop();
    }

    /**
     * onStop调用时取消相应的网络请求
     */
    protected abstract void cancelRequestsOnStop();
}
