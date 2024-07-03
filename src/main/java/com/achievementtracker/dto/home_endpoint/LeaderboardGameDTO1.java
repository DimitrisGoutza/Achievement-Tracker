package com.achievementtracker.dto.home_endpoint;

public class LeaderboardGameDTO1 {
    private Long storeId;
    private String title;
    private String capsuleSmallImageURL;
    private int challengeRating;

    public LeaderboardGameDTO1(Long storeId, String title, String capsuleSmallImageURL, int challengeRating) {
        this.storeId = storeId;
        this.title = title;
        this.capsuleSmallImageURL = capsuleSmallImageURL;
        this.challengeRating = challengeRating;
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

    public String getCapsuleSmallImageURL() {
        return capsuleSmallImageURL;
    }

    public void setCapsuleSmallImageURL(String capsuleSmallImageURL) {
        this.capsuleSmallImageURL = capsuleSmallImageURL;
    }

    public int getChallengeRating() {
        return challengeRating;
    }

    public void setChallengeRating(int challengeRating) {
        this.challengeRating = challengeRating;
    }

    @Override
    public String toString() {
        return "LeaderboardGameDTO1{" +
                "storeId=" + storeId +
                ", title='" + title + '\'' +
                ", capsuleSmallImageURL='" + capsuleSmallImageURL + '\'' +
                ", challengeRating=" + challengeRating +
                '}';
    }
}