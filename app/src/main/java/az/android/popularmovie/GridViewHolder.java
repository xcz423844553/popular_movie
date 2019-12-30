package az.android.popularmovie;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import az.android.popularmovie.Entity.Movie;

public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface GridItemClickListner {
        void onGridItemClick(Movie movie);
    }

    View itemView;
    ImageView mGridImage;
    GridItemClickListner mOnClickListener;

    GridViewHolder(View itemView, GridItemClickListner mOnClickListener) {
        super(itemView);
        mGridImage = (ImageView) itemView.findViewById(R.id.iv_item);
        this.itemView = itemView;
        itemView.setOnClickListener(this);
        this.mOnClickListener = mOnClickListener;
    }

    void bind(String imageUrl) {
        Picasso.get().load(imageUrl).into(mGridImage);
    }

    void bind(Bitmap poster) {
        mGridImage.setImageBitmap(poster);
    }

    @Override
    public void onClick(View view) {
        mOnClickListener.onGridItemClick((Movie) itemView.getTag());
    }
}
