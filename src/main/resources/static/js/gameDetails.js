/* ---------------------- Useful Element References ---------------------- */
const achievementTierTable = document.getElementById("achievement-tier-table");
const achievementContainers = achievementTierTable.querySelectorAll("div.achievements-container");
//region Carousel
const carouselButtons = achievementTierTable.querySelectorAll("button.carousel-scroll-button");
//endregion
//region Achievement Tiers - Dropdown
const tierDropdownContainers = document.querySelectorAll("div.tier-dropdown-container");
//endregion
//region Achievement Tiers - Filters
const searchInput = document.querySelector("input#achievement-search");
const hiddenAchievementsCheckbox = document.querySelector("input[type='checkbox']#hidden-achievements-filter");
//endregion
/* ---------------------- Important Declarations ---------------------- */
//region Progress Bar
const PROGRESS_INTERVAL = 20; // milliseconds
const TIME_TO_FINISH = 500; // milliseconds
//endregion
//region Carousel
const ScrollBehavior = { SMOOTH: "smooth", INSTANT: "instant" };
let continuousScrollInterval;
let mousedownDurationInterval;
//endregion
//region Achievement Tiers - Dropdown
const DropdownStates = { EXPANDED : "expanded", COLLAPSED: "collapsed" };
//endregion
//region Achievement Tiers - Pagination
const ACHIEVEMENT_BATCH_SIZE = 10;
//endregion
//region Achievement Tiers - Filters
let searchTimeout;
//endregion
/* ---------------------- DOMContentLoaded Actions ---------------------- */
//region General
replacePlaceholderImages();
showMessageForEmptyTiers();
initializePointsCounter();
//endregion
//region Progress Bar
document.querySelectorAll("div.circular-progress:not(.circular-progress:has(span#challenge-rating-points))").forEach(progressBar => initializeProgressBar(progressBar));
//endregion
//region Carousel
makeCarouselScrollButtonsVisible();
disableOrEnableCarouselButtons();
//endregion
//region Achievement Tiers - Dropdown
tierDropdownContainers.forEach(container => {
    highlightRowsMatchingFilters(container);
    setAchievementCountForContainer(container);
});
colorTableRowsAccordingToPercentage();
//endregion
/* ---------------------- Event Listeners ---------------------- */
//region Carousel
carouselButtons.forEach(button => {
    button.addEventListener("mousedown", (event) => {
        const mousedownTimestamp = performance.now();

        scrollAchievementsContainer(button, 300, ScrollBehavior.SMOOTH);
        mousedownDurationInterval = setInterval(function periodicallyCheckDuration() {
            if (performance.now() - mousedownTimestamp >= 400) {
                clearInterval(mousedownDurationInterval); // we no longer need to check
                startContinuousScrolling(event);
            }
        }, 50);
    });
    button.addEventListener("mouseup", () => {
        clearInterval(mousedownDurationInterval); // user no longer holds down click
        stopContinuousScrolling();
    });
    button.addEventListener("mouseleave", () => {
        clearInterval(mousedownDurationInterval);
        stopContinuousScrolling();
    });
});
achievementContainers.forEach(container => container.addEventListener("scroll", () => disableOrEnableCarouselButtons()));
//endregion
//region Achievement Tiers - Dropdown
document.querySelectorAll("div.tier-information").forEach(container => container.addEventListener("click", function toggleDropdownContainer() {
    const tierContainer = container.parentElement;
    if (tierContainer.dataset.state === DropdownStates.COLLAPSED) {
        highlightRowsMatchingFilters(tierContainer);
        expandHighlightedRowsAndCollapseOthers(tierContainer);
        showOrHidePaginationButtons(tierContainer);
    }
    else {
        collapseExpandedRows(tierContainer, false);
    }
}));
//endregion
//region Achievement Tiers - Pagination
document.querySelectorAll("button.show-more-button").forEach(button => button.addEventListener("click", () => {
    expandHighlightedRowsAndCollapseOthers(button.parentElement);
    showOrHidePaginationButtons(button.parentElement);
}));
document.querySelectorAll("button.show-less-button").forEach(button => button.addEventListener("click", () => {
    collapseExpandedRows(button.parentElement, true);
    showOrHidePaginationButtons(button.parentElement);
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
            else
                expandHighlightedRowsAndCollapseOthers(container);
            showOrHidePaginationButtons(container);
        });
    }, timeoutDuration);
});
hiddenAchievementsCheckbox.addEventListener("change", () => {
    const searchIsEmpty = (searchInput.value === "");
    tierDropdownContainers.forEach(container => {
        highlightRowsMatchingFilters(container);
        setAchievementCountForContainer(container);
        if (!searchIsEmpty || container.dataset.state === DropdownStates.EXPANDED) {
            expandHighlightedRowsAndCollapseOthers(container);
            showOrHidePaginationButtons(container);
        }
    });
});
//endregion
/* ---------------------- Function Declarations ---------------------- */
//region General
function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll("img#game-header ,img.achievement-icon, img.achievement-icon-big");
    imageElements.forEach(image => {
        const actualSrc = image.dataset.actualSrc;

        const img = new Image();
        img.onload = () => image.setAttribute('src', actualSrc);
        img.src = actualSrc;
    })
}
function showMessageForEmptyTiers() {
    const achievementContainers = achievementTierTable.querySelectorAll(".achievements-container");
    achievementContainers.forEach(container => {
        const achievements = container.querySelectorAll("img.achievement-icon");
        if (achievements.length === 0) {
            container.querySelector("span.empty-tier-message").style.display = "block";
            container.parentElement.style.justifyContent = "center";
        }
    })
}
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
//endregion
//region Progress Bar
function initializeProgressBar(bar) {
    const progressValueElement = bar.querySelector(".progress-value");
    const targetValue = parseFloat(progressValueElement.dataset.percent);

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
//endregion
//region Carousel
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
    // basically repeat instant scrolling very often
    continuousScrollInterval = setInterval(() => {
        scrollAchievementsContainer(button, 10, ScrollBehavior.INSTANT);
    }, 5);
}
function stopContinuousScrolling() {
    clearInterval(continuousScrollInterval);
}
function makeCarouselScrollButtonsVisible() {
    carouselButtons.forEach(button => {
        const container = button.parentNode.querySelector("div.achievements-container");
        const isOverflowing = container.scrollWidth > container.clientWidth;
        if (isOverflowing) {
            // Left Button
            container.previousElementSibling.style.display = "block";
            // Right Button
            container.nextElementSibling.style.display = "block";
        }
    })
}
function disableOrEnableCarouselButtons() {
    achievementContainers.forEach(container => {
        const scrollLeftButton = container.previousElementSibling;
        const scrollRightButton = container.nextElementSibling;
        scrollLeftButton.disabled = container.scrollLeft === 0;
        scrollRightButton.disabled = container.scrollLeft + container.clientWidth >= container.scrollWidth;
    });
}
//endregion
//region Achievement Tiers - Dropdown
function expandRow(row) {
    row.style.display = "table-row";
    row.offsetHeight;
    row.dataset.state = DropdownStates.EXPANDED;
}
function collapseRow(row) {
    row.dataset.state = DropdownStates.COLLAPSED;
    setTimeout(() => row.style.display = "none", 150);
}
function colorTableRowsAccordingToPercentage() {
    document.querySelectorAll("table.achievement-details-table tr:not(tr.no-achievements-row)").forEach(row => {
        const percentage = parseFloat(row.dataset.percentage);
        row.style.setProperty('--achievement-percentage', `${percentage}%`);
    });
}
function setAchievementCountForContainer(tierContainer) {
    const relatedTable = tierContainer.querySelector("table.achievement-details-table");

    const highlightedRowCount = relatedTable.querySelectorAll("tr[data-highlighted='true']").length;

    const countElement = tierContainer.querySelector("span[data-role='count']");
    countElement.textContent = highlightedRowCount;
}
//endregion
//region Achievement Tiers - Pagination
function getExpandedRowsAsArray(tierContainer) {
    const expandedRows = tierContainer.querySelectorAll(`table.achievement-details-table tr[data-state=${DropdownStates.EXPANDED}]`);
    return Array.from(expandedRows);
}
function showOrHidePaginationButtons(tierContainer) {
    if (tierContainer.dataset.state === DropdownStates.EXPANDED) {
        const currentlyExpanded = getExpandedRowsAsArray(tierContainer).length;
        const totalAchievements = parseInt(tierContainer.querySelector("span[data-role='count']").textContent);

        const showMoreBtn = tierContainer.querySelector("button.show-more-button");
        showMoreBtn.style.display = totalAchievements > currentlyExpanded ? "flex" : "none";
        const showLessBtn = tierContainer.querySelector("button.show-less-button");
        showLessBtn.style.display = currentlyExpanded > ACHIEVEMENT_BATCH_SIZE ? "flex" : "none";
    } else {
        // container is collapsed, so we also hide the buttons
        const showMoreBtn = tierContainer.querySelector("button.show-more-button");
        showMoreBtn.style.display = "none";
        const showLessBtn = tierContainer.querySelector("button.show-less-button");
        showLessBtn.style.display = "none";
    }
}
function expandHighlightedRowsAndCollapseOthers(tierContainer) {
    const highlightedRows = Array.from(tierContainer.querySelectorAll("tr[data-highlighted='true']"));

    const allRows = tierContainer.querySelectorAll("table.achievement-details-table tr:not(.no-achievements-row)");
    const noResultsRow = tierContainer.querySelector("table.achievement-details-table tr.no-achievements-row");
    if (highlightedRows.length !== 0) {
        collapseRow(noResultsRow);
        tierContainer.dataset.state = DropdownStates.EXPANDED;

        const pendingRowsToExpand = determineRowsToExpand(tierContainer, highlightedRows);
        allRows.forEach(row => {
            if (pendingRowsToExpand.includes(row))
                expandRow(row);
            if (!highlightedRows.includes(row))
                collapseRow(row);
        });
    } else {
        allRows.forEach(row => collapseRow(row));
        tierContainer.dataset.state = DropdownStates.EXPANDED;
        expandRow(noResultsRow);
    }
}
function determineRowsToExpand(tierContainer, highlightedRows) {
    const currentlyExpandedRows = getExpandedRowsAsArray(tierContainer);
    const rowsNotYetExpanded = highlightedRows.filter(row => !currentlyExpandedRows.includes(row));

    return rowsNotYetExpanded.slice(0, ACHIEVEMENT_BATCH_SIZE);
}
function collapseExpandedRows(tierContainer, paginated) {
    const expandedRows = getExpandedRowsAsArray(tierContainer);
    if (paginated === true) {
        const currentlyExpanded = expandedRows.length;
        // Collapse as many needed in order for the remaining rows to be divisible by batch size
        let rowsToBeCollapsed = [];
        const divisibleByBatchSize = (currentlyExpanded % ACHIEVEMENT_BATCH_SIZE === 0);
        if (divisibleByBatchSize) {
            rowsToBeCollapsed = expandedRows.slice(currentlyExpanded - ACHIEVEMENT_BATCH_SIZE, currentlyExpanded);
        } else {
            const remainder = currentlyExpanded % ACHIEVEMENT_BATCH_SIZE;
            rowsToBeCollapsed = expandedRows.slice(currentlyExpanded - remainder, currentlyExpanded);
        }
        // Reverse before collapsing to ensure they collapse from bottom to top
        rowsToBeCollapsed.reverse().forEach(row => collapseRow(row));
    } else {
        // Collapse everything
        expandedRows.forEach(row => collapseRow(row));
        tierContainer.querySelector("button.show-more-button").style.display = "none";
        tierContainer.querySelector("button.show-less-button").style.display = "none";
        tierContainer.dataset.state = DropdownStates.COLLAPSED;
    }
}
//endregion
//region Achievement Tiers - Filters
function highlightRowsMatchingFilters(tierContainer) {
    // clear previously marked rows
    clearHighlightedRows(tierContainer);

    const includeHidden = hiddenAchievementsCheckbox.checked;
    const searchTerm = searchInput.value;
    if (searchTerm !== "") {
        const regex = new RegExp(searchTerm, 'i');

        const highlightedRows = tierContainer.querySelectorAll(includeHidden ?
            "table.achievement-details-table tr:not(.no-achievements-row)" :    // include rows with hidden achievements
            "table.achievement-details-table tr:not(.no-achievements-row, [data-hidden='true'])");  // exclude rows with hidden achievements
        // In both cases we exclude the ".no-achievements-row" for obvious reasons
        highlightedRows.forEach(row => {
            const title = row.querySelector(".achievement-title").innerText;
            const description = row.querySelector(".achievement-description").innerText;
            // Check against search term
            if (regex.test(title) || regex.test(description))
                row.dataset.highlighted = true;
        });
    } else {
        const highlightedRows = tierContainer.querySelectorAll(includeHidden ?
            "table.achievement-details-table tr:not(.no-achievements-row)" :    // include rows with hidden achievements
            "table.achievement-details-table tr:not(.no-achievements-row, [data-hidden='true'])");  // exclude rows with hidden achievements
        // In both cases we exclude the ".no-achievements-row" for obvious reasons

        highlightedRows.forEach(row => row.dataset.highlighted = true);   // no search input so everything is highlighted
    }

}
function clearHighlightedRows(tierContainer) {
    tierContainer.querySelectorAll("table.achievement-details-table tr")
        .forEach(row => row.removeAttribute("data-highlighted"));
}
//endregion