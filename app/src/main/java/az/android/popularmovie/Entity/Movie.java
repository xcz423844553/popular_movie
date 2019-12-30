package az.android.popularmovie.Entity;

import android.graphics.Bitmap;

public class Movie {

    public static String TAG_MOVIE_ID = "movie_id";
    public static String TAG_MOVIE_IMGURL = "movie_img_url";
    public static String TAG_MOVIE_TITLE = "movie_title";
    public static String TAG_MOVIE_RATING = "movie_rating";
    public static String TAG_MOVIE_OVERVIEW = "movie_overview";
    public static String TAG_MOVIE_RELEASE_DATE = "movie_release_date";
    public static String TAG_MOVIE_POSTER = "movie_poster";
    private long id;
    private String imgUrl;
    private String title;
    private double rating;
    private String overview;
    private String releaseDate;
    private Bitmap poster;

    private Movie(long id) {
        this.id = id;
    }

    public static class Builder {
        private long id;
        private String imgUrl;
        private String title;
        private double rating;
        private String overview;
        private String releaseDate;
        private Bitmap poster;

        public Builder(long id) {
            this.id = id;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setImgUrl(String url) {
            this.imgUrl = url;
            return this;
        }

        public Builder setRating(double rating) {
            this.rating = rating;
            return this;
        }

        public Builder setOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder setPoster(Bitmap poster) {
            this.poster = poster;
            return this;
        }

        public Movie build() {
            Movie movie = new Movie(this.id);
            movie.imgUrl = this.imgUrl;
            movie.title = this.title;
            movie.rating = this.rating;
            movie.overview = this.overview;
            movie.releaseDate = this.releaseDate;
            movie.poster = this.poster;
            return movie;
        }
    }

    public long getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public double getRating() {
        return rating;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Bitmap getPoster() {
        return poster;
    }
}
