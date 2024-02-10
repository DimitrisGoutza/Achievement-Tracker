package com.achievementtracker.entity;

import com.achievementtracker.dto.GameCategoriesDTO;
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
    @Column(name = "TOTAL_VOTES")
    private Integer totalVotes;
    @NotNull
    @OneToMany(mappedBy = "category",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private Set<CategorizedGame> categorizedGames = new HashSet<>();

    protected Category() {}

    public Category(GameCategoriesDTO.CategoryDetailsDTO categoryDetailsDTO) {
        this.name = categoryDetailsDTO.getName();
        this.totalVotes = categoryDetailsDTO.getVotes();
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public Set<CategorizedGame> getCategorizedGames() {
        return categorizedGames;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalVotes=" + totalVotes +
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
