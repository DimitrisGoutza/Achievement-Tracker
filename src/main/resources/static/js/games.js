/* ---------------------- Useful Element References ---------------------- */
//region General
const existingTableBody = document.getElementById("table-content");
const existingPaginationFooter = document.getElementById("main-container-footer");
//endregion
//region Pagination
const pageSizeSelect = document.getElementById("page-entries-select");
//endregion
//region Search
const gameSearchInput = document.getElementById("game-search");
//endregion
//region Filters
const filterForm = document.getElementById("filter-form");
const achievementsCheckbox = document.getElementById("achievements-checkbox");
const hiddenAchievementsCheckbox = document.getElementById("hidden-achievements-checkbox");
const categoryCheckboxes = Array.from(filterForm.querySelectorAll("input[type='checkbox'].category-checkbox"));
const minReviewsInput = document.getElementById("min-reviews-input");
const maxReviewsInput = document.getElementById("max-reviews-input");
const minReleaseDateInput = document.getElementById("min-release-date");
const maxReleaseDateInput = document.getElementById("max-release-date");
//endregion

/* ---------------------- Important Declarations ---------------------- */
//region Pagination
let previousSize = parseInt(pageSizeSelect.value);
//endregion
//region Sorting
const SortClasses = ["default","asc","desc"];
const SORT_PARAM_FORMAT = "{column}_{sortDirection}";   // name_desc
//endregion
//region Search
const MIN_SEARCH_CHARACTER_LENGTH = 2;
//endregion
//region Filters
// NULL values convert into empty strings in order be comparable later
const DEFAULT_MIN_REVIEWS = (minReviewsInput.dataset.defaultMin) ?? "";
const DEFAULT_MAX_REVIEWS = (maxReviewsInput.dataset.defaultMax) ?? "";
const DEFAULT_MIN_RELEASE_DATE = (minReleaseDateInput.dataset.defaultMin) ?? "";
const DEFAULT_MAX_RELEASE_DATE = (maxReleaseDateInput.dataset.defaultMax) ?? "";
//endregion

/* ---------------------- DOMContentLoaded Actions ---------------------- */
//region General
replacePlaceholderImages();
//endregion
//region Sorting
setSortStatesAccordingToURL();
//endregion
//region Filters
toggleHiddenAchievementsSubChoice();
moveCheckedCategoriesToTop();
setMaxAndMinDateRanges();

const applyButton = document.getElementById("filter-submit-button");
applyButton.disabled = !formDataHasChanged();
//endregion

/* ---------------------- Event Listeners ---------------------- */
//region Pagination
pageSizeSelect.addEventListener("change", () => changePageSize());
attachEventListenersToPageButtons();
//endregion
//region Sorting
const allHeaders = document.querySelectorAll("th");
allHeaders.forEach(th => th.addEventListener("click", () => sortTable(th)));
//endregion
//region Search
let searchTimeout;
let prevEligibleSearch = "";

gameSearchInput.addEventListener("input", (event) => {
    const searchTerm = event.target.value.trim();

    clearTimeout(searchTimeout);
    const timeoutDuration = (searchTerm === "" ? 0 : 300);
    searchTimeout = setTimeout(() => {
        if (searchIsEligible(searchTerm, prevEligibleSearch)) {
            searchGames(searchTerm);
            prevEligibleSearch = searchTerm;
        }
    }, timeoutDuration);
});
//endregion
//region Filters
achievementsCheckbox.addEventListener("change", () => {
    toggleHiddenAchievementsSubChoice();
    applyButton.disabled = !formDataHasChanged();
});

hiddenAchievementsCheckbox.addEventListener("change", () => applyButton.disabled = !formDataHasChanged());

const categorySearch = document.getElementById("category-search");
categorySearch.addEventListener("input", (event) => searchCategories(event.target.value));

categoryCheckboxes.forEach(checkbox => checkbox.addEventListener("change", () => {
    moveCheckedCategoriesToTop();
    applyButton.disabled = !formDataHasChanged();
}));

const reviewInputs = document.getElementById("reviews-filter").querySelectorAll("input[type='text']");
reviewInputs.forEach(input => {
    input.addEventListener("input", () => applyButton.disabled = !formDataHasChanged());
    input.addEventListener("keydown", (event) => {
        if (event.key === "Enter")
            applyFilters();
    });
});

const releaseDateInputs = document.getElementById("release-date-filter").querySelectorAll("input[type='month']");
releaseDateInputs.forEach(dateInput => dateInput.addEventListener("input", () => {
    setMaxAndMinDateRanges();
    applyButton.disabled = !formDataHasChanged();
}));

applyButton.addEventListener("click", () => applyFilters());
filterForm.addEventListener("reset", () => clearFilters());
//endregion

/* ---------------------- Function Declarations ---------------------- */
//region General
function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll('.game-banner-img, .achievement-icon');
    imageElements.forEach(image => {
        const actualSrc = image.dataset.actualSrc;

        const img = new Image();
        img.onload = () => image.setAttribute('src', actualSrc);
        img.src = actualSrc;
    })
}

function scrollToTop() {
    const table = document.querySelector("#main-container-header");
    const topOffset = table.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({
        top: topOffset,
        behavior: "instant"
    });
}

function arraysContainTheSameItems(arr1, arr2) {
    const set1 = new Set(arr1);
    const set2 = new Set(arr2);

    return (set1.size === set2.size) && arr1.every(item => set2.has(item));
}

function updateTableContent(html) {
    const fragment = document.createRange().createContextualFragment(html);
    // Update table body
    const updatedTableBody = fragment.getElementById("table-content");
    existingTableBody.innerHTML = updatedTableBody.innerHTML;
    // Update pagination footer
    const updatedPaginationFooter = fragment.getElementById("main-container-footer");
    existingPaginationFooter.innerHTML = updatedPaginationFooter.innerHTML;
}
//endregion
//region Pagination
function attachEventListenersToPageButtons() {
    const currentPageNumber = parseInt(document.querySelector("button.selected-page-button").innerText);
    document.getElementById("page-prev-button")
        .addEventListener("click", () => changePageNumber(currentPageNumber - 1));
    document.getElementById("page-next-button")
        .addEventListener("click", () => changePageNumber(currentPageNumber + 1));

    const pageButtons = document.querySelectorAll("button.page-buttons:not(.selected-page-button, #page-prev-button, #page-next-button)");
    pageButtons.forEach(button => {
        const pageNumber = parseInt(button.innerText);
        button.addEventListener("click", () => changePageNumber(pageNumber));
    })
}

function changePageSize() {
    const pageSize = pageSizeSelect.value;
    const searchTerm = gameSearchInput.value;

    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", pageSize);
        if (searchTerm.length >= MIN_SEARCH_CHARACTER_LENGTH)
            params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            const currentPage = parseInt(document.querySelector("div#page-buttons-container").dataset.currentPage);

            updateTableContentForPageSize(html, currentPage !== 1, parseInt(pageSize));
            replacePlaceholderImages();
            attachEventListenersToPageButtons();
        })
        .catch(error => console.error("Error: "+error));
}

function changePageNumber(requestedPageNumber) {
    const pageSize = pageSizeSelect.value;
    const searchTerm = gameSearchInput.value;

    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", pageSize);
        params.set("page", requestedPageNumber);
        if (searchTerm.length >= MIN_SEARCH_CHARACTER_LENGTH)
            params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContent(html);
            replacePlaceholderImages();
            scrollToTop();
            attachEventListenersToPageButtons();
        })
        .catch(error => console.error("Error: "+error));
}

function updateTableContentForPageSize(html, updateAllRows, currentSize) {
    const fragment = document.createRange().createContextualFragment(html);
    const updatedTableBody = fragment.getElementById("table-content");

    if (updateAllRows) {
        // Completely replace table body
        existingTableBody.innerHTML = updatedTableBody.innerHTML;
    } else {
        if (currentSize > previousSize) {
            // Append new rows
            const newTableRows = Array.from(updatedTableBody.querySelectorAll("tr")).slice(previousSize);
            newTableRows.forEach(row => existingTableBody.appendChild(row));
        } else {
            // Remove extra rows
            const rowsToBeRemoved = Array.from(existingTableBody.querySelectorAll("tr")).slice(currentSize).reverse();
            rowsToBeRemoved.forEach(row => row.remove());
        }
    }
    // Update pagination footer
    const updatedPaginationFooter = fragment.getElementById("main-container-footer");
    existingPaginationFooter.innerHTML = updatedPaginationFooter.innerHTML;
    // Finally update the previous size variable
    previousSize = pageSizeSelect.value;
}
//endregion
//region Sorting
function sortTable(targetHeader) {
    const sortColumnName = targetHeader.dataset.columnName;
    const currentSortState = targetHeader.dataset.sorted;
    const requestedSortState = getNextSortState(currentSortState);

    const selectedPageSize = pageSizeSelect.value;
    const searchTerm = gameSearchInput.value;
    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);
        if (searchTerm.length >= MIN_SEARCH_CHARACTER_LENGTH)
            params.set("search", searchTerm);
        if (requestedSortState === SortClasses[0] /* = default */)
            params.delete("sort");
        else
            params.set("sort", SORT_PARAM_FORMAT.replace("{column}", sortColumnName).replace("{sortDirection}", requestedSortState));

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            const currentPageNumber = parseInt(document.querySelector("div#page-buttons-container").dataset.currentPage);
            updateTableContentForSort(html, currentPageNumber !== 1);
            replacePlaceholderImages();
            setSortStates(targetHeader, requestedSortState);
            generateVisibleURL(sortColumnName, requestedSortState);
        })
        .catch(error => console.error("Error: "+error));
}

