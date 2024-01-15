package com.achievementtracker.dto;

import com.achievementtracker.deserializer.GameDetailDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = GameDetailDeserializer.class)
public class GameDetailDTO {
    private String name;
    private String id;
    private String longDescription;
    private String shortDescription;
    private String headerImageUrl;
    private String capsuleImageUrl;
    private String capsuleSmallImageUrl;
    private int totalAchievements;
    private boolean comingSoon;
    private String releaseDate;
    private String backgroundImageUrl;
    private String backgroundRawImageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }

    public String getCapsuleImageUrl() {
        return capsuleImageUrl;
    }

    public void setCapsuleImageUrl(String capsuleImageUrl) {
        this.capsuleImageUrl = capsuleImageUrl;
    }

    public String getCapsuleSmallImageUrl() {
        return capsuleSmallImageUrl;
    }

    public void setCapsuleSmallImageUrl(String capsuleSmallImageUrl) {
        this.capsuleSmallImageUrl = capsuleSmallImageUrl;
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(int totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public boolean isComingSoon() {
        return comingSoon;
    }

    public void setComingSoon(boolean comingSoon) {
        this.comingSoon = comingSoon;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getBackgroundRawImageUrl() {
        return backgroundRawImageUrl;
    }

    public void setBackgroundRawImageUrl(String backgroundRawImageUrl) {
        this.backgroundRawImageUrl = backgroundRawImageUrl;
    }

    @Override
    public String toString() {
        return "GameDetailDTO{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", headerImageUrl='" + headerImageUrl + '\'' +
                ", capsuleImageUrl='" + capsuleImageUrl + '\'' +
                ", capsuleSmallImageUrl='" + capsuleSmallImageUrl + '\'' +
                ", totalAchievements=" + totalAchievements +
                ", comingSoon=" + comingSoon +
                ", releaseDate='" + releaseDate + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", backgroundRawImageUrl='" + backgroundRawImageUrl + '\'' +
                '}';
    }
}
