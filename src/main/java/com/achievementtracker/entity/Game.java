package com.achievementtracker.entity;

import com.achievementtracker.dto.GameDetailDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "GAME")
public class Game {
    @Id
    // no auto-generation since it is provided by Steam API and is unique
    @Column(name = "STORE_ID")
    private Long storeId;
    @NotNull
    @Column(name = "STEAM_APP_ID")
    private Long steamAppId;
    @NotNull
    @Column(name = "TITLE")
    private String title;
    @Column(name = "RELEASE_DATE")
    private LocalDate releaseDate;
    @NotNull
    @Column(name = "COMING_SOON")
    private boolean comingSoon;
    @NotNull
    @Column(name = "SHORT_DESCRIPTION")
    private String shortDescription;
    @NotNull
    @Column(name = "LONG_DESCRIPTION")
    private String longDescription;
    @Embedded
    private Image images;
    @NotNull
    @Column(name = "CHALLENGE_RATING")
    private int challengeRating;
    @Column(name = "AVERAGE_COMPLETION")
    private Double averageCompletion;
    @Column(name = "DIFFICULTY_SPREAD")
    private Double difficultySpread;
    @OneToMany(mappedBy = "game",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private Set<Achievement> achievements = new HashSet<>();
    @OneToMany(mappedBy = "game",
               fetch = FetchType.LAZY)
    private Set<CategorizedGame> categorizedGames = new HashSet<>();

    protected Game() {
    }

    public Game(Long storeId, GameDetailDTO gameDetailDTO) {
        this.storeId = storeId;
        this.steamAppId = gameDetailDTO.getSteamAppId();
        this.title = gameDetailDTO.getTitle();
        this.releaseDate = gameDetailDTO.getReleaseDate();
        this.comingSoon = gameDetailDTO.isComingSoon();
        this.shortDescription = gameDetailDTO.getShortDescription();
        this.longDescription = gameDetailDTO.getLongDescription();

        this.images = new Image(
                gameDetailDTO.getHeaderImageUrl(),
                gameDetailDTO.getCapsuleImageUrl(),
                gameDetailDTO.getCapsuleSmallImageUrl(),
                gameDetailDTO.getBackgroundImageUrl(),
                gameDetailDTO.getBackgroundRawImageUrl()
        );
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setComingSoon(boolean comingSoon) {
        this.comingSoon = comingSoon;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public void setChallengeRating(int challengeRating) {
        this.challengeRating = challengeRating;
    }

    public void setAverageCompletion(Double averageCompletion) {
        this.averageCompletion = averageCompletion;
    }

    public void setDifficultySpread(Double difficultySpread) {
        this.difficultySpread = difficultySpread;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Long getSteamAppId() {
        return steamAppId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public boolean isComingSoon() {
        return comingSoon;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Image getImages() {
        return images;
    }

    public int getChallengeRating() {
        return challengeRating;
    }

    public Double getAverageCompletion() {
        return averageCompletion;
    }

    public Double getDifficultySpread() {
        return difficultySpread;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public Set<CategorizedGame> getCategorizedGames() {
        return categorizedGames;
    }

    public void addAchievement(Achievement achievement) {
        if (achievements == null)
            achievements = new HashSet<>();

        // Achievement MUST be associated with THIS Game
        if (achievement.getGame().equals(this))
            achievements.add(achievement);
    }

    @Override
    public String toString() {
        return "Game{" +
                "storeId=" + storeId +
                ", steamAppId=" + steamAppId +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", comingSoon=" + comingSoon +
                ", shortDescription='" + shortDescription + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", images=" + images +
                ", challengeRating=" + challengeRating +
                ", averageCompletion=" + averageCompletion +
                ", difficultySpread=" + difficultySpread +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(storeId, game.storeId) && Objects.equals(steamAppId, game.steamAppId) && Objects.equals(title, game.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, steamAppId, title);
    }
}
