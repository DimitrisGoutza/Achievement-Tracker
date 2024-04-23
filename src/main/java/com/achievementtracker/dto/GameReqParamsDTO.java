package com.achievementtracker.dto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class GameReqParamsDTO {
    private String page;
    private String size;
    private String sort;
    private String search;
    private String categories;
    private String achievements;
    private String min_reviews;
    private String max_reviews;
    private String min_release;
    private String max_release;

    /* Default Parameter Values */
    private final String DEFAULT_PAGE = "1";
    private final String DEFAULT_PAGE_SIZE = "100";
    private final String DEFAULT_MIN_REVIEWS = "250";
    private final String DEFAULT_MAX_REVIEWS = "";
    private final String DEFAULT_MIN_RELEASE = "";
    private final String DEFAULT_MAX_RELEASE = "";

    public GameReqParamsDTO() {
        /* Default Param Values */
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_PAGE_SIZE;
        this.search = "";
        this.categories = "";
        this.sort = "challenge_desc";
        this.min_reviews = DEFAULT_MIN_REVIEWS;
        this.max_reviews = DEFAULT_MAX_REVIEWS;
        this.min_release = DEFAULT_MIN_RELEASE;
        this.max_release = DEFAULT_MAX_RELEASE;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public void setMin_reviews(String min_reviews) {
        this.min_reviews = min_reviews;
    }

    public void setMax_reviews(String max_reviews) {
        this.max_reviews = max_reviews;
    }

    public void setMin_release(String min_release) {
        this.min_release = min_release;
    }

    public void setMax_release(String max_release) {
        this.max_release = max_release;
    }

    public String getPage() {
        return page;
    }

    public Integer getPageAsInt() {
        return Integer.valueOf(page);
    }

    public String getSize() {
        return size;
    }

    public Integer getSizeAsInt() {
        return Integer.valueOf(size);
    }

    public String getSort() {
        return sort;
    }

    public String getSearch() {
        return search;
    }

    public String getCategories() {
        return categories;
    }

    public List<Long> getCategoriesAsList() {
        if (categories.isEmpty())
            return List.of();
        if (categories.contains(","))
            return Arrays.stream(categories.split(","))
                    .map(Long::valueOf).toList();
        else
            return List.of(Long.valueOf(categories));
    }

    public String getAchievements() {
        return achievements;
    }

    public Integer getAchievementsAsNullableInt() {
        if (achievements == null || achievements.isEmpty())
            return null;
        return Integer.valueOf(achievements);
    }

    public String getMinReviews() {
        return min_reviews;
    }

    public Integer getMinReviewsAsNullableInt() {
        if (min_reviews == null || min_reviews.isEmpty())
            return null;
        return Integer.valueOf(min_reviews);
    }

    public String getMaxReviews() {
        return max_reviews;
    }

    public Integer getMaxReviewsAsNullableInt() {
        if (max_reviews == null || max_reviews.isEmpty())
            return null;
        return Integer.valueOf(max_reviews);
    }

    public String getMinReleaseDate() {
        return min_release;
    }

    public LocalDate getMinReleaseDateAsNullableDate() {
        if (min_release == null || min_release.isEmpty())
            return null;
        return LocalDate.parse(min_release + "-01");
    }

    public String getMaxReleaseDate() {
        return max_release;
    }

    public LocalDate getMaxReleaseDateAsNullableDate() {
        if (max_release == null || max_release.isEmpty())
            return null;
        return LocalDate.parse(max_release + "-01");
    }

    public String getDEFAULT_PAGE() {
        return DEFAULT_PAGE;
    }

    public String getDEFAULT_PAGE_SIZE() {
        return DEFAULT_PAGE_SIZE;
    }

    public String getDEFAULT_MIN_REVIEWS() {
        return DEFAULT_MIN_REVIEWS;
    }

    public String getDEFAULT_MAX_REVIEWS() {
        return DEFAULT_MAX_REVIEWS;
    }

    public String getDEFAULT_MIN_RELEASE() {
        return DEFAULT_MIN_RELEASE;
    }

    public String getDEFAULT_MAX_RELEASE() {
        return DEFAULT_MAX_RELEASE;
    }
}
