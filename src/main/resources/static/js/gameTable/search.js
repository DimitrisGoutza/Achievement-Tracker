document.addEventListener("DOMContentLoaded", () => {
    /* --------------------- Event Listeners --------------------- */
    const gameSearch = document.getElementById("game-search");
    let searchTimeout;
    let previousSearchTerm = "";

    gameSearch.addEventListener("input", (event) => {
        const searchTerm = event.target.value.trim();

        clearTimeout(searchTimeout);
        const timeoutDuration = (searchTerm === "" ? 0 : 300);
        searchTimeout = setTimeout(() => {
            if (searchIsEligible(searchTerm, previousSearchTerm)) {
                searchGames(searchTerm);
                previousSearchTerm = searchTerm;
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

    const MIN_SEARCH_CHARACTER_LENGTH = 2;
    if (currentSearch.length < MIN_SEARCH_CHARACTER_LENGTH && previousSearch.length < MIN_SEARCH_CHARACTER_LENGTH)
        return false;

    // More checks..

    return true;
}

function searchGames(searchTerm) {
    const determineEndpointURL = () => {
        const selectedPageSize = document.getElementById("page-entries-select").value;

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
            updateTableContent(html);
            replacePlaceholderImages();
        })
        .catch(error => console.error("Error: "+error));
}