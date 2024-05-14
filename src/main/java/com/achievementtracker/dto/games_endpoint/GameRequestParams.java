package com.achievementtracker.dto.games_endpoint;

import com.achievementtracker.dao.Page;
import com.achievementtracker.entity.Game;
import com.achievementtracker.entity.Game_;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class GameRequestParams {
    @Pattern(regexp = "^[1-9][0-9]*$", message = "Must be a positive (not zero) integer")
    private String page;
    @Pattern(regexp = "^[1-9][0-9]*$", message = "Must be a positive (not zero) integer")
    private String size;
    @Pattern(regexp = "^[a-zA-Z]+_(asc|desc)$", message = "Must be in format \"(columnName)_(asc|desc)\" ")
    private String sort;
    private String search;
    @Pattern(regexp = "^([1-9][0-9]*)(,[1-9][0-9]*)*$", message = "Categories must be at least 1 positive integer separated by commas, example \"(15,35,23)\" ")
    private String categories;
    @Pattern(regexp = "^[12]$", message = "Must either be 2 (hidden achievements) OR 1 (just achievements)")
    private String achievements;
    @Pattern(regexp = "^[0-9]+$", message = "Must be a positive (not zero) integer or empty")
    private String min_reviews;
    @Pattern(regexp = "^[0-9]+$", message = "Must be a positive (not zero) integer or empty")
    private String max_reviews;
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$", message = "Must be in format \"YYYY-MM\" ")
    private String min_release;
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$", message = "Must be in format \"YYYY-MM\" ")
    private String max_release;

    /* Default Parameter Values */
    private final int DEFAULT_PAGE = 1;
    private final int DEFAULT_PAGE_SIZE = 100;
    private final SingularAttribute<Game, ?> DEFAULT_SORT_ATTRIBUTE = Game_.challengeRating;
    private final Page.SortDirection DEFAULT_SORT_DIRECTION = Page.SortDirection.DESC;
    private final Integer DEFAULT_MIN_REVIEWS = 250;
    private final Integer DEFAULT_MAX_REVIEWS = null;
    private final LocalDate DEFAULT_MIN_RELEASE = null;
    private final LocalDate DEFAULT_MAX_RELEASE = null;

    public GameRequestParams() {
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
        if (page == null)
            return DEFAULT_PAGE;
        return Integer.valueOf(page);
    }

    public String getSize() {
        return size;
    }

    public Integer getSizeAsInt() {
        if (size == null)
            return DEFAULT_PAGE_SIZE;
        return Integer.valueOf(size);
    }

    public String getSort() {
        return sort;
    }

    public SingularAttribute<Game, ?> getSortAttribute() {
        if (sort == null)
            return DEFAULT_SORT_ATTRIBUTE;

        String sortColumn = sort.split("_")[0];
        return switch (sortColumn) {
            case "id" -> Game_.storeId;
            case "name" -> Game_.title;
            case "challenge" -> Game_.challengeRating;
            case "difficulty" -> Game_.difficultySpread;
            case "rating" -> Game_.rating;
            case "release" -> Game_.releaseDate;
            default -> DEFAULT_SORT_ATTRIBUTE;
        };
    }

    public Page.SortDirection getSortDirection() {
        if (sort == null)
            return DEFAULT_SORT_DIRECTION;

        String sortDirection = sort.split("_")[1];
        return sortDirection.equalsIgnoreCase(Page.SortDirection.ASC.name()) ?
                Page.SortDirection.ASC : Page.SortDirection.DESC;
    }

    public String getSearch() {
        return search;
    }

    public String getCategories() {
        return categories;
    }

    public List<Long> getCategoriesAsList() {
        if (categories == null)
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
        if (achievements == null)
            return null;
        return Integer.valueOf(achievements);
    }

    public String getMinReviews() {
        return min_reviews;
    }

    public Integer getMinReviewsAsNullableInt() {
        if (min_reviews == null)
            return DEFAULT_MIN_REVIEWS;
        return Integer.valueOf(min_reviews);
    }

    public String getMaxReviews() {
        return max_reviews;
    }

    public Integer getMaxReviewsAsNullableInt() {
        if (max_reviews == null )
            return DEFAULT_MAX_REVIEWS;
        return Integer.valueOf(max_reviews);
    }

    public String getMinReleaseDate() {
        return min_release;
    }

    public LocalDate getMinReleaseDateAsNullableDate() {
        if (min_release == null)
            return DEFAULT_MIN_RELEASE;
        return LocalDate.parse(min_release + "-01");
    }

    public String getMaxReleaseDate() {
        return max_release;
    }

    public LocalDate getMaxReleaseDateAsNullableDate() {
        if (max_release == null)
            return DEFAULT_MAX_RELEASE;
        return LocalDate.parse(max_release + "-01");
    }

    public int getDEFAULT_PAGE() {
        return DEFAULT_PAGE;
    }

    public int getDEFAULT_PAGE_SIZE() {
        return DEFAULT_PAGE_SIZE;
    }

    public SingularAttribute<Game, ?> getDEFAULT_SORT_ATTRIBUTE() {
        return DEFAULT_SORT_ATTRIBUTE;
    }

    public Page.SortDirection getDEFAULT_SORT_DIRECTION() {
        return DEFAULT_SORT_DIRECTION;
    }

    public Integer getDEFAULT_MIN_REVIEWS() {
        return DEFAULT_MIN_REVIEWS;
    }

    public Integer getDEFAULT_MAX_REVIEWS() {
        return DEFAULT_MAX_REVIEWS;
    }

    public LocalDate getDEFAULT_MIN_RELEASE() {
        return DEFAULT_MIN_RELEASE;
    }

    public LocalDate getDEFAULT_MAX_RELEASE() {
        return DEFAULT_MAX_RELEASE;
    }
}
