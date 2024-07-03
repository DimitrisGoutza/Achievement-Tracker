/* ---------------------- Useful Element References ---------------------- */

/* ---------------------- Important Declarations ---------------------- */

/* ---------------------- DOMContentLoaded Actions ---------------------- */
//region General
replacePlaceholderImages();
//endregion

/* ---------------------- Event Listeners ---------------------- */

/* ---------------------- Function Declarations ---------------------- */
//region General
function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll('img.game-capsule, img.achievement-icon, img.game-capsule, img.game-capsule-small');
    imageElements.forEach(image => {
        const actualSrc = image.dataset.actualSrc;

        const img = new Image();
        img.onload = () => image.setAttribute('src', actualSrc);
        img.src = actualSrc;
    });
}
//endregion