package com.achievementtracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "GAME_CATEGORY")
public class CategorizedGame {
    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "CATEGORY_ID")
        protected Long categoryId;
        @Column(name = "GAME_ID")
        protected Long gameId;

        public Id() {
        }

        public Id(Long categoryId, Long gameId) {
            this.categoryId = categoryId;
            this.gameId = gameId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(categoryId, id.categoryId) && Objects.equals(gameId, id.gameId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryId, gameId);
        }
    }
    @EmbeddedId
    private Id id = new Id();
    @NotNull
    @Column(name = "VOTES")
    private Integer votes;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID",
                insertable = false, updatable = false)
    private Category category;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GAME_ID",
                insertable = false, updatable = false)
    private Game game;

    protected CategorizedGame() {}

    public CategorizedGame(Integer votes, Category category, Game game) {
        this.votes = votes;
        this.category = category;
        this.game = game;

        this.id.categoryId = category.getId();
        this.id.gameId = game.getId();

        category.getCategorizedGames().add(this);
        game.getCategorizedGames().add(this);
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Id getId() {
        return id;
    }

    public Integer getVotes() {
        return votes;
    }

    public Category getCategory() {
        return category;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "CategorizedGame{" +
                "id=" + id +
                ", votes=" + votes +
                '}';
    }
}
