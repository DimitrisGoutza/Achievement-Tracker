package com.achievementtracker.entity;

import com.achievementtracker.dto.AchievementStatsDTO;
import com.achievementtracker.dto.GameSchemaDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "description")
    private String description;
    @Column(name = "hidden")
    private boolean hidden;
    @Column(name = "percentage")
    private Double percentage;
    @Column(name = "icon")
    private String iconURL;
    @Column(name = "icon_gray")
    private String iconGrayURL;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
                          CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "game_id")
    private Game game;

    public Achievement() {
    }

    public Achievement(Integer id, String name, String displayName, String description, boolean hidden, Double percentage, String iconURL, String iconGrayURL) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.hidden = hidden;
        this.percentage = percentage;
        this.iconURL = iconURL;
        this.iconGrayURL = iconGrayURL;
    }

    public Achievement(GameSchemaDTO.AchievementDetailsDTO achievementDetailsDTO,
                       AchievementStatsDTO achievementStatsDTO) {
        this.name = achievementDetailsDTO.getName();
        this.displayName = achievementDetailsDTO.getDisplayName();
        this.description = achievementDetailsDTO.getDescription();
        this.hidden = achievementDetailsDTO.isHidden();
        this.iconURL = achievementDetailsDTO.getIconUrl();
        this.iconGrayURL = achievementDetailsDTO.getIconGrayUrl();

        this.percentage = achievementStatsDTO.getAchievements().get(this.name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getIconGrayURL() {
        return iconGrayURL;
    }

    public void setIconGrayURL(String iconGrayURL) {
        this.iconGrayURL = iconGrayURL;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", hidden=" + hidden +
                ", percentage=" + percentage +
                ", iconURL='" + iconURL + '\'' +
                ", iconGrayURL='" + iconGrayURL + '\'' +
                '}';
    }
}
