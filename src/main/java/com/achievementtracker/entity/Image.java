package com.achievementtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Image {
    @NotNull
    @Column(name = "HEADER_IMAGE")
    private String headerImageURL;
    @NotNull
    @Column(name = "CAPSULE_IMAGE")
    private String capsuleImageURL;
    @NotNull
    @Column(name = "CAPSULE_SMALL_IMAGE")
    private String capsuleSmallImageURL;
    @NotNull
    @Column(name = "BACKGROUND_IMAGE")
    private String backgroundImageURL;
    @NotNull
    @Column(name = "BACKGROUND_RAW_IMAGE")
    private String backgroundRawImageURL;

    protected Image() {
    }

    public Image(String headerImageURL, String capsuleImageURL, String capsuleSmallImageURL, String backgroundImageURL, String backgroundRawImageURL) {
        this.headerImageURL = headerImageURL;
        this.capsuleImageURL = capsuleImageURL;
        this.capsuleSmallImageURL = capsuleSmallImageURL;
        this.backgroundImageURL = backgroundImageURL;
        this.backgroundRawImageURL = backgroundRawImageURL;
    }

    public String getHeaderImageURL() {
        return headerImageURL;
    }

    public String getCapsuleImageURL() {
        return capsuleImageURL;
    }

    public String getCapsuleSmallImageURL() {
        return capsuleSmallImageURL;
    }

    public String getBackgroundImageURL() {
        return backgroundImageURL;
    }

    public String getBackgroundRawImageURL() {
        return backgroundRawImageURL;
    }

    @Override
    public String toString() {
        return "Image{" +
                "headerImageURL='" + headerImageURL + '\'' +
                ", capsuleImageURL='" + capsuleImageURL + '\'' +
                ", capsuleSmallImageURL='" + capsuleSmallImageURL + '\'' +
                ", backgroundImageURL='" + backgroundImageURL + '\'' +
                ", backgroundRawImageURL='" + backgroundRawImageURL + '\'' +
                '}';
    }
}
