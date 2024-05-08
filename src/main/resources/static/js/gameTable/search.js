const MIN_SEARCH_CHARACTER_LENGTH = 2;
document.addEventListener("DOMContentLoaded", () => {
    /* --------------------- Event Listeners --------------------- */
    const gameSearch = document.getElementById("game-search");
    let searchTimeout;
    let prevEligibleSearch = "";

    gameSearch.addEventListener("input", (event) => {
        const searchTerm = event.target.value.trim();

        clearTimeout(searchTimeout);
        const timeoutDuration = (searchTerm === "" ? 0 : 300);
        searchTimeout = setTimeout(() => {
            if (searchIsEligible(searchTerm, prevEligibleSearch)) {
                searchGames(searchTerm);
                prevEligibleSearch = searchTerm;
            }
        }, timeoutDuration);
    });
});

function searchIsEligible(currentSearch, previousSearch) {
    /*
    Search is not eligible for API request if:
        1. current search is the same as the previous
        2. both current and previous searches are below character limit
           (which means there's no need to reset the results, cause it already happened)
    */
    if (previousSearch === currentSearch)
        return false;

    if (currentSearch.length < MIN_SEARCH_CHARACTER_LENGTH && previousSearch.length < MIN_SEARCH_CHARACTER_LENGTH)
        return false;

    // More checks..

    return true;
}

function searchGames(searchTerm) {
    const selectedPageSize = pageSizeSelect.value;
    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);
        params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContentForSearch(html);
            replacePlaceholderImages();
            attachEventListenersToPageButtons();
        })
        .catch(error => console.error("Error: "+error));
}

function updateTableContentForSearch(html) {
    const fragment = document.createRange().createContextualFragment(html);
    // Update table body
    const updatedTableBody = fragment.getElementById("table-content");
    existingTableBody.innerHTML = updatedTableBody.innerHTML;
    // Update pagination footer
    const updatedPageFooter = fragment.getElementById("main-container-footer");
    existingPaginationFooter.innerHTML = updatedPageFooter.innerHTML;
}