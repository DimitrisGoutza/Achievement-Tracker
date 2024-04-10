package com.achievementtracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CATEGORY")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @NotNull
    @Column(name = "NAME", unique = true)
    private String name;
    @NotNull
    @Column(name = "POPULARITY")
    private Integer popularity = 1;
    @NotNull
    @OneToMany(mappedBy = "category",
               fetch = FetchType.LAZY)
    private Set<CategorizedGame> categorizedGames = new HashSet<>();

    /* UI Attributes below */
    @Transient
    boolean available = false;

    protected Category() {}

    public Category(String categoryName) {
        this.name = categoryName;
    }

    public void increasePopularity() {
        this.popularity++;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public Set<CategorizedGame> getCategorizedGames() {
        return categorizedGames;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
