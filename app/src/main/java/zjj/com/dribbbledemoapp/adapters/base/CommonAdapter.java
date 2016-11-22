package zjj.com.dribbbledemoapp.adapters.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;


public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    protected Context context;
    protected int layoutId;
    protected List<T> data;
    protected LayoutInflater inflater;

    public CommonAdapter(Context context, int layoutId, List<T> data) {
        this.context = context;
        this.layoutId = layoutId;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, layoutId, parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
//        holder.updatePosition(position);
        convert(holder, data.get(position));
    }

    /**
     * 将数据绑定到视图 (在onBindViewHolder中调用的抽象方法, ***必须重写)
     */
    protected abstract void convert(CommonViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return data.size();
    }
}
