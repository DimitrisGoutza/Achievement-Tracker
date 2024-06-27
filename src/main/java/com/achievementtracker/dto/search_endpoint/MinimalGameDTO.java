package com.achievementtracker.dto.search_endpoint;

public class MinimalGameDTO {
    private Long storeId;
    private String title;
    private String capsuleSmallImageURL;

    public MinimalGameDTO(Long storeId, String title, String capsuleSmallImageURL) {
        this.storeId = storeId;
        this.title = title;
        this.capsuleSmallImageURL = capsuleSmallImageURL;
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

    @Override
    public String toString() {
        return "GameDTO{" +
                "storeId=" + storeId +
                ", title='" + title + '\'' +
                ", capsuleSmallImageURL='" + capsuleSmallImageURL + '\'' +
                '}';
    }
}
