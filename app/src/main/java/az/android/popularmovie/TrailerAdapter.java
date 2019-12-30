package az.android.popularmovie;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import az.android.popularmovie.Entity.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    List<Trailer> trailers;
    Context mContext;

    public TrailerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.list_item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerViewHolder holder, int position) {
        if (trailers != null) {
            holder.bind(trailers.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return trailers == null ? 0 : trailers.size();
    }

    public void setTrailers(List<Trailer> array) {
        if (array == null) return;
        this.trailers = array;
        this.notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextViewName;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTextViewName = (TextView) itemView.findViewById(R.id.iv_trailer_title);
            itemView.setOnClickListener(this);
        }

        public void bind(String name) {
            mTextViewName.setText(name);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Trailer trailer = trailers.get(position);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getVideoUrl()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailer.getVideoUrl()));
            try {
                view.getContext().startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                view.getContext().startActivity(webIntent);
            }
        }
    }
}
