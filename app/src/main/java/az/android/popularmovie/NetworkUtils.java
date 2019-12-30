package az.android.popularmovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import az.android.popularmovie.Entity.Movie;
import az.android.popularmovie.Entity.Review;
import az.android.popularmovie.Entity.Trailer;

public class NetworkUtils {
    public static final String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String BASE_URL_HIGHRATED = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String BASE_URL_FAVORITE = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/w185/";
    public static final String BASE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";
    public static final String BASE_URL_DETAIL_PREFIX = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_URL_TRAILER_POSTFIX = "/videos?api_key=";
    public static final String BASE_URL_REVIEW_POSTFIX = "/reviews?api_key=";

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Movie> parseResponseToMovieList(String data) {
        if (data == null) return null;
        List<Movie> array = new ArrayList<>();
        try {
            JSONArray result = new JSONObject(data).getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                Movie movie = new Movie.Builder(obj.getLong("id"))
                        .setImgUrl(obj.getString("poster_path"))
                        .setTitle(obj.getString("title"))
                        .setRating(obj.getDouble("vote_average"))
                        .setOverview(obj.getString("overview"))
                        .setReleaseDate(obj.getString("release_date"))
                        .build();
                array.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return array;
    }

    public static List<Trailer> parseResponseToTrailerList(String data) {
        if (data == null) return null;
        List<Trailer> array = new ArrayList<>();
        try {
            JSONArray result = new JSONObject(data).getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                Trailer trailer = new Trailer.Builder()
                        .setVideoUrl(obj.getString("key"))
                        .setName(obj.getString("name"))
                        .build();
                array.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return array;
    }

    public static List<Review> parseResponseToReviewList(String data) {
        if (data == null) return null;
        List<Review> array = new ArrayList<>();
        try {
            JSONArray result = new JSONObject(data).getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                Review review = new Review.Builder()
                        .setAuthor(obj.getString("author"))
                        .setContent(obj.getString("content"))
                        .build();
                array.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return array;
    }
}
