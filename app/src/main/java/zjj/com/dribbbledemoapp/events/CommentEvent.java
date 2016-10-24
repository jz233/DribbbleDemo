package zjj.com.dribbbledemoapp.events;


import zjj.com.dribbbledemoapp.adapters.base.CommonViewHolder;

public class CommentEvent {

    public String cmtId;
    public boolean like;
    public CommonViewHolder holder;

    public CommentEvent(String cmtId, boolean like, CommonViewHolder holder) {
        this.cmtId = cmtId;
        this.like = like;
        this.holder = holder;
    }
}
