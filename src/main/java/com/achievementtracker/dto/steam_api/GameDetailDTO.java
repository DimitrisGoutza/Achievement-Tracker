package com.achievementtracker.dto.steam_api;

import com.achievementtracker.deserializer.GameDetailDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;

@JsonDeserialize(using = GameDetailDeserializer.class)
public class GameDetailDTO {
    private String title;
    private Long steamAppId;
    private String longDescription;
    private String shortDescription;
    private String headerImageUrl;
    private String capsuleImageUrl;
    private String capsuleSmallImageUrl;
    private boolean comingSoon;
    private LocalDate releaseDate;
    private String backgroundImageUrl;
    private String backgroundRawImageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSteamAppId() {
        return steamAppId;
    }

    public void setSteamAppId(Long steamAppId) {
        this.steamAppId = steamAppId;
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

    public boolean isComingSoon() {
        return comingSoon;
    }

    public void setComingSoon(boolean comingSoon) {
        this.comingSoon = comingSoon;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
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
                "title='" + title + '\'' +
                ", steamAppId=" + steamAppId +
                ", longDescription='" + longDescription + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", headerImageUrl='" + headerImageUrl + '\'' +
                ", capsuleImageUrl='" + capsuleImageUrl + '\'' +
                ", capsuleSmallImageUrl='" + capsuleSmallImageUrl + '\'' +
                ", comingSoon=" + comingSoon +
                ", releaseDate=" + releaseDate +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", backgroundRawImageUrl='" + backgroundRawImageUrl + '\'' +
                '}';
    }
}
