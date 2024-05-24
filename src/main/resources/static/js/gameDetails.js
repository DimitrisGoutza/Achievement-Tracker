/* ---------------------- Useful Element References ---------------------- */
const achievementTierTable = document.getElementById("achievement-tier-table");
/* ---------------------- Important Declarations ---------------------- */

/* ---------------------- DOMContentLoaded Actions ---------------------- */
showMessageForEmptyTiers();
replacePlaceholderImages();
/* ---------------------- Event Listeners ---------------------- */

/* ---------------------- Function Declarations ---------------------- */
function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll("img.achievement-icon");
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
        if (achievements.length === 0)
            container.querySelector("span.empty-tier-message").style.display = "";
    })
}