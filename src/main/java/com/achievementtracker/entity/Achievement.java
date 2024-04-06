package com.achievementtracker.entity;

import com.achievementtracker.dto.AchievementStatsDTO;
import com.achievementtracker.dto.GameSchemaDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "ACHIEVEMENT")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @NotNull
    @Column(name = "NAME")
    private String name;
    @NotNull
    @Column(name = "DISPLAY_NAME")
    private String displayName;
    @Column(name = "DESCRIPTION")
    private String description;
    @NotNull
    @Column(name = "HIDDEN")
    private boolean hidden;
    @NotNull
    @Column(name = "ICON")
    private String iconURL;
    @NotNull
    @Column(name = "ICON_GRAY")
    private String iconGrayURL;
    @NotNull
    @Column(name = "PERCENTAGE")
    private Double percentage;
    @NotNull
    @Column(name = "POS")
    private int position;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "GAME_ID")
    private Game game;

    protected Achievement() {
    }

    public Achievement(GameSchemaDTO.AchievementDetailsDTO achievementDetailsDTO,
                       AchievementStatsDTO achievementStatsDTO, Game game) {
        this.name = achievementDetailsDTO.getName();
        this.displayName = achievementDetailsDTO.getDisplayName();
        this.description = achievementDetailsDTO.getDescription();
        this.hidden = achievementDetailsDTO.isHidden();
        this.iconURL = achievementDetailsDTO.getIconUrl();
        this.iconGrayURL = achievementDetailsDTO.getIconGrayUrl();
        this.percentage = achievementStatsDTO.getAchievements().get(this.name);

        this.game = game;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getIconGrayURL() {
        return iconGrayURL;
    }

    public Double getPercentage() {
        return percentage;
    }

    public int getPosition() {
        return position;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", hidden=" + hidden +
                ", iconURL='" + iconURL + '\'' +
                ", iconGrayURL='" + iconGrayURL + '\'' +
                ", percentage=" + percentage +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return Objects.equals(name, that.name) && Objects.equals(displayName, that.displayName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, description);
    }
}
