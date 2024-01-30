package com.achievementtracker.entity;

import com.achievementtracker.dto.GameDetailDTO;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "game")
public class Game {
    @Id
    @Column(name = "id")
    // We make use of the one returned from the Steam API (appid), no need for auto generation
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @Column(name = "coming_soon")
    private boolean comingSoon;
    @Column(name = "score")
    private Double score;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_detail_id")
    private GameDetail gameDetail;
    @OneToMany(mappedBy = "game",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Achievement> achievements;

    public Game() {
        achievements = new LinkedList<>();
    }

    public Game(Integer id, String title, LocalDate releaseDate, boolean comingSoon, Double score) {
        achievements = new LinkedList<>();

        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.comingSoon = comingSoon;
        this.score = score;
    }

    public Game(GameDetailDTO gameDetailDTO, Double score) {
        achievements = new LinkedList<>();

        this.id = gameDetailDTO.getId();
        this.title = gameDetailDTO.getName();
        this.releaseDate = gameDetailDTO.getReleaseDate();
        this.comingSoon = gameDetailDTO.isComingSoon();
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isComingSoon() {
        return comingSoon;
    }

    public void setComingSoon(boolean comingSoon) {
        this.comingSoon = comingSoon;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public GameDetail getGameDetail() {
        return gameDetail;
    }

    public void setGameDetail(GameDetail gameDetail) {
        this.gameDetail = gameDetail;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public void addAchievement(Achievement achievement) {
        achievement.setGame(this);
        achievements.add(achievement);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", comingSoon=" + comingSoon +
                ", score=" + score +
                '}';
    }
}
