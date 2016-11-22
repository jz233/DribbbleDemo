package zjj.com.dribbbledemoapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import zjj.com.dribbbledemoapp.R;
import zjj.com.dribbbledemoapp.activities.DetailsActivity;
import zjj.com.dribbbledemoapp.activities.UserActivity;
import zjj.com.dribbbledemoapp.domains.Shot;
import zjj.com.dribbbledemoapp.listeners.OnLoadMoreListener;

public class ShotsListAdapter extends RecyclerView.Adapter{

    private final int ITEM_TYPE_VIEW = 0;
    private final int ITEM_TYPE_LOADING = 1;
    private GridLayoutManager layoutManager;
    private Context context;
    private List<Shot> shotsList;
    private int totalCount;
    private int lastVisiblePosition;
    private boolean isLoading = false;
    private final int threshold = 2;
    private int previousTotalCount;
    private OnLoadMoreListener listener;
    private int currentPage = 1;
    private int firstVisiblePosition;
    private int visibleItemCount;

    public ShotsListAdapter(Context context, List<Shot> shotsList) {
        this.context = context;
        this.shotsList = shotsList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalCount = layoutManager.getItemCount();
                visibleItemCount = recyclerView.getChildCount();
                firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
//                lastVisiblePosition = layoutManager.findLastVisibleItemPosition();  //position比count小1

                if (!isLoading && (firstVisiblePosition+visibleItemCount + threshold)>= totalCount) {
                    isLoading = true;
                    previousTotalCount = totalCount;
                    listener.onLoadMore(++currentPage);
                }
                if (isLoading && (totalCount > previousTotalCount)) {
                    isLoading = false;
                }

            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shot_cardview, null);
            return new ShotsViewHolder(view);
        } else if (viewType == ITEM_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shot_cardview, null);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ShotsViewHolder) {
            Shot shot = shotsList.get(position);
            final ShotsViewHolder sHolder = (ShotsViewHolder) holder;
            sHolder.shots_title.setText(shot.getTitle());
            sHolder.shots_user.setText(shot.getUser().getName());
            sHolder.shots_views_count.setText(String.valueOf(shot.getViews_count()));
            sHolder.shots_comments_count.setText(String.valueOf(shot.getComments_count()));
            sHolder.shots_likes_count.setText(String.valueOf(shot.getLikes_count()));

            Picasso.with(context).load(shot.getImages().getNormal())
                    .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                    .into(sHolder.shots_thumb, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Toast.makeText(context, "loading pic error", Toast.LENGTH_SHORT).show();
                    Picasso.with(context).cancelRequest(sHolder.shots_thumb);
                }
            });
            Picasso.with(context).load(shot.getUser().getAvatar_url())
                    .placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar)
                    .into(sHolder.shots_user_avatar, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "loading avatar error", Toast.LENGTH_SHORT).show();
                            Picasso.with(context).cancelRequest(sHolder.shots_thumb);
                        }
                    });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder lHolder = (LoadingViewHolder) holder;
        }

    }


    @Override
    public int getItemCount() {
        return shotsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return shotsList.get(position) == null ? ITEM_TYPE_LOADING : ITEM_TYPE_VIEW;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    class ShotsViewHolder extends RecyclerView.ViewHolder{

        private ImageView shots_thumb;
        private CircleImageView shots_user_avatar;
        private TextView shots_title;
        private TextView shots_user;
        private TextView shots_views_count;
        private TextView shots_comments_count;
        private TextView shots_likes_count;

        public ShotsViewHolder(View itemView) {
            super(itemView);

            shots_thumb = (ImageView) itemView.findViewById(R.id.shots_thumb);
            shots_user_avatar = (CircleImageView) itemView.findViewById(R.id.shots_user_avatar);
            shots_title = (TextView) itemView.findViewById(R.id.shots_title);
            shots_user = (TextView) itemView.findViewById(R.id.shots_user);
            shots_views_count = (TextView) itemView.findViewById(R.id.shots_views_count);
            shots_comments_count = (TextView) itemView.findViewById(R.id.shots_comments_count);
            shots_likes_count = (TextView) itemView.findViewById(R.id.shots_likes_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    Shot shot = shotsList.get(getAdapterPosition());
                    intent.putExtra("id", shot.getId());
                    context.startActivity(intent);
                }
            });
            shots_user_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, UserActivity.class);
                    Shot shot = shotsList.get(getAdapterPosition());
                    intent.putExtra("userId", shot.getUser().getId());
                    intent.putExtra("name", shot.getUser().getName());
                    context.startActivity(intent);
                }
            });
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar pb_loading;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            pb_loading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
        }
    }

}
