/* ---------------------- Useful Element References ---------------------- */
//region Stats & Progress Bars
const progressBars = document.querySelectorAll("div.circular-progress:not(.circular-progress:has(span#challenge-rating-points))");
//endregion
//region Achievement Tier List
const tierListTierContainers = document.querySelectorAll("div.tier-container");
const tierListAchievementContainers = document.querySelectorAll("div#achievement-tier-table div.achievements-container");
const tierListAchievementIcons = document.querySelectorAll("div#achievement-tier-table img.achievement-icon");
//endregion
//region Achievement Tier List - Carousel
const carouselButtons = document.querySelectorAll("div#achievement-tier-table button.carousel-scroll-button");
//endregion
//region Achievement Tiers - Dropdown
const tierDropdownContainers = document.querySelectorAll("div.tier-dropdown-container");
const tierDropdownInformation = document.querySelectorAll("div.tier-dropdown-container div.tier-information");
const scrollToTopButton = document.getElementById("scroll-to-top");
//endregion
//region Achievement Tiers - Pagination
const showMoreButtons = document.querySelectorAll("button.show-more-button");
const showLessButtons = document.querySelectorAll("button.show-less-button");
//endregion
//region Achievement Tiers - Filters
const searchInput = document.querySelector("input#achievement-search");
const hiddenAchievementsCheckbox = document.querySelector("input[type='checkbox']#hidden-achievements-filter");
//endregion
/* ---------------------- Important Declarations ---------------------- */
//region Stats & Progress Bars
const PROGRESS_INTERVAL = 20; // milliseconds
const TIME_TO_FINISH = 500; // milliseconds
//endregion
//region Achievement Tier List - Carousel
const SCROLL_BEHAVIOR = { SMOOTH: "smooth", INSTANT: "instant" };
let continuousScrollInterval;
let mousedownDurationChecker;
//endregion
//region Achievement Tiers - Dropdown
const DROPDOWN_STATES = { EXPANDED : "expanded", COLLAPSED: "collapsed" };
//endregion
//region Achievement Tiers - Pagination
const ACHIEVEMENT_BATCH_SIZE = 10;
//endregion
//region Achievement Tiers - Filters
let searchTimeout;
//endregion
/* ---------------------- DOMContentLoaded Actions ---------------------- */
replacePlaceholderImages();
initializePointsCounter();
progressBars.forEach(progressBar => initializeProgressBar(progressBar));
tierListAchievementContainers.forEach(container => {
    filterTierListContainer(container);
    updateCarouselButtons(container);
    updateNoAchievementsMessage(container);
});
tierDropdownContainers.forEach(container => {
    highlightRowsMatchingFilters(container);
    setAchievementCountForContainer(container);
});
/* ---------------------- Event Listeners ---------------------- */
//region Achievement Tier List
tierListTierContainers.forEach(container => container.addEventListener("click", function goToTierDropdown() {
    const tier = container.dataset.tier;
    const targetDropdown = document.querySelector(`div.tier-dropdown-container[data-tier=${tier}]`);
    highlightRowsMatchingFilters(targetDropdown);
    expandHighlightedRowsAndCollapseOthers(targetDropdown);
    updatePaginationButtons(targetDropdown);
    setTimeout(() => scrollToElement(targetDropdown, false, SCROLL_BEHAVIOR.INSTANT), 150);
}));
//endregion
//region Achievement Tier List - Carousel
carouselButtons.forEach(button => {
    button.addEventListener("mousedown", (event) => {
        const mousedownTimestamp = performance.now();

        scrollAchievementsContainer(button, 300, SCROLL_BEHAVIOR.SMOOTH); // scroll once
        mousedownDurationChecker = setInterval(function periodicallyCheckDuration() {
            if (performance.now() - mousedownTimestamp >= 400) { // and if the user keeps holding, then scroll continuously
                clearInterval(mousedownDurationChecker); // we no longer need to check
                startContinuousScrolling(event);
            }
        }, 50);
    });
    button.addEventListener("mouseup", () => {
        clearInterval(mousedownDurationChecker); // user no longer holds down click
        stopContinuousScrolling();
    });
    button.addEventListener("mouseleave", () => {
        clearInterval(mousedownDurationChecker); // // user moved his cursor off OR button got disabled
        stopContinuousScrolling();
    });
});
tierListAchievementContainers.forEach(container => container.addEventListener("scroll", () => updateCarouselButtons(container)));
//endregion
//region Achievement Tiers - Dropdown
scrollToTopButton.addEventListener("click", () => {
    const achievementTierList = document.querySelector("#achievement-tier-table");
    scrollToElement(achievementTierList, false, SCROLL_BEHAVIOR.SMOOTH);
});
window.addEventListener("scroll", () => updateScrollToTopButton());
tierListAchievementIcons.forEach(icon => icon.addEventListener("click", function goToAchievementDetails() {
    const targetID = icon.dataset.id;
    const targetContainer = document.querySelector(`div.tier-dropdown-container:has(tr[data-id='${targetID}'])`);
    const targetRow = document.querySelector(`table.achievement-details-table tr[data-id='${targetID}']`);
    highlightRowsMatchingFilters(targetContainer);
    expandHighlightedRowsUpToTarget(targetContainer, targetID);
    updatePaginationButtons(targetContainer);
    if (targetRow.dataset.state === DROPDOWN_STATES.EXPANDED)
        scrollToElement(targetRow, true, SCROLL_BEHAVIOR.INSTANT);   // instantly scroll to element since rows expand without animations
        setTimeout(() => flashRow(targetRow), 100);
}));
tierDropdownInformation.forEach(container => container.addEventListener("click", function toggleDropdownContainer() {
    const tierContainer = container.parentElement;
    if (tierContainer.dataset.state === DROPDOWN_STATES.COLLAPSED) {
        highlightRowsMatchingFilters(tierContainer);
        expandHighlightedRowsAndCollapseOthers(tierContainer);
        updatePaginationButtons(tierContainer);
    }
    else {
        collapseExpandedRows(tierContainer, false);
    }
}));
//endregion
//region Achievement Tiers - Pagination
showMoreButtons.forEach(button => button.addEventListener("click", () => {
    expandHighlightedRowsAndCollapseOthers(button.parentElement);
    updatePaginationButtons(button.parentElement);
}));
showLessButtons.forEach(button => button.addEventListener("click", () => {
    collapseExpandedRows(button.parentElement, true);
    updatePaginationButtons(button.parentElement);
}));
//endregion
//region Achievement Tiers - Filters
searchInput.addEventListener("input", (event) => {
    const searchTerm = event.target.value.trim();
    clearTimeout(searchTimeout);
    const timeoutDuration = (searchTerm === "" ? 0 : 100);

    searchTimeout = setTimeout(() => {
        tierDropdownContainers.forEach(container => {
            highlightRowsMatchingFilters(container);
            setAchievementCountForContainer(container);
            if (searchTerm === "")
                collapseExpandedRows(container, false);
            else  // only if search is active, extend the updated results
                expandHighlightedRowsAndCollapseOthers(container);
            updatePaginationButtons(container);
        });
    }, timeoutDuration);
});
hiddenAchievementsCheckbox.addEventListener("change", () => {
    // First update the tier list
    tierListAchievementContainers.forEach(container => {
       filterTierListContainer(container);
       updateCarouselButtons(container);
       updateNoAchievementsMessage(container);
    });

    // Then the dropdown containers
    const searchIsEmpty = (searchInput.value === "");
    tierDropdownContainers.forEach(container => {
        highlightRowsMatchingFilters(container);
        setAchievementCountForContainer(container);
        if (!searchIsEmpty || container.dataset.state === DROPDOWN_STATES.EXPANDED) {
            // If search is active OR container is already expanded, also expand the updated results
            expandHighlightedRowsAndCollapseOthers(container);
            updatePaginationButtons(container);
        }
    });
});
//endregion
/* ---------------------- Function Declarations ---------------------- */
//region General
function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll("img#game-header ,img.achievement-icon, img.achievement-icon-big, img.game-capsule-small");
    imageElements.forEach(image => {
        const actualSrc = image.dataset.actualSrc;

        const img = new Image();
        img.onload = () => image.setAttribute('src', actualSrc);
        img.src = actualSrc;
    });
}
//endregion
//region Stats & Progress Bars
function initializePointsCounter() {
    const pointsElement = document.getElementById("challenge-rating-points");
    const targetValue = parseInt(pointsElement.dataset.points);

    // ensure it finishes on time
    const increment = (targetValue * PROGRESS_INTERVAL) / TIME_TO_FINISH;

    let counter = 0;
    const progress = setInterval(() => {
        counter = Math.min(counter + increment, targetValue);  // make sure to not step over the final value

        pointsElement.textContent = `${counter.toFixed(0)} points`;  // increment visible counter

        if (counter >= targetValue)
            clearInterval(progress);
    }, PROGRESS_INTERVAL);
}
function initializeProgressBar(bar) {
    const progressValueElement = bar.querySelector(".progress-value");
    let targetValue = parseFloat(progressValueElement.dataset.percent);

    if (isNaN(targetValue)) {
        progressValueElement.textContent = "-";
    } else {
        // ensure it finishes on time
        const increment = (targetValue * PROGRESS_INTERVAL) / TIME_TO_FINISH;

        let counter = 0.0;
        const progress = setInterval(() => {
            counter = Math.min(counter + increment, targetValue);  // make sure to not step over the final value

            progressValueElement.textContent = `${counter.toFixed(1)}%`;  // increment visible percentage

            bar.style.setProperty('--current-angle', `${counter.toFixed(1) * 3.6}deg`);  // increment bar

            if (counter >= targetValue)
                clearInterval(progress);
        }, PROGRESS_INTERVAL);
    }
}
//endregion
//region Achievement Tier List
function updateNoAchievementsMessage(container) {
    const visibleAchievements = Array.from(container.querySelectorAll("img.achievement-icon")).filter(icon => getComputedStyle(icon).display === "block");
    const emptyTierMessage = container.querySelector("span.empty-tier-message");
    if (visibleAchievements.length === 0) {
        container.parentElement.style.justifyContent = "center";
        emptyTierMessage.style.display = "block";
    } else {
        emptyTierMessage.style.display = "none";
        container.parentElement.style.justifyContent = "flex-start";
    }
}
function filterTierListContainer(container) {
    const showHidden = hiddenAchievementsCheckbox.checked;
    const hiddenAchievementIcons = container.querySelectorAll("img.achievement-icon[data-hidden='true']");
    if (showHidden)
        hiddenAchievementIcons.forEach(icon => icon.style.display = "block");
    else
        hiddenAchievementIcons.forEach(icon => icon.style.display = "none");
}
//endregion
//region Achievement Tier List - Carousel
function scrollAchievementsContainer(button, scrollAmount, scrollBehavior) {
    const direction = button.dataset.direction;
    const container = button.parentNode.querySelector("div.achievements-container");

    if (direction === "left")
        container.scrollBy({left: -scrollAmount, behavior: scrollBehavior});
    else
        container.scrollBy({left: scrollAmount, behavior: scrollBehavior});
}
function startContinuousScrolling(event) {
    const button = event.target;
    // basically instant scroll a small amount at very short intervals
    continuousScrollInterval = setInterval(() => {
        scrollAchievementsContainer(button, 10, SCROLL_BEHAVIOR.INSTANT);
    }, 5);
}
function stopContinuousScrolling() {
    clearInterval(continuousScrollInterval);
}
function updateCarouselButtons(container) {
    const scrollLeftButton = container.previousElementSibling;
    const scrollRightButton = container.nextElementSibling;

    scrollLeftButton.disabled = container.scrollLeft === 0;
    scrollRightButton.disabled = container.scrollLeft + container.clientWidth >= container.scrollWidth;
}
//endregion
//region Achievement Tiers - Dropdown
function expandRow(row, animated) {
    if (animated) {
        row.style.display = "table-row";  // First make it visible in the DOM
        row.offsetHeight;  // Trigger re-flow
        row.dataset.state = DROPDOWN_STATES.EXPANDED;  // Finally trigger animation with data-state
    } else {
        row.dataset.state = DROPDOWN_STATES.EXPANDED;  // Set the state first to trigger animation (while not visible in DOM)
        row.style.display = "table-row";  // Then make it visible in the DOM
    }
}
function collapseRow(row, animated) {
    if (animated) {
        row.dataset.state = DROPDOWN_STATES.COLLAPSED;  // Trigger animation with data-state
        setTimeout(() => row.style.display = "none", 150);  // Remove element from DOM after animation finishes
    } else {
        row.style.display = "none";  // Remove element from DOM first
        row.dataset.state = DROPDOWN_STATES.COLLAPSED;  // Then trigger animation
    }
}
function flashRow(row) {
    row.dataset.flash = true;
    setTimeout(() => row.removeAttribute("data-flash"), 300);
}
function setAchievementCountForContainer(tierContainer) {
    // Query for highlighted rows
    const highlightedRowCount = tierContainer.querySelectorAll("table.achievement-details-table tr[data-highlighted='true']").length;
    // Update the textContent of count element
    const countElement = tierContainer.querySelector("span[data-role='count']");
    countElement.textContent = highlightedRowCount;
}
function getExpandedRowsAsArray(tierContainer) {
    const expandedRows = tierContainer.querySelectorAll(`table.achievement-details-table tr[data-state=${DROPDOWN_STATES.EXPANDED}]`);
    return Array.from(expandedRows);
}
function expandHighlightedRowsAndCollapseOthers(tierContainer) {
    const highlightedRows = Array.from(tierContainer.querySelectorAll("tr[data-highlighted='true']"));

    const allRows = tierContainer.querySelectorAll("table.achievement-details-table tr:not(.no-achievements-row)");
    const noResultsRow = tierContainer.querySelector("table.achievement-details-table tr.no-achievements-row");
    if (highlightedRows.length !== 0) {
        collapseRow(noResultsRow, true); // collapse the no achievements row first
        tierContainer.dataset.state = DROPDOWN_STATES.EXPANDED; // then expand dropdown container

        const pendingRowsToExpand = determineRowsToExpand(tierContainer, highlightedRows);  // paginate the highlighted rows
        for (const row of allRows) { // for every row
            if (pendingRowsToExpand.includes(row))
                expandRow(row, true); // expand the highlighted ones that we paginated
            if (!highlightedRows.includes(row))
                collapseRow(row, true); // collapse all that are not highlighted
        }
    } else {
        // Otherwise collapse every row
        allRows.forEach(row => collapseRow(row, true));
        tierContainer.dataset.state = DROPDOWN_STATES.EXPANDED;
        // Except for the no achievements row
        expandRow(noResultsRow, true);
    }
}
function expandHighlightedRowsUpToTarget(targetContainer, targetRowID) {
    const highlightedRows = Array.from(targetContainer.querySelectorAll("tr[data-highlighted='true']"));

    const allRows = targetContainer.querySelectorAll("table.achievement-details-table tr:not(.no-achievements-row)");
    const noResultsRow = targetContainer.querySelector("table.achievement-details-table tr.no-achievements-row");

    if (highlightedRows.length !== 0) {
        collapseRow(noResultsRow, true);  // collapse the no achievements row first
        targetContainer.dataset.state = DROPDOWN_STATES.EXPANDED; // then expand dropdown container

        for (const row of allRows) { // for every row
            if (highlightedRows.includes(row))
                expandRow(row, false); // expand the highlighted ones (no animation because we haven't paginated them)
            else
                collapseRow(row, false); // collapse all that are not highlighted

            if (row.dataset.id === targetRowID)
                break;  // stop after target row is expanded
        }
    } else {
        // Otherwise collapse every row
        allRows.forEach(row => collapseRow(row, false));
        targetContainer.dataset.state = DROPDOWN_STATES.EXPANDED;
        // Except for the no achievements row
        expandRow(noResultsRow, true);
    }
}
function scrollToElement(element, centered, scrollBehavior) {
    const topOffset = element.getBoundingClientRect().top + window.scrollY;
    const windowHeight = window.innerHeight;
    const targetScrollTop = topOffset - (windowHeight / 2);

    window.scrollTo({
        top: centered ? targetScrollTop : topOffset,
        behavior: scrollBehavior
    });
}
function updateScrollToTopButton() {
    const button = document.getElementById("scroll-to-top");
    const breakpointElement = document.querySelector("#achievement-tier-table");

    button.disabled = window.scrollY < breakpointElement.getBoundingClientRect().bottom;
}
//endregion
//region Achievement Tiers - Pagination
function updatePaginationButtons(tierContainer) {
    if (tierContainer.dataset.state === DROPDOWN_STATES.EXPANDED) {
        const currentlyExpanded = getExpandedRowsAsArray(tierContainer).length;
        const totalAchievements = parseInt(tierContainer.querySelector("span[data-role='count']").textContent);

        const showMoreBtn = tierContainer.querySelector("button.show-more-button");
        // If the counter states a bigger number than the one representing currently expanded rows
        showMoreBtn.style.display = totalAchievements > currentlyExpanded ? "flex" : "none";
        const showLessBtn = tierContainer.querySelector("button.show-less-button");
        // If currently expanded rows are more than batch size
        showLessBtn.style.display = currentlyExpanded > ACHIEVEMENT_BATCH_SIZE ? "flex" : "none";
    } else {
        // container is collapsed, so we just hide the buttons
        const showMoreBtn = tierContainer.querySelector("button.show-more-button");
        showMoreBtn.style.display = "none";
        const showLessBtn = tierContainer.querySelector("button.show-less-button");
        showLessBtn.style.display = "none";
    }
}
function determineRowsToExpand(tierContainer, highlightedRows) {
    const currentlyExpandedRows = getExpandedRowsAsArray(tierContainer);
    // Find which are highlighted BUT not yet expanded
    const rowsNotYetExpanded = highlightedRows.filter(row => !currentlyExpandedRows.includes(row));
    // Paginate them according to batch size
    return rowsNotYetExpanded.slice(0, ACHIEVEMENT_BATCH_SIZE);
}
function collapseExpandedRows(tierContainer, paginated) {
    const expandedRows = getExpandedRowsAsArray(tierContainer);
    if (paginated) {
        const currentlyExpanded = expandedRows.length;
        // Collapse as many needed in order for the remaining rows to be divisible by batch size
        let rowsToBeCollapsed;
        const divisibleByBatchSize = (currentlyExpanded % ACHIEVEMENT_BATCH_SIZE === 0);
        if (divisibleByBatchSize) {
            rowsToBeCollapsed = expandedRows.slice(currentlyExpanded - ACHIEVEMENT_BATCH_SIZE, currentlyExpanded);
        } else {
            const remainder = currentlyExpanded % ACHIEVEMENT_BATCH_SIZE;
            rowsToBeCollapsed = expandedRows.slice(currentlyExpanded - remainder, currentlyExpanded);
        }
        // Reverse before collapsing to ensure they collapse from bottom to top
        rowsToBeCollapsed.reverse().forEach(row => collapseRow(row, true));
    } else {
        // Collapse everything
        expandedRows.forEach(row => collapseRow(row, true));
        tierContainer.querySelector("button.show-more-button").style.display = "none";
        tierContainer.querySelector("button.show-less-button").style.display = "none";
        tierContainer.dataset.state = DROPDOWN_STATES.COLLAPSED;
    }
}
//endregion
//region Achievement Tiers - Filters
function highlightRowsMatchingFilters(tierContainer) {
    // Clear previously marked rows
    clearHighlightedRows(tierContainer);
    // Gather filter information
    const includeHidden = hiddenAchievementsCheckbox.checked;
    const searchTerm = searchInput.value;
    if (searchTerm !== "") {
        const regex = new RegExp(searchTerm, 'i');

        const highlightedRows = tierContainer.querySelectorAll(includeHidden ?
            // This is where we filter hidden achievements
            "table.achievement-details-table tr:not(.no-achievements-row)" :
            "table.achievement-details-table tr:not(.no-achievements-row, [data-hidden='true'])");
            // In both cases we exclude the ".no-achievements-row" for obvious reasons

        highlightedRows.forEach(row => {
            const title = row.querySelector(".achievement-title").innerText;
            const description = row.querySelector(".achievement-description").innerText;
            // This is where we filter according to search
            if (regex.test(title) || regex.test(description))
                row.dataset.highlighted = true;
        });
    } else {
        const highlightedRows = tierContainer.querySelectorAll(includeHidden ?
            // This is where we filter hidden achievements
            "table.achievement-details-table tr:not(.no-achievements-row)" :
            "table.achievement-details-table tr:not(.no-achievements-row, [data-hidden='true'])");
            // In both cases we exclude the ".no-achievements-row" for obvious reasons

        // No search filter so everything is highlighted
        highlightedRows.forEach(row => row.dataset.highlighted = true);
    }
}
function clearHighlightedRows(tierContainer) {
    tierContainer.querySelectorAll("table.achievement-details-table tr")
        .forEach(row => row.removeAttribute("data-highlighted"));
}
//endregion