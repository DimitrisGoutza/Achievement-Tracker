let previousSize;
const pageSizeSelect = document.getElementById("page-entries-select");
const gameSearchInput = document.getElementById("game-search");
const existingTableBody = document.getElementById("table-content");
const existingPaginationFooter = document.getElementById("main-container-footer");

document.addEventListener("DOMContentLoaded", () => {
    replacePlaceholderImages();
    previousSize = parseInt(pageSizeSelect.value);

    /* --------------------- Event Listeners --------------------- */
    pageSizeSelect.addEventListener("change", () => changePageSize());

    attachEventListenersToPageButtons();
});

function attachEventListenersToPageButtons() {
    const currentPageNumber = parseInt(document.querySelector("button.selected-page-button").innerText);
    document.getElementById("page-prev-button")
        .addEventListener("click", () => changePageNumber(currentPageNumber - 1));
    document.getElementById("page-next-button")
        .addEventListener("click", () => changePageNumber(currentPageNumber + 1));

    const pageButtons = document.querySelectorAll("button.page-buttons:not(.selected-page-button, #page-prev-button, #page-next-button)");
    pageButtons.forEach(button => {
        const pageNumber = parseInt(button.innerText);
        button.addEventListener("click", () => changePageNumber(pageNumber));
    })
}

function changePageSize() {
    const pageSize = pageSizeSelect.value;
    const searchTerm = gameSearchInput.value;

    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", pageSize);
        if (searchTerm.length >= MIN_SEARCH_CHARACTER_LENGTH)
            params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            const currentPage = parseInt(document.querySelector("div#page-buttons-container").dataset.currentPage);

            updateTableContentForPageSize(html, currentPage !== 1, parseInt(pageSize));
            replacePlaceholderImages();
            attachEventListenersToPageButtons();
        })
        .catch(error => console.error("Error: "+error));
}

function changePageNumber(requestedPageNumber) {
    const pageSize = pageSizeSelect.value;
    const searchTerm = gameSearchInput.value;

    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", pageSize);
        params.set("page", requestedPageNumber);
        if (searchTerm.length >= MIN_SEARCH_CHARACTER_LENGTH)
            params.set("search", searchTerm);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContentForPage(html);
            replacePlaceholderImages();
            scrollToTop();
            attachEventListenersToPageButtons();
        })
        .catch(error => console.error("Error: "+error));
}

function updateTableContentForPage(html) {
    const fragment = document.createRange().createContextualFragment(html);
    // Update table body
    const updatedTableBody = fragment.getElementById("table-content");
    existingTableBody.innerHTML = updatedTableBody.innerHTML;
    // Update pagination footer
    const updatedPaginationFooter = fragment.getElementById("main-container-footer");
    existingPaginationFooter.innerHTML = updatedPaginationFooter.innerHTML;
}

function updateTableContentForPageSize(html, updateAllRows, currentSize) {
    const fragment = document.createRange().createContextualFragment(html);
    const updatedTableBody = fragment.getElementById("table-content");

    if (updateAllRows) {
        // Completely replace table body
        existingTableBody.innerHTML = updatedTableBody.innerHTML;
    } else {
        if (currentSize > previousSize) {
            // Append new rows
            const newTableRows = Array.from(updatedTableBody.querySelectorAll("tr")).slice(previousSize);
            newTableRows.forEach(row => existingTableBody.appendChild(row));
        } else {
            // Remove extra rows
            const rowsToBeRemoved = Array.from(existingTableBody.querySelectorAll("tr")).slice(currentSize).reverse();
            rowsToBeRemoved.forEach(row => row.remove());
        }
    }
    // Update pagination footer
    const updatedPaginationFooter = fragment.getElementById("main-container-footer");
    existingPaginationFooter.innerHTML = updatedPaginationFooter.innerHTML;
    // Finally update the previous size variable
    previousSize = pageSizeSelect.value;
}

function replacePlaceholderImages() {
    const imageElements = document.querySelectorAll('.game-banner-img, .achievement-icon');
    imageElements.forEach(image => {
        const actualSrc = image.dataset.actualSrc;

        const img = new Image();
        img.onload = () => image.setAttribute('src', actualSrc);
        img.src = actualSrc;
    })
}

function scrollToTop() {
    const table = document.querySelector("#main-container-header");
    const topOffset = table.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({
        top: topOffset,
        behavior: "instant"
    });
}