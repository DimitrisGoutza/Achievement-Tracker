/* ---------------------- Useful Element References ---------------------- */
//region Search
const existingResultList = document.getElementById("nav-game-search-results");
const navSearchInput = document.getElementById("nav-game-search");
const searchResultsContainer = document.getElementById("nav-game-search-results");
//endregion
/* ---------------------- Important Declarations ---------------------- */
//region Search
let navSearchTimeout;
const SEARCH_RESULT_SIZE = 50;
//endregion
/* ---------------------- DOMContentLoaded Actions ---------------------- */

/* ---------------------- Event Listeners ---------------------- */
//region Search
navSearchInput.addEventListener("input", () => {
    const term = navSearchInput.value.trim();
    const termIsEmpty = term === "";

    clearTimeout(navSearchTimeout);
    const timeoutDuration = (termIsEmpty ? 0 : 300);
    navSearchTimeout = setTimeout(() => {
        if (termIsEmpty)
            clearSearchResults();
        else
            fetchSearchResults(term);
    }, timeoutDuration);
});
//endregion
/* ---------------------- Function Declarations ---------------------- */
//region Search
function fetchSearchResults(searchTerm) {
    fetch(`/search?term=${searchTerm}&size=${SEARCH_RESULT_SIZE}`)
        .then(response => response.text())
        .then(html => {
            const fragment = document.createRange().createContextualFragment(html);
            const updatedResultList = fragment.querySelector("ul");

            existingResultList.innerHTML = updatedResultList.innerHTML;

            replacePlaceholderImages();

            if (searchResultsContainer.scrollHeight > searchResultsContainer.clientHeight) // overflowing on Y axis
                searchResultsContainer.scrollTo({top: 0, left: 0, behavior: "instant"});
        })
        .catch(error => console.error("Error: "+error));
}
function clearSearchResults() {
    const resultItems = searchResultsContainer.querySelectorAll("li");
    resultItems.forEach(result => result.remove());
}
//endregion