package az.android.popularmovie.Entity;

public class Trailer {

    private String videoUrl;
    private String name;

    private Trailer() {
    }

    public static class Builder {
        private String videoUrl;
        private String name;

        public Builder() {
        }

        public Builder setVideoUrl(String videlUrl) {
            this.videoUrl = videlUrl;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Trailer build() {
            Trailer trailer = new Trailer();
            trailer.videoUrl = this.videoUrl;
            trailer.name = this.name;
            return trailer;
        }
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getName() {
        return name;
    }
}