function updateTableContentForSort(html, updatePaginationFooter) {
    const fragment = document.createRange().createContextualFragment(html);
    // Update table body
    const updatedTableBody = fragment.getElementById("table-content");
    existingTableBody.innerHTML = updatedTableBody.innerHTML;
    if (updatePaginationFooter) {
        // Update pagination footer
        const updatedPaginationFooter = fragment.getElementById("main-container-footer");
        existingPaginationFooter.innerHTML = updatedPaginationFooter.innerHTML;
    }
}

function getNextSortState(currentSortState) {
    /*
    Returns the next element from SortClasses[],
    resets to the start when reaching the end of the Array
    */
    const indexOfCurrentState = SortClasses.indexOf(currentSortState);
    const size = SortClasses.length;
    return SortClasses[(indexOfCurrentState + 1) % size];
}

function setSortStates (targetHeader, requestedSortState) {
    // if statement makes the function still usable whenever we pass the target header as undefined
    if (targetHeader instanceof Element)
        targetHeader.dataset.sorted = requestedSortState;
    // Reset state for the rest of the columns
    const allHeadersExceptTarget = document.querySelectorAll("th");
    allHeadersExceptTarget.forEach(header => {
        if (header !== targetHeader)
            header.dataset.sorted = SortClasses[0];
    });
}

function generateVisibleURL(sortColumnName, requestedSortState) {
    const newVisibleURL = new URL(window.location.href);

    const params = new URLSearchParams(newVisibleURL.searchParams);
    if (requestedSortState === SortClasses[0] /* = default */)
        params.delete("sort");
    else
        params.set("sort", SORT_PARAM_FORMAT.replace("{column}", sortColumnName).replace("{sortDirection}", requestedSortState));

    newVisibleURL.search = params.toString();

    window.history.replaceState({}, "", newVisibleURL);
}

function setSortStatesAccordingToURL() {
    let targetHeader;
    let sortDirection;

    const params = new URLSearchParams(window.location.search);
    const sortParam = params.get("sort");
    if (sortParam) {
        sortDirection = sortParam.split("_")[1];
        const columnName = sortParam.split("_")[0];
        targetHeader = document.querySelector(`th[data-column-name='${columnName}']`);
    }

    setSortStates(targetHeader, sortDirection);
}
//endregion
//region Search
function searchIsEligible(currentSearch, previousSearch) {
    /*
    Search is not eligible for API request if:
        1. current search is the same as the previous
        2. both current and previous searches are below character limit
           (which means there's no need to reset the results, cause it already happened)
    */
    if (previousSearch === currentSearch)
        return false;

    if (currentSearch.length < MIN_SEARCH_CHARACTER_LENGTH && previousSearch.length < MIN_SEARCH_CHARACTER_LENGTH)
        return false;

    // More checks..

    return true;
}

function searchGames(searchTerm) {
    const selectedPageSize = pageSizeSelect.value;
    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);
        params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContent(html);
            replacePlaceholderImages();
            attachEventListenersToPageButtons();
        })
        .catch(error => console.error("Error: "+error));
}
//endregion
//region Filters
function clearFilters() {
    const emptyFormURL = new URL(window.location.href);
    emptyFormURL.search = "";
    window.location.href = emptyFormURL.toString();
}

