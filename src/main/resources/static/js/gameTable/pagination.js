const pageSizeSelect = document.getElementById("page-entries-select");
const gameSearchInput = document.getElementById("game-search");

document.addEventListener("DOMContentLoaded", () => {
    disableActivePageButton();

    /* --------------------- Event Listeners --------------------- */
    pageSizeSelect.addEventListener("change", () => changePageSize());
});

function changePageSize() {
    const determineEndpointURL = () => {
        const size = pageSizeSelect.value;
        const searchTerm = gameSearchInput.value;

        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", size);
        params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContent(html);
        })
        .catch(error => console.error("Error: "+error));
}

function changePageNumber(requestedPageNumber) {
    const determineEndpointURL = () => {
        const pageSize = pageSizeSelect.value;
        const searchTerm = gameSearchInput.value;

        const fetchURL = new URL(window.location.href);
        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", pageSize);
        params.set("page", requestedPageNumber);
        params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContent(html);
            disableActivePageButton();
            scrollToTopOfTable();
        })
        .catch(error => console.error("Error: "+error));
}

function updateTableContent(html) {
    const fragment = document.createRange().createContextualFragment(html);
    const updatedTableBody = fragment.getElementById("table-content");
    const updatedPageFooter = fragment.getElementById("pagination-footer");

    // update the relevant elements only
    document.getElementById("table-content").innerHTML = updatedTableBody.innerHTML;
    document.getElementById("pagination-footer").innerHTML = updatedPageFooter.innerHTML;
}

function disableActivePageButton() {
    const activeButton = document.getElementById("pagination-footer").querySelector("button.selected");
    if (activeButton)
        activeButton.removeAttribute("onclick");
}

function scrollToTopOfTable() {
    const table = document.querySelector("table");
    const tableTopOffset = table.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({
        top: tableTopOffset,
        behavior: "smooth"
    });
}