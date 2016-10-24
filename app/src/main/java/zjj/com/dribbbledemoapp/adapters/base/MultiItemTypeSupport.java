package zjj.com.dribbbledemoapp.adapters.base;


public interface MultiItemTypeSupport<T> {
    /**
     * 根据item类型得到布局id
     */
    int getLayoutId(int itemType);

    /**
     *  根据数据内容返回item的类型
     */
    int getItemViewType(int position, T t);
}
