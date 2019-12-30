package az.android.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import az.android.popularmovie.Entity.Movie;

public class GridAdapter extends RecyclerView.Adapter<GridViewHolder> {

    private static final String TAG_LOG = GridAdapter.class.getSimpleName();
    private Context mContext;
    private List<Movie> dataSource;
    private GridViewHolder.GridItemClickListner mOnClickItemListener;

    public GridAdapter(Context context, GridViewHolder.GridItemClickListner mOnClickItemListener) {
        this.mContext = context;
        this.mOnClickItemListener = mOnClickItemListener;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_movie, parent, false);
        return new GridViewHolder(view, mOnClickItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        if (dataSource == null) return;
        Movie movie = dataSource.get(position);
        String imageUrl = NetworkUtils.BASE_URL_IMAGE + movie.getImgUrl();
        if (movie.getPoster() == null) {
            holder.bind(imageUrl);
        } else {
            holder.bind(movie.getPoster());
        }
        holder.itemView.setTag(movie);
    }

    @Override
    public int getItemCount() {
        return dataSource == null ? 0 : dataSource.size();
    }

    public void swapArray(List<Movie> array) {
        if (array == null) return;
        dataSource = array;
        this.notifyDataSetChanged();
    }
}