function applyFilters() {
    // Toggle checkboxes
    const achievements = achievementsCheckbox.checked;
    let hiddenAchievements = false;
    if (achievements) {
        hiddenAchievements = hiddenAchievementsCheckbox.checked;
    }

    // Category checkboxes
    const selectedCategories = [];

    categoryCheckboxes.forEach(checkbox => {
        if (checkbox.checked)
            selectedCategories.push(checkbox.value);
    });

    // Review inputs
    const minReviews = minReviewsInput.value.trim();
    const maxReviews = maxReviewsInput.value.trim();

    // Release Date inputs
    const minReleaseDate = minReleaseDateInput.value;
    const maxReleaseDate = maxReleaseDateInput.value;

    /* Form Action Path */
    filterForm.action = window.location.pathname;

    /* Form Request Parameters */
    // Achievements
    if (achievements) {
        if (hiddenAchievements) // Hidden Achievements
            createFormParameter("achievements", 2);
        else
            createFormParameter("achievements", 1);
    }
    // Categories
    if (selectedCategories.length !== 0)
        createFormParameter("categories", selectedCategories.toString());
    // Reviews
    createFormParameter("min_reviews", minReviews.length !== 0 ? minReviews : 0);
    if (maxReviews.length !== 0)
        createFormParameter("max_reviews", maxReviews)
    // Release Date
    if (minReleaseDate.length !== 0)
        createFormParameter("min_release", minReleaseDate);
    if (maxReleaseDate.length !== 0)
        createFormParameter("max_release", maxReleaseDate);

    /* Submit */
    if (filterInputsAreValid())
        filterForm.submit();
}

function createFormParameter(paramName, value) {
    const parameter = document.createElement("input");
    parameter.setAttribute("type", "hidden");
    parameter.setAttribute("name", paramName);
    parameter.value = value;

    const paramElement = filterForm.querySelector(`input[type='hidden'][name='${paramName}']`);
    if (paramElement)
        paramElement.value = value;
    else
        filterForm.appendChild(parameter);
}

function formDataHasChanged() {
    const currentURL = new URL(window.location.href);
    const params = currentURL.searchParams;
    // Toggle checkboxes
    const achievementsParam = params.get("achievements");
    if (achievementsParam) { // If the parameter exists it means the checkbox was previously checked
        const checkboxIsStillChecked = achievementsCheckbox.checked;
        if (!checkboxIsStillChecked)
            return true;
        else { // If the main checkbox has not changed (from previously being checked), maybe the sub-checkbox has
            if (achievementsParam === "2") {
                const checkboxIsStillChecked = hiddenAchievementsCheckbox.checked;
                if (!checkboxIsStillChecked)
                    return true;
            } else {
                const checkboxIsNowChecked = hiddenAchievementsCheckbox.checked;
                if (checkboxIsNowChecked)
                    return true;
            }
        }
    } else { // Else it means the checkbox wasn't previously checked
        const checkboxIsNowChecked = achievementsCheckbox.checked;
        if (checkboxIsNowChecked)
            return true;
    }
    // Category checkboxes
    const categoriesParam = params.get("categories");
    if (categoriesParam) {
        // Get the previously applied categories (their values) through URL parameters
        const previouslySelectedCategoryIDs = categoriesParam.split(",");
        // Filter the currently checked categories and only keep their values
        const currentlySelectedCategoryIDs = categoryCheckboxes.filter(checkbox => checkbox.checked).map(checkbox => checkbox.value);
        // Compare the two collections
        if (!arraysContainTheSameItems(previouslySelectedCategoryIDs, currentlySelectedCategoryIDs))
            return true;
    } else { // Else there weren't any previously selected categories
        const atLeastOneChecked = Array.from(categoryCheckboxes).some(checkbox => checkbox.checked);
        if (atLeastOneChecked)
            return true;
    }
    // Review inputs
    const minReviewsParam = params.get("min_reviews");
    const currentMinReviews = minReviewsInput.value.trim();
    if (minReviewsParam) { // First check if the user previously typed a minimum
        if (minReviewsParam !== currentMinReviews) // and compare it to the current input
            return true;
    } else if (currentMinReviews !== DEFAULT_MIN_REVIEWS) { // Else compare the current input to the default one
        return true;
    }

    const maxReviewsParam = params.get("max_reviews");
    const currentMaxReviews = maxReviewsInput.value.trim();
    if (maxReviewsParam) { // First check if the user previously typed a maximum
        if (maxReviewsParam !== currentMaxReviews) // and compare it to the current input
            return true;
    } else if (currentMaxReviews !== DEFAULT_MAX_REVIEWS) { // Else compare the current input to the default one
        return true;
    }
    // Release Date inputs
    const minReleaseParam = params.get("min_release");
    const currentMinRelease = minReleaseDateInput.value;
    if (minReleaseParam) { // First check if the user previously typed a minimum
        if (minReleaseParam !== currentMinRelease) // and compare it to the current input
            return true
    } else if (currentMinRelease !== DEFAULT_MIN_RELEASE_DATE) { // Else compare the current input to the default one
        return true;
    }

    const maxReleaseParam = params.get("max_release");
    const currentMaxRelease = maxReleaseDateInput.value;
    if (maxReleaseParam) { // First check if the user previously typed a minimum
        if (maxReleaseParam !== currentMaxRelease) // and compare it to the current input
            return true
    } else if (currentMaxRelease !== DEFAULT_MAX_RELEASE_DATE) { // Else compare the current input to the default one
        return true;
    }

    // ..more filter checks

    return false;
}

