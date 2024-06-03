/* ---------------------- Useful Element References ---------------------- */
const achievementTierTable = document.getElementById("achievement-tier-table");
const achievementContainers = achievementTierTable.querySelectorAll("div.achievements-container");
//region Carousel
const carouselButtons = achievementTierTable.querySelectorAll("button.carousel-scroll-button");
//endregion
/* ---------------------- Important Declarations ---------------------- */
//region Carousel
const ScrollBehavior = { SMOOTH: "smooth", INSTANT: "instant" };
let continuousScrollInterval;
let mousedownDurationInterval;
//endregion
//region Tier Achievements
DropdownStates = { EXTENDED : "extended", HIDDEN: "hidden" };
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
//region Tier Achievements
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
//region Tier Achievements
const dropdownContainers = document.querySelectorAll("div.tier-dropdown-container");
dropdownContainers.forEach(container => container.addEventListener("click", () => showAchievementsForThisTier(container)));
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
//region Tier Achievements
function colorTableRowsAccordingToPercentage() {
    const activeColor = getComputedStyle(document.documentElement).getPropertyValue('--active-element-color');

    const achievementTables = document.querySelectorAll("table.achievement-details-table");
    achievementTables.forEach(table => {
        const backgroundColor = getComputedStyle(table.querySelector("tr")).backgroundColor;
        table.querySelectorAll("tr").forEach(row => {
            const percentage = parseFloat(row.dataset.percentage);
            row.style.background = `linear-gradient(to right, ${activeColor} ${percentage}%, ${rgbToRgba(backgroundColor, 0.75)} ${percentage}%, ${rgbToRgba(backgroundColor, 0.75)} 100%)`;
        });
    });
}
function showAchievementsForThisTier(containerClicked) {
    // TODO: add client side pagination for layout reasons
    const achievementTier = containerClicked.dataset.tier;
    const relatedAchievementTable = document.querySelector(`table.achievement-details-table[data-tier=${achievementTier}]`);

    const dropdownIndicatorElement = containerClicked.querySelector("span.dropdown-indicator");
    const tableIsHidden = relatedAchievementTable.dataset.state === DropdownStates.HIDDEN;
    if (tableIsHidden) {
        dropdownIndicatorElement.dataset.state = DropdownStates.EXTENDED;
        relatedAchievementTable.dataset.state = DropdownStates.EXTENDED;
    } else {
        dropdownIndicatorElement.dataset.state = DropdownStates.HIDDEN;
        relatedAchievementTable.dataset.state = DropdownStates.HIDDEN;
    }
}
//endregion