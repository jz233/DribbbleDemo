package zjj.com.dribbbledemoapp.applications;


import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;


import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import zjj.com.dribbbledemoapp.utils.Constants;

public class AppController extends Application {

    private static OkHttpClient client;
    private static AppController instance;
    private final static Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/

        instance = this;
//        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(getClient()));
//        EventBus.builder().throwSubscriberException(true).installDefaultEventBus();
        Picasso picasso = new Picasso.Builder(instance).downloader(new OkHttp3Downloader(getClient())).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Toast.makeText(AppController.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).build();
        Picasso.setSingletonInstance(picasso);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

    }

    public static synchronized AppController getInstance() {
        return instance;
    }

    public static synchronized OkHttpClient getClient() {
        if (client == null) {
            synchronized (AppController.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .readTimeout(5000, TimeUnit.MILLISECONDS)
                            .connectTimeout(5000, TimeUnit.MILLISECONDS)
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
        return client;
    }

    public void enqueueGetRequest(String[] patterns, String tag, Callback callBack){
        enqueueGetRequest(patterns, null, tag, callBack);
    }

    public void enqueueGetRequest(String[] patterns, HashMap<String, String> params, String tag, Callback callBack) {
        String url = buildUrl(Constants.METHOD_GET, patterns, params);
        Log.d("AppController", url);
        Request request = new Request.Builder().get().url(url).tag(tag).build();
        getClient().newCall(request).enqueue(callBack);
    }

    public void enqueuePostRequest(String[] patterns, String tag, Callback callBack){
        String url = buildUrl(Constants.METHOD_POST, patterns, null);
        Log.d("AppController", "POST " + url);
        FormBody.Builder builder = new FormBody.Builder();
        FormBody body = builder.add(Constants.TAG_ACCESS_TOKEN, Constants.AUTH_ACCESS_TOKEN)
                .build();
        Request request = new Request.Builder().url(url).post(body).tag(tag).build();
        getClient().newCall(request).enqueue(callBack);
    }

    public void enqueueDeleteRequest(String[] patterns, String tag, Callback callBack) {
        String url = buildUrl(Constants.METHOD_DELETE, patterns, null);
        Log.d("AppController", "DELETE " + url);
        FormBody.Builder builder = new FormBody.Builder();
        FormBody body = builder.add(Constants.TAG_ACCESS_TOKEN, Constants.AUTH_ACCESS_TOKEN)
                .build();
        Request request = new Request.Builder().url(url).delete(body).tag(tag).build();
        getClient().newCall(request).enqueue(callBack);
    }

    /**
     * 根据参数生成url
     */
    private String buildUrl(String requestMethod, String[] patterns, HashMap<String, String> params) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        HttpUrl.Builder segment = builder.scheme("https")
                .host(Constants.CLIENT_HOST)
                .addPathSegment(Constants.CLIENT_VERSION);
        for (String pattern : patterns) {
            segment = segment.addPathSegment(pattern);
        }
        if (params != null) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                segment = segment.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        //添加 API token
        if (requestMethod.equals(Constants.METHOD_GET)) {
            segment = segment.addQueryParameter(Constants.TAG_ACCESS_TOKEN, Constants.CLIENT_ACCESS_TOKEN);
        }
        HttpUrl httpUrl = segment.build();
        return httpUrl.toString();
    }

    public static void updateUIOnMainThread(Runnable r) {
        mainHandler.post(r);
    }

}
