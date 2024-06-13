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
const hiddenAchievementsCheckbox = document.querySelector("input[type='checkbox']#hidden-achievements-filter");
//endregion
/* ---------------------- Important Declarations ---------------------- */
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
let searchIsActive = false;
//endregion
/* ---------------------- DOMContentLoaded Actions ---------------------- */
//region General
replacePlaceholderImages();
showMessageForEmptyTiers();
//endregion
//region Carousel
makeCarouselScrollButtonsVisible();
disableOrEnableCarouselButtons();
//endregion
//region Achievement Tiers - Dropdown
tierDropdownContainers.forEach(container => setAchievementCountForContainer(container));
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
const dropdownContainers = document.querySelectorAll("div.tier-information");
dropdownContainers.forEach(container => container.addEventListener("click", function toggleDropdownContainer() {
    const tierContainer = container.parentElement;
    if (tierContainer.dataset.state === DropdownStates.COLLAPSED) {
        expandRowsPaginated(tierContainer);
        showOrHidePaginationButtons(tierContainer);
    }
    else {
        collapseDropdownContainer(tierContainer);
    }
}));
//endregion
//region Achievement Tiers - Pagination
const showMoreButtons = document.querySelectorAll("button.show-more-button");
showMoreButtons.forEach(button => button.addEventListener("click", () => {
    expandRowsPaginated(button.parentElement);
    showOrHidePaginationButtons(button.parentElement);
}));
const showLessButtons = document.querySelectorAll("button.show-less-button");
showLessButtons.forEach(button => button.addEventListener("click", () => {
    collapseRowsPaginated(button.parentElement);
    showOrHidePaginationButtons(button.parentElement);
}));
//endregion
//region Achievement Tiers - Filters
const searchInput = document.querySelector("input#achievement-search");
searchInput.addEventListener("input", (event) => {
    const searchTerm = event.target.value.trim();
    clearTimeout(searchTimeout);
    const timeoutDuration = (searchTerm === "" ? 0 : 100);

    searchTimeout = setTimeout(() => {
        if (searchTerm === "") {
            searchIsActive = false;
            tierDropdownContainers.forEach(container => {
                collapseDropdownContainer(container);
                setAchievementCountForContainer(container);
            });
        } else {
            searchIsActive = true;
            searchAchievements(searchTerm);
            tierDropdownContainers.forEach(container => {
                expandRowsPaginated(container);
                setAchievementCountForContainer(container);
                showOrHidePaginationButtons(container);
            });
        }
    }, timeoutDuration);
});
hiddenAchievementsCheckbox.addEventListener("change", () => {
   tierDropdownContainers.forEach(container => {
      setAchievementCountForContainer(container);

      const achievementRows = container.querySelectorAll("table.achievement-details-table tr:not(.no-achievements-row)");
      if (container.dataset.state === DropdownStates.EXPANDED) {
          if (hiddenAchievementsCheckbox.checked) {
              achievementRows.forEach(row => {
                  if (row.dataset.hidden)
                      expandRow(row);
              });
              showOrHidePaginationButtons(container);
          } else {
              achievementRows.forEach(row => {
                  if (row.dataset.hidden)
                      collapseRow(row);
              });
              showOrHidePaginationButtons(container);
          }
      }
      if (searchIsActive) {
          expandRowsPaginated(container);
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
function rgbToRgba(rgb, alpha) {
    // Extract the numbers from the rgb string using a regular expression
    const result = rgb.match(/\d+/g);

    // Check if the regex successfully found the numbers
    if (result && result.length === 3) {
        // Convert the numbers to integers
        const r = parseInt(result[0], 10);
        const g = parseInt(result[1], 10);
        const b = parseInt(result[2], 10);

        return `rgba(${r}, ${g}, ${b}, ${alpha})`;
    }
    return "rgba(0, 0, 0, 1)";
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
    const activeColor = getComputedStyle(document.documentElement).getPropertyValue('--active-element-color');
    const backgroundColor = getComputedStyle(document.querySelector("table.achievement-details-table tr:not(tr.no-achievements-row)")).backgroundColor;

    const achievementTables = document.querySelectorAll("table.achievement-details-table");
    achievementTables.forEach(table => {
        table.querySelectorAll("tr:not(tr.no-achievements-row)").forEach(row => {
            const percentage = parseFloat(row.dataset.percentage);
            row.style.background = `linear-gradient(to right, ${activeColor} ${percentage}%, ${rgbToRgba(backgroundColor, 0.75)} ${percentage}%, ${rgbToRgba(backgroundColor, 0.75)} 100%)`;
        });
    });
}
function setAchievementCountForContainer(tierContainer) {
    const relatedTable = tierContainer.querySelector("table.achievement-details-table");

    let achievementCount;
    if (searchIsActive) {
        achievementCount = relatedTable
            .querySelectorAll(hiddenAchievementsCheckbox.checked
                ? "tr[data-matches-search='true']:not(.no-achievements-row)"
                : "tr[data-matches-search='true']:not([data-hidden='true'], .no-achievements-row)")
            .length;
    } else {
        achievementCount = relatedTable
            .querySelectorAll(hiddenAchievementsCheckbox.checked
                ? "tr:not(.no-achievements-row)"
                : "tr:not([data-hidden='true'], .no-achievements-row)")
            .length;
    }

    const countElement = tierContainer.querySelector("span[data-role='count']");
    countElement.textContent = achievementCount;

    const noAchievementsRow = relatedTable.querySelector("tr.no-achievements-row");
    if (noAchievementsRow && achievementCount !== 0)
        noAchievementsRow.remove();
}
function collapseDropdownContainer(tierContainer) {
    // hide rows
    const expandedRows = tierContainer.querySelectorAll("table.achievement-details-table tr");
    expandedRows.forEach(row => collapseRow(row));
    // hide pagination button
    tierContainer.querySelector("button.show-more-button").style.display = "none";
    tierContainer.querySelector("button.show-less-button").style.display = "none";
    // finally update container state
    tierContainer.dataset.state = DropdownStates.COLLAPSED;
}
//endregion
//region Achievement Tiers - Pagination
function calculateExpandedRows(tierContainer) {
    const expandedRows = tierContainer.querySelectorAll(`table.achievement-details-table tr[data-state=${DropdownStates.EXPANDED}]`);
    return expandedRows.length;
}
function showOrHidePaginationButtons(tierContainer) {
    const currentlyExpanded = calculateExpandedRows(tierContainer);
    const totalAchievements = parseInt(tierContainer.querySelector("span[data-role='count']").textContent);

    const showMoreBtn = tierContainer.querySelector("button.show-more-button");
    showMoreBtn.style.display = totalAchievements > currentlyExpanded ? "flex" : "none";

    const showLessBtn = tierContainer.querySelector("button.show-less-button");
    showLessBtn.style.display = currentlyExpanded > ACHIEVEMENT_BATCH_SIZE ? "flex" : "none";
}
function expandRowsPaginated(tierContainer) {
    const currentlyExpanded = calculateExpandedRows(tierContainer);

    let allRows = [];
    if (searchIsActive) {
        allRows = Array.from(tierContainer.querySelectorAll(hiddenAchievementsCheckbox.checked
            ? "tr[data-matches-search='true']"
            : "tr[data-matches-search='true']:not([data-hidden='true'])"
        ));
    } else {
        allRows = Array.from(tierContainer.querySelectorAll(hiddenAchievementsCheckbox.checked
        ? "tr"
        : "tr:not([data-hidden='true'])"
        ));
    }
    const rowsToBeExpanded = allRows.slice(currentlyExpanded, currentlyExpanded + ACHIEVEMENT_BATCH_SIZE);

    tierContainer.dataset.state = (allRows.length === 0 ? DropdownStates.COLLAPSED : DropdownStates.EXPANDED);
    rowsToBeExpanded.forEach(row => expandRow(row));
}
function collapseRowsPaginated(tierContainer) {
    const currentlyExpanded = calculateExpandedRows(tierContainer);

    const allRows = Array.from(tierContainer.querySelectorAll("table.achievement-details-table tr[data-state='expanded']"));

    let rowsToBeCollapsed = [];
    const divisibleByBatchSize = currentlyExpanded % ACHIEVEMENT_BATCH_SIZE === 0;
    if (divisibleByBatchSize) {
        rowsToBeCollapsed = allRows.slice(currentlyExpanded - ACHIEVEMENT_BATCH_SIZE, currentlyExpanded);
    } else {
        const remainder = currentlyExpanded % ACHIEVEMENT_BATCH_SIZE;
        rowsToBeCollapsed = allRows.slice(currentlyExpanded - remainder, currentlyExpanded);
    }

    rowsToBeCollapsed.reverse().forEach(row => collapseRow(row));
}
//endregion
//region Achievement Tiers - Filters
function searchAchievements(term) {
    const regex = new RegExp(term, 'i');

    tierDropdownContainers.forEach(container => {
        let matchingRows = 0;
        container.querySelectorAll("table.achievement-details-table tr:not(tr.no-achievements-row)").forEach(row => {
            row.dataset.state = DropdownStates.COLLAPSED; // clear previous search states

            const title = row.querySelector(".achievement-title").innerText;
            const description = row.querySelector(".achievement-description").innerText;

            if (regex.test(title) || regex.test(description)) {
                row.dataset.matchesSearch = true;
                matchingRows++;
            } else {
                row.dataset.matchesSearch = false;
                collapseRow(row);
            }
        });
    });
}
//endregion