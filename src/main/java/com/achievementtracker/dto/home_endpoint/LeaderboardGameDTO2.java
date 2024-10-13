package com.achievementtracker.dto.home_endpoint;

import java.time.LocalDate;

public class LeaderboardGameDTO2 {
    private Long storeId;
    private String title;
    private String capsuleSmallImageURL;
    private LocalDate releaseDate;

    public LeaderboardGameDTO2(Long storeId, String title, String capsuleSmallImageURL, LocalDate achievementCount) {
        this.storeId = storeId;
        this.title = title;
        this.capsuleSmallImageURL = capsuleSmallImageURL;
        this.releaseDate = achievementCount;
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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "LeaderboardGameDTO2{" +
                "storeId=" + storeId +
                ", title='" + title + '\'' +
                ", capsuleSmallImageURL='" + capsuleSmallImageURL + '\'' +
                ", achievementCount=" + releaseDate +
                '}';
    }
}