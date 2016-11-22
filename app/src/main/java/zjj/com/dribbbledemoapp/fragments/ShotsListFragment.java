package zjj.com.dribbbledemoapp.fragments;


import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.adapters.ShotsListAdapter;
import zjj.com.dribbbledemoapp.applications.AppController;
import zjj.com.dribbbledemoapp.base.BaseFragment;
import zjj.com.dribbbledemoapp.domains.Shot;
import zjj.com.dribbbledemoapp.listeners.OnLoadMoreListener;
import zjj.com.dribbbledemoapp.utils.Constants;

public class ShotsListFragment extends BaseFragment {

    private RecyclerView rv_art_list;
    private SwipeRefreshLayout swipe_refresh;
    private ShotsListAdapter adapter;
    private Handler mainHandler;
    private List<Shot> results;
    private int currentPage = 1;
    private int perPage = 12;
    private HashMap<String, String> params;

    public ShotsListFragment() {
    }

    public static ShotsListFragment newInstance() {
        ShotsListFragment fragment = new ShotsListFragment();
        return fragment;
    }

    @Override
    public View initView(LayoutInflater inflater) {
        mainHandler = new Handler(Looper.getMainLooper());

        View view = inflater.inflate(R.layout.fragment_art_list, null);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        rv_art_list = (RecyclerView) view.findViewById(R.id.rv_art_list);
        rv_art_list.setLayoutManager(new GridLayoutManager(context, 2));

        return view;
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
        params = new HashMap<>();
        //TODO per_page
        params.put("page", String.valueOf(currentPage));

        AppController.getInstance().enqueueGetRequest(
                new String[]{Constants.SHOTS},
                params,
                "list",
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipe_refresh.setRefreshing(false);
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        final Shot[] shotsList = new Gson().fromJson(body, Shot[].class);
                        results = new ArrayList<>(Arrays.asList(shotsList));

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                swipe_refresh.setRefreshing(false);
                                adapter = new ShotsListAdapter(context, results);
                                //下拉读取更多
                                addInfiniteScrollingListener();
                                //设置适配器
                                rv_art_list.setAdapter(adapter);
                            }
                        });
                    }
                });
    }

    private void addInfiniteScrollingListener() {
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int currentPage) {
                params.put("page", String.valueOf(currentPage));
                //网络请求读取下一页
                AppController.getInstance().enqueueGetRequest(
                        new String[]{Constants.SHOTS},
                        params,
                        "more",
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String body = response.body().string();
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Shot[] list = new Gson().fromJson(body, Shot[].class);
                                        List<Shot> incResults = Arrays.asList(list);
                                        results.addAll(incResults);
                                        adapter.setLoading(false);
                                    }
                                });
                            }
                        });
            }
        });

    }

}
