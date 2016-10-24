package zjj.com.dribbbledemoapp.base;


import org.greenrobot.eventbus.EventBus;

public class EventBusActivity extends BaseActivity {
    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    protected void cancelRequestsOnStop() {

    }
}