function toggleHiddenAchievementsSubChoice() {
    const hiddenAchievementsElement = hiddenAchievementsCheckbox.closest("label");

    if (achievementsCheckbox.checked) {
        hiddenAchievementsElement.style.display = "block";
        drawLineConnectingCheckboxes();
    } else {
        hiddenAchievementsCheckbox.checked = false;
        hiddenAchievementsElement.style.display = "none";
        eraseLineConnectingCheckboxes();
    }
}

function drawLineConnectingCheckboxes() {
    // TODO: build line connecting checkboxes (later)
}

function eraseLineConnectingCheckboxes() {
}

function searchCategories(input) {
    const allCategories = filterForm.querySelectorAll("li.category-item");
    const regex = new RegExp(input, 'i');

    allCategories.forEach(category => {
        const categoryName = category.querySelector("label.category-name").textContent;
        if (regex.test(categoryName)) {
            category.style.display = "";
        } else {
            category.style.display = "none";
        }
    });
}

function moveCheckedCategoriesToTop() {
    const categoriesSortedByPos = Array.from(filterForm.querySelectorAll("li.category-item")).sort((a, b) => {
        const posB = parseInt(a.dataset.pos);
        const posA = parseInt(b.dataset.pos);
        return posB - posA;
    });
    const checkedCategories = [];
    // push the checked category items into an Array
    categoryCheckboxes.forEach(checkbox => {
        if (checkbox.checked)
            checkedCategories.push(checkbox.closest("li.category-item"));
    });
    // filter unchecked categories (with the help of categoriesSortedByPos, to keep their original order intact)
    const uncheckedCategories = categoriesSortedByPos.filter(category => !checkedCategories.includes(category));
    // insert the checked categories first
    const newCategories = [...checkedCategories, ...uncheckedCategories];
    const categoryList = document.getElementById("category-list");
    // clear the previous list
    categoryList.innerHTML = "";
    // re-build it in new order
    newCategories.forEach(category => categoryList.appendChild(category));
}

function setMaxAndMinDateRanges() {
    minReleaseDateInput.setAttribute("max", maxReleaseDateInput.value ? maxReleaseDateInput.value : maxReleaseDateInput.getAttribute("max"));
    maxReleaseDateInput.setAttribute("min", minReleaseDateInput.value ? minReleaseDateInput.value : minReleaseDateInput.getAttribute("min"));
}

function filterInputsAreValid() {
    // TODO : build a function that shows an error modal with a different message depending on the validation error
    /* Reviews */
    const minReviewsValue = minReviewsInput.value.trim();
    const maxReviewsValue = maxReviewsInput.value.trim();
    const reviewInputPattern = /^\d+$/; // \d+ matches one or more digits (0-9)

    // Check 1 : Pass the regex test while not being empty (cause empty means no limit)
    if (minReviewsValue.length !== 0 && !reviewInputPattern.test(minReviewsValue))
        return false;
    if (maxReviewsValue.length !== 0 && !reviewInputPattern.test(maxReviewsValue))
        return false;
    // Check 2 : If both of them are populated, minimum should be smaller than or equal to maximum
    if (maxReviewsValue.length !==0 && minReviewsValue.length !==0) {
        const minReviews = parseInt(minReviewsValue);
        const maxReviews = parseInt(maxReviewsValue);
        if (minReviews > maxReviews)
            return false;
    }

    /* Release Dates */
    const minReleaseDate = minReleaseDateInput.value;
    const maxReleaseDate = maxReleaseDateInput.value;
    const datePattern = /^\d{4}-(0[1-9]|1[012])$/; // \d{4} matches exactly four digits which represent the year, (0[1-9]|1[0-2]) matches a two-digit month from 01 to 12

    // Check 1 : Pass the regex test while not being empty (cause empty means no limit)
    if (minReleaseDate.length !==0 && !datePattern.test(minReleaseDate))
        return false;
    if (maxReleaseDate.length !==0 && !datePattern.test(maxReleaseDate))
        return false;

    // Check 2 : If both of them are populated, max date should be ahead or equal to min date
    if (minReleaseDate.length !== 0 && maxReleaseDate.length !== 0) {
        const minDate = new Date(minReleaseDate + "-01");
        const maxDate = new Date(maxReleaseDate + "-01");
        if (maxDate < minDate)
            return false;
    }

    // more checks ..

    return true;
}
//endregion