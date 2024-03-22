document.addEventListener("DOMContentLoaded", () => {
    disableActivePageButton();
    // Event Listeners
    const selectPageSizeElement = document.getElementById("page-entries-select");
    selectPageSizeElement.addEventListener("change", () => changePageSize());
});

function changePageSize() {
    const determineEndpointURL = () => {
        const selectedPageSize = document.getElementById("page-entries-select").value;

        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);

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
        const selectedPageSize = document.getElementById("page-entries-select").value;

        const fetchURL = new URL(window.location.href);
        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);
        params.set("page", requestedPageNumber);

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContent(html);
            disableActivePageButton();
            scrollToTop();
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
    const activeButton = document.querySelector("button.selected");
    const activePageNumber = activeButton.innerText;
    if (activeButton.getAttribute("onclick").includes("changePageNumber("+activePageNumber+")"))
        activeButton.removeAttribute("onclick");
}

function scrollToTop() {
    const table = document.querySelector("table");
    const tableTopOffset = table.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({
        top: tableTopOffset,
        behavior: "smooth"
    });
}