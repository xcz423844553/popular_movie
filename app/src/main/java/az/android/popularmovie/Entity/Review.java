package az.android.popularmovie.Entity;

public class Review {
    private String author;
    private String content;

    private Review() {
    }

    public static class Builder {
        private String author;
        private String content;

        public Builder() {
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Review build() {
            Review review = new Review();
            review.author = this.author;
            review.content = this.content;
            return review;
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
