package com.achievementtracker.dto.games_endpoint;

import java.time.LocalDate;

public class GameDTO {
    private Long storeId;
    private String title;
    private LocalDate releaseDate;
    private Double rating;
    private String capsuleImageURL;
    private int challengeRating;
    private Double difficultySpread;

    public GameDTO(Long storeId, String title, LocalDate releaseDate, Double rating,
                   String capsuleImageURL, int challengeRating, Double difficultySpread) {
        this.storeId = storeId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.capsuleImageURL = capsuleImageURL;
        this.challengeRating = challengeRating;
        this.difficultySpread = difficultySpread;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCapsuleImageURL() {
        return capsuleImageURL;
    }

    public void setCapsuleImageURL(String capsuleImageURL) {
        this.capsuleImageURL = capsuleImageURL;
    }

    public int getChallengeRating() {
        return challengeRating;
    }

    public void setChallengeRating(int challengeRating) {
        this.challengeRating = challengeRating;
    }

    public Double getDifficultySpread() {
        return difficultySpread;
    }

    public void setDifficultySpread(Double difficultySpread) {
        this.difficultySpread = difficultySpread;
    }

    @Override
    public String toString() {
        return "GameDTO{" +
                "storeId=" + storeId +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", rating=" + rating +
                ", capsuleImageURL='" + capsuleImageURL + '\'' +
                ", challengeRating=" + challengeRating +
                ", difficultySpread=" + difficultySpread +
                '}';
    }
}
