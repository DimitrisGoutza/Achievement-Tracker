package com.achievementtracker.entity;

import com.achievementtracker.dto.GameDetailDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "game_detail")
public class GameDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "long_description")
    private String longDescription;
    @Column(name = "header_image")
    private String headerImageURL;
    @Column(name = "capsule_image")
    private String capsuleImageURL;
    @Column(name = "capsule_small_image")
    private String capsuleSmallImageURL;
    @Column(name = "background_image")
    private String backgroundImageURL;
    @Column(name = "background_raw_image")
    private String backgroundRawImageURL;
    @Column(name = "total_achievements")
    private int totalAchievements;
    @OneToOne(mappedBy = "gameDetail", cascade = CascadeType.ALL)
    private Game game;

    public GameDetail() {
    }

    public GameDetail(String shortDescription, String longDescription, String headerImageURL,
                      String capsuleImageURL, String capsuleSmallImageURL, String backgroundImageURL,
                      String backgroundRawImageURL, int totalAchievements) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.headerImageURL = headerImageURL;
        this.capsuleImageURL = capsuleImageURL;
        this.capsuleSmallImageURL = capsuleSmallImageURL;
        this.backgroundImageURL = backgroundImageURL;
        this.backgroundRawImageURL = backgroundRawImageURL;
        this.totalAchievements = totalAchievements;
    }

    public GameDetail(GameDetailDTO gameDetailDTO) {
        this.shortDescription = gameDetailDTO.getShortDescription();
        this.longDescription = gameDetailDTO.getLongDescription();
        this.headerImageURL = gameDetailDTO.getHeaderImageUrl();
        this.capsuleImageURL = gameDetailDTO.getCapsuleImageUrl();
        this.capsuleSmallImageURL = gameDetailDTO.getCapsuleSmallImageUrl();
        this.backgroundImageURL = gameDetailDTO.getBackgroundImageUrl();
        this.backgroundRawImageURL = gameDetailDTO.getBackgroundRawImageUrl();
        this.totalAchievements = gameDetailDTO.getTotalAchievements();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getHeaderImageURL() {
        return headerImageURL;
    }

    public void setHeaderImageURL(String headerImageURL) {
        this.headerImageURL = headerImageURL;
    }

    public String getCapsuleImageURL() {
        return capsuleImageURL;
    }

    public void setCapsuleImageURL(String capsuleImageURL) {
        this.capsuleImageURL = capsuleImageURL;
    }

    public String getCapsuleSmallImageURL() {
        return capsuleSmallImageURL;
    }

    public void setCapsuleSmallImageURL(String capsuleSmallImageURL) {
        this.capsuleSmallImageURL = capsuleSmallImageURL;
    }

    public String getBackgroundImageURL() {
        return backgroundImageURL;
    }

    public void setBackgroundImageURL(String backgroundImageURL) {
        this.backgroundImageURL = backgroundImageURL;
    }

    public String getBackgroundRawImageURL() {
        return backgroundRawImageURL;
    }

    public void setBackgroundRawImageURL(String backgroundRawImageURL) {
        this.backgroundRawImageURL = backgroundRawImageURL;
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(int totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "GameDetail{" +
                "id=" + id +
                ", shortDescription='" + shortDescription + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", headerImageURL='" + headerImageURL + '\'' +
                ", capsuleImageURL='" + capsuleImageURL + '\'' +
                ", capsuleSmallImageURL='" + capsuleSmallImageURL + '\'' +
                ", backgroundImageURL='" + backgroundImageURL + '\'' +
                ", backgroundRawImageURL='" + backgroundRawImageURL + '\'' +
                ", totalAchievements=" + totalAchievements +
                '}';
    }
}
