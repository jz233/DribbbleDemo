package zjj.com.dribbbledemoapp.events;


import zjj.com.dribbbledemoapp.adapters.base.CommonViewHolder;

public class UpdateHolderEvent<T> {

    public CommonViewHolder holder;
    public T values;

    public UpdateHolderEvent(CommonViewHolder holder, T values) {
        this.holder = holder;
        this.values = values;
    }
}
