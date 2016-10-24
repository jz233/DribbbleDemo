package zjj.com.dribbbledemoapp.adapters.base;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import zjj.com.dribbbledemoapp.R;


public class CommonViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    /**
     * item布局整体View对象(可复用)
     */
    private View mConvertView;
    /**
     * item布局中控件id和View的集合
     */
    private SparseArray<View> mViews;


    public CommonViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    /**
     * 得到ViewHolder引用
     */
    public static CommonViewHolder getViewHolder(Context context, @LayoutRes int layoutId, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        CommonViewHolder holder = new CommonViewHolder(context, itemView, parent);
        return holder;
    }

    /**
     * 根据id得到item布局中某个控件引用
     */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public CommonViewHolder setText(@IdRes int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public CommonViewHolder setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }
    public CommonViewHolder setCircleImageResource(@IdRes int viewId, @DrawableRes int resId) {
        CircleImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }
    public CommonViewHolder setCircleImageUrl(@IdRes int viewId, String url) {
        CircleImageView view = getView(viewId);
        Picasso.with(mContext).load(url).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(view);
        return this;
    }
    public CommonViewHolder setImageUrl(@IdRes int viewId, String url) {
        ImageView view = getView(viewId);
        Picasso.with(mContext).load(url).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(view);
        return this;
    }

    public CommonViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public CommonViewHolder setOnViewClickListener(View.OnClickListener listener) {
        mConvertView.setOnClickListener(listener);
        return this;
    }

}
