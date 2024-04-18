const filterForm = document.getElementById("filter-form");
const DEFAULT_MIN_REVIEWS = "1000";
const DEFAULT_MAX_REVIEWS = "";

document.addEventListener("DOMContentLoaded", () => {
    // The category ids show their original position - we need this order later
    const categoriesWithOriginalOrder = Array.from(filterForm.querySelectorAll("li.category-item")).sort((a, b) => {
        const idB = parseInt(a.querySelector("input[type='checkbox'].category-checkbox").id.split("-")[1]);
        const idA = parseInt(b.querySelector("input[type='checkbox'].category-checkbox").id.split("-")[1]);
        return idB - idA;
    });
    toggleHiddenAchievementsSubChoice();
    moveCheckedCategoriesToTop(categoriesWithOriginalOrder);

    const applyButton = document.getElementById("filter-submit-button");
    applyButton.disabled = !formDataHasChanged();


    /* --------------------- Event Listeners --------------------- */
    const achievementsCheckbox = document.getElementById("achievements-checkbox");
    achievementsCheckbox.addEventListener("change", () => {
        toggleHiddenAchievementsSubChoice();
        applyButton.disabled = !formDataHasChanged();
    });

    const hiddenAchievementsCheckbox = document.getElementById("hidden-achievements-checkbox");
    hiddenAchievementsCheckbox.addEventListener("change", () => applyButton.disabled = !formDataHasChanged());

    const categorySearch = document.getElementById("category-search");
    categorySearch.addEventListener("input", (event) => searchCategories(event.target.value));

    const categoryCheckboxes = filterForm.querySelectorAll("input[type='checkbox'].category-checkbox");
    categoryCheckboxes.forEach(checkbox => checkbox.addEventListener("change", () => {
        moveCheckedCategoriesToTop(categoriesWithOriginalOrder);
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

    applyButton.addEventListener("click", () => applyFilters());
    filterForm.addEventListener("reset", () => clearFilters());
});

function clearFilters() {
    const emptyFormURL = new URL(window.location.href);
    emptyFormURL.search = "";
    window.location.href = emptyFormURL.toString();
}

function applyFilters() {
    // Toggle checkboxes
    const achievementsCheckbox = document.getElementById("achievements-checkbox");
    const achievements = achievementsCheckbox.checked;
    let hiddenAchievements = false;
    if (achievements) {
        const hiddenAchievementsCheckbox = document.getElementById("hidden-achievements-checkbox");
        hiddenAchievements = hiddenAchievementsCheckbox.checked;
    }

    // Category checkboxes
    const allCategories = filterForm.querySelectorAll("li.category-item");
    const selectedCategories = [];

    allCategories.forEach(category => {
        const checkbox = category.querySelector("input[type='checkbox'].category-checkbox");
        if (checkbox.checked)
            selectedCategories.push(checkbox.value);
    });

    // Review inputs
    const minReviews = document.getElementById("min-reviews-input").value;
    const maxReviews = document.getElementById("max-reviews-input").value;

    /* Form Action Path */
    filterForm.action = window.location.pathname;

    /* Form Request Parameters */
    // Achievements
    if (achievements) {
        createFormParameter("achievements", achievements);
        // Hidden Achievements
        if (hiddenAchievements)
            createFormParameter("hidden", hiddenAchievements);
    }
    // Categories
    if (selectedCategories.length !== 0)
        createFormParameter("categoryid", selectedCategories.toString());
    // Reviews
    if (minReviews.length !== 0)
        createFormParameter("min_reviews", minReviews);
    if (maxReviews.length !== 0)
        createFormParameter("max_reviews", maxReviews)

    // Submit
    if (filterInputsAreValid())
        filterForm.submit();
}

function createFormParameter(paramName, value) {
    const parameter = document.createElement("input");
    parameter.setAttribute("type", "hidden");
    parameter.setAttribute("name", paramName);
    parameter.value = value;

    const paramElementExists = filterForm.querySelector(`input[type='hidden'][name='${paramName}'][value='${value}']`);
    if (!paramElementExists)
        filterForm.appendChild(parameter);
}

function formDataHasChanged() {
    const currentURL = new URL(window.location.href);
    const params = currentURL.searchParams;
    // Toggle checkboxes
    const achievementsParam = params.get("achievements");
    const hiddenAchievementsParam = params.get("hidden");
    if (achievementsParam) { // If the parameter exists it means the checkbox was previously checked
        const checkboxIsStillChecked = document.getElementById("achievements-checkbox").checked;
        if (!checkboxIsStillChecked)
            return true;
        else { // If the main checkbox has not changed (from previously being checked), maybe the sub-checkbox has
            if (hiddenAchievementsParam) {
                const checkboxIsStillChecked = document.getElementById("hidden-achievements-checkbox").checked;
                if (!checkboxIsStillChecked)
                    return true;
            } else {
                const checkboxIsNowChecked = document.getElementById("hidden-achievements-checkbox").checked;
                if (checkboxIsNowChecked)
                    return true;
            }
        }
    } else { // Else it means the checkbox wasn't previously checked
        const checkboxIsNowChecked = document.getElementById("achievements-checkbox").checked;
        if (checkboxIsNowChecked)
            return true;
    }
    // Category checkboxes
    const categoryIdsParam = params.get("categoryid");
    if (categoryIdsParam) {
        const allCategories = Array.from(filterForm.querySelectorAll("li.category-item"));
        // Get the previously applied categories (their values) through URL parameters
        const previouslySelectedCategoryIDs = categoryIdsParam.split(",");
        // Filter the currently checked categories and only keep their values
        const currentlySelectedCategoryIDs = allCategories.filter(category => category.querySelector("input[type='checkbox'].category-checkbox").checked)
            .map(category => category.querySelector("input[type='checkbox'].category-checkbox").value);
        // Compare the two collections
        if (!arraysContainTheSameItems(previouslySelectedCategoryIDs, currentlySelectedCategoryIDs))
            return true;
    } else { // Else there weren't any previously selected categories
        const categoryCheckboxes = filterForm.querySelectorAll("input[type='checkbox'].category-checkbox");
        const checkedCheckboxExists = Array.from(categoryCheckboxes).some(checkbox => checkbox.checked);
        if (checkedCheckboxExists)
            return true;
    }
    // Review inputs
    const minReviewsParam = params.get("min_reviews");
    const currentMinReviews = document.getElementById("min-reviews-input").value;
    if (minReviewsParam) { // First check if the user previously typed a minimum
        if (minReviewsParam !== currentMinReviews) { // and compare it to the current input
            return true;
        }
    } else if (currentMinReviews !== DEFAULT_MIN_REVIEWS) { // Else compare the current input to the default one
        return true;
    }

    const maxReviewsParam = params.get("max_reviews");
    const currentMaxReviews = document.getElementById("max-reviews-input").value;
    if (maxReviewsParam) { // First check if the user previously typed a maximum
        if (maxReviewsParam !== currentMaxReviews) { // and compare it to the current input
            return true;
        }
    } else if (currentMaxReviews !== DEFAULT_MAX_REVIEWS) { // Else compare the current input to the default one
        return true;
    }

    // ..more filter checks

    return false;
}

function arraysContainTheSameItems(arr1, arr2) {
    const set1 = new Set(arr1);
    const set2 = new Set(arr2);

    return (set1.size === set2.size) && arr1.every(item => set2.has(item));
}

function toggleHiddenAchievementsSubChoice() {
    const achievementsCheckbox = document.getElementById("achievements-checkbox");
    const hiddenAchievementsCheckbox = document.getElementById("hidden-achievements-checkbox");
    const hiddenAchievementsElement = hiddenAchievementsCheckbox.closest("label");

    if (achievementsCheckbox.checked) {
        hiddenAchievementsElement.style.display = "block";
    } else {
        hiddenAchievementsCheckbox.checked = false;
        hiddenAchievementsElement.style.display = "none";
    }
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

function moveCheckedCategoriesToTop(categoriesWithOriginalOrder) {
    const checkedCategories = [];
    // push the checked categories into an Array
    filterForm.querySelectorAll("li.category-item").forEach(category => {
        const checkbox = category.querySelector("input[type='checkbox'].category-checkbox");
        if (checkbox.checked)
            checkedCategories.push(category);
    });
    // filter unchecked categories with their original order
    const uncheckedCategories = categoriesWithOriginalOrder.filter(category => !checkedCategories.includes(category));
    // insert the checked categories first
    const newCategories = [...checkedCategories, ...uncheckedCategories];
    const categoryList = document.getElementById("category-list");
    // clear the previous list
    categoryList.innerHTML = "";
    // re-build it in new order
    newCategories.forEach(category => categoryList.appendChild(category));
}

function filterInputsAreValid() {
    // Reviews
    const minReviewsInput = document.getElementById("min-reviews-input").value;
    const maxReviewsInput = document.getElementById("max-reviews-input").value;
    const pattern = /^\d+$/;
    if (!pattern.test(minReviewsInput) || (maxReviewsInput !== DEFAULT_MAX_REVIEWS && !pattern.test(maxReviewsInput)))
        // TODO : build a function that shows an error modal
        return false;

    const minReviews = parseInt(minReviewsInput);
    const maxReviews = parseInt(maxReviewsInput);
    if (minReviews > maxReviews && maxReviewsInput !== DEFAULT_MAX_REVIEWS)
        // TODO : error modal here as well but with different message
        return false;

    // more checks ..

    return true;
}