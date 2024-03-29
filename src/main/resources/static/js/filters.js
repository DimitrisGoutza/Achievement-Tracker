const filterForm = document.getElementById("filter-form");

document.addEventListener("DOMContentLoaded", () => {
    // The category ids show their original position - we need this order later
    const categoriesWithOriginalOrder = Array.from(filterForm.querySelectorAll("li.category-item")).sort((a, b) => {
        const idB = parseInt(a.querySelector("input[type='checkbox'].category-checkbox").id.split("-")[1]);
        const idA = parseInt(b.querySelector("input[type='checkbox'].category-checkbox").id.split("-")[1]);
        return idB - idA;
    });
    moveCheckedCategoriesToTop(categoriesWithOriginalOrder);

    const applyButton = filterForm.querySelector("button[type='submit']");
    applyButton.disabled = !formDataHasChanged();


    /* --------------------- Event Listeners --------------------- */
    const categorySearch = document.getElementById("category-search");
    categorySearch.addEventListener("input", (event) => searchCategories(event.target.value));

    const categoryCheckboxes = filterForm.querySelectorAll("input[type='checkbox'].category-checkbox");
    categoryCheckboxes.forEach(checkbox => checkbox.addEventListener("change", () => {
        moveCheckedCategoriesToTop(categoriesWithOriginalOrder);
        applyButton.disabled = !formDataHasChanged();
    }));

    filterForm.addEventListener("submit", () => applyFilters());
    filterForm.addEventListener("reset", () => clearFilters());
});

function clearFilters() {
    filterForm.action = window.location.pathname;
    filterForm.submit();
}

function applyFilters() {
    // Category checkboxes
    const allCategories = filterForm.querySelectorAll("li.category-item");
    const selectedCategories = [];

    allCategories.forEach(category => {
        const checkbox = category.querySelector("input[type='checkbox'].category-checkbox");
        if (checkbox.checked)
            selectedCategories.push(checkbox.value);
    });

    // Form Action Path
    filterForm.action = window.location.pathname;
    // Form Request Parameters
    if (selectedCategories.length !== 0) {
        const categoryParam = createFormParameterElement("categoryid");
        categoryParam.value = selectedCategories.toString();
        filterForm.appendChild(categoryParam);
    }
    // Submit
    filterForm.submit();
}

function createFormParameterElement(name) {
    const parameter = document.createElement("input");
    parameter.setAttribute("type", "hidden");
    parameter.setAttribute("name", name);
    return parameter;
}

function formDataHasChanged() {
    const currentURL = new URL(window.location.href);
    const params = currentURL.searchParams;

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
    } else {
        // Else there weren't any previously selected categories
        const categoryCheckboxes = filterForm.querySelectorAll("input[type='checkbox'].category-checkbox");
        const checkedCheckboxExists = Array.from(categoryCheckboxes).some(checkbox => checkbox.checked);
        if (checkedCheckboxExists)
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