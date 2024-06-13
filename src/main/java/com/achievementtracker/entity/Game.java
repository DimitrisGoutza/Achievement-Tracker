package com.achievementtracker.entity;

import com.achievementtracker.dto.steam_api.GameCategoriesAndReviewsDTO;
import com.achievementtracker.dto.steam_api.GameDetailDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.*;

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
    @Column(name = "RATING")
    private Double rating;
    @NotNull
    @Column(name = "REVIEWS")
    private int reviews;
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

    public Game(Long storeId, GameDetailDTO gameDetailDTO, GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO) {
        this.storeId = storeId;
        this.steamAppId = gameDetailDTO.getSteamAppId();
        this.title = gameDetailDTO.getTitle();
        this.releaseDate = gameDetailDTO.getReleaseDate();
        this.comingSoon = gameDetailDTO.isComingSoon();
        this.rating = calculateRating(gameCategoriesAndReviewsDTO);
        this.reviews = gameCategoriesAndReviewsDTO.getPositiveReviews() + gameCategoriesAndReviewsDTO.getNegativeReviews();
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

    public void setRating(GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO) {
        this.rating = calculateRating(gameCategoriesAndReviewsDTO);
    }

    public void setReviews(GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO) {
        this.reviews = gameCategoriesAndReviewsDTO.getPositiveReviews() + gameCategoriesAndReviewsDTO.getNegativeReviews();
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

    public Double getRating() {
        return rating;
    }

    public int getReviews() {
        return reviews;
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

    public List<Achievement> getAchievementsAsList() {
        return achievements.stream()
                .sorted(Comparator.comparingInt(Achievement::getPosition))
                .toList();
    }

    public int getHiddenAchievementCount() {
        List<Achievement> hiddenAchievements = achievements.stream().filter(Achievement::isHidden).toList();
        return hiddenAchievements.size();
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

    private Double calculateRating(GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO) {
        int totalReviews = gameCategoriesAndReviewsDTO.getPositiveReviews() + gameCategoriesAndReviewsDTO.getNegativeReviews();

        if (totalReviews != 0)
            return ((double) gameCategoriesAndReviewsDTO.getPositiveReviews() / totalReviews) * 100.0;
        else
            return null;
    }

    @Override
    public String toString() {
        return "Game{" +
                "storeId=" + storeId +
                ", steamAppId=" + steamAppId +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", comingSoon=" + comingSoon +
                ", rating=" + rating +
                ", reviews=" + reviews +
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
