/* ---------------------- Useful Element References ---------------------- */
const achievementTierTable = document.getElementById("achievement-tier-table");
const achievementContainers = achievementTierTable.querySelectorAll("div.achievements-container");
const carouselButtons = achievementTierTable.querySelectorAll("button.carousel-scroll-button");
/* ---------------------- Important Declarations ---------------------- */
//region Carousel
const ScrollBehavior = { SMOOTH: "smooth", INSTANT: "instant" };
let continuousScrollInterval;
let mousedownDurationInterval;
//endregion
/* ---------------------- DOMContentLoaded Actions ---------------------- */
replacePlaceholderImages();
showMessageForEmptyTiers();
//region Carousel
makeCarouselScrollButtonsVisible();
disableOrEnableCarouselButtons();
//endregion
colorTableRowsAccordingToPercentage();
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
/* ---------------------- Function Declarations ---------------------- */
function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll("img.achievement-icon, img.achievement-icon-big");
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
function colorTableRowsAccordingToPercentage() {
    const activeColor = getComputedStyle(document.documentElement).getPropertyValue('--active-element-color');

    const achievementTables = document.querySelectorAll("table.achievement-details-table");
    achievementTables.forEach(table => {
        const backgroundColor = getComputedStyle(table.querySelector("tr")).backgroundColor;
        table.querySelectorAll("tr").forEach(row => {
            const percentage = parseFloat(row.dataset.percentage);
            row.style.background = `linear-gradient(to right, ${activeColor} ${percentage}%, ${backgroundColor} ${percentage}%, ${backgroundColor} 100%)`;
        });
    });
}
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