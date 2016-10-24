package zjj.com.dribbbledemoapp.adapters.base;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> {
    private MultiItemTypeSupport mSupport;

    public MultiItemCommonAdapter(Context context, List<T> data, MultiItemTypeSupport<T> support) {
        super(context, -1, data);
        mSupport = support;
    }

    @Override
    public int getItemViewType(int position) {
        return mSupport.getItemViewType(position, mData.get(position));
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mSupport.getLayoutId(viewType);
        CommonViewHolder holder = CommonViewHolder.getViewHolder(mContext, layoutId, parent);
        return holder;
    }
}
