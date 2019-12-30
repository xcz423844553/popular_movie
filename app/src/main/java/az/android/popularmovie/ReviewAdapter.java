package az.android.popularmovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import az.android.popularmovie.Entity.Review;
import az.android.popularmovie.Entity.Trailer;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    List<Review> reviews;
    Context mContext;

    public ReviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.list_item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        if (reviews != null) {
            holder.bind(reviews.get(position).getAuthor(), reviews.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    public void setReviews(List<Review> array) {
        if (array == null) return;
        this.reviews = array;
        this.notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewAuthor;
        TextView mTextViewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mTextViewAuthor = (TextView) itemView.findViewById(R.id.iv_review_author);
            mTextViewContent = (TextView) itemView.findViewById(R.id.iv_review_content);
        }

        public void bind(String author, String content) {
            mTextViewAuthor.setText(author);
            mTextViewContent.setText(content);
        }
    }
}
