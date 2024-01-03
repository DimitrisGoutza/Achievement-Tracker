package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Achievement {
    @JsonProperty("name")
    private String name;
    @JsonProperty("percent")
    private double percentage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "name='" + name + '\'' +
                ", percentage=" + percentage +
                '}';
    }
}
