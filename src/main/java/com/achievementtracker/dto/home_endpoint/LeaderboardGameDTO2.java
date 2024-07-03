package com.achievementtracker.dto.home_endpoint;

public class LeaderboardGameDTO2 {
    private Long storeId;
    private String title;
    private String capsuleSmallImageURL;
    private long achievementCount;

    public LeaderboardGameDTO2(Long storeId, String title, String capsuleSmallImageURL, long achievementCount) {
        this.storeId = storeId;
        this.title = title;
        this.capsuleSmallImageURL = capsuleSmallImageURL;
        this.achievementCount = achievementCount;
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

    public long getAchievementCount() {
        return achievementCount;
    }

    public void setAchievementCount(long achievementCount) {
        this.achievementCount = achievementCount;
    }

    @Override
    public String toString() {
        return "LeaderboardGameDTO2{" +
                "storeId=" + storeId +
                ", title='" + title + '\'' +
                ", capsuleSmallImageURL='" + capsuleSmallImageURL + '\'' +
                ", achievementCount=" + achievementCount +
                '}';
    }
}