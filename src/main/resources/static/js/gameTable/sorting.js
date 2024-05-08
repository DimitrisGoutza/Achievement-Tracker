const SortClasses = {
    default: "sorted-default",
    asc: "sorted-asc",
    desc: "sorted-desc"
};
const SORT_PARAM_FORMAT = "{column}_{sortDirection}";   // name_desc
// TODO : avoid hardcoded CSS classes, make use of data-sorted attr

document.addEventListener("DOMContentLoaded", () => {
    attachSortStates();

    /* --------------------- Event Listeners --------------------- */
    const allHeaders = document.querySelectorAll("th");
    allHeaders.forEach(th => th.addEventListener("click", () => sortTable(th)));
});

function sortTable(targetHeader) {
    const sortDirectionElement = targetHeader.querySelector("span.sort-direction");
    const sortColumnName = targetHeader.dataset.columnName;

    const currentSortState = Object.keys(SortClasses).find(sortState =>
        sortDirectionElement.classList.contains(SortClasses[sortState]));
    const requestedSortState = getNextSortState(currentSortState);
    const requestedSortClass = SortClasses[requestedSortState];

    const selectedPageSize = pageSizeSelect.value;
    const searchTerm = gameSearchInput.value;
    const determineEndpointURL = () => {
        const fetchURL = new URL(window.location.href);

        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);
        if (searchTerm.length >= MIN_SEARCH_CHARACTER_LENGTH)
            params.set("search", searchTerm);
        if (requestedSortClass === SortClasses.default)
            params.delete("sort");
        else
            params.set("sort", SORT_PARAM_FORMAT.replace("{column}", sortColumnName).replace("{sortDirection}", requestedSortState));

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            const currentPageNumber = parseInt(document.querySelector("div#page-buttons-container").dataset.currentPage);
            updateTableContentForSort(html, currentPageNumber !== 1);
            replacePlaceholderImages();
            toggleSortDirection(sortDirectionElement, currentSortState, requestedSortState);
            resetSortClassesForOtherColumns(targetHeader);

            window.history.replaceState({}, "", generateVisibleURL(sortColumnName, requestedSortState, requestedSortClass));
        })
        .catch(error => console.error("Error: "+error));
}

function updateTableContentForSort(html, updatePaginationFooter) {
    const fragment = document.createRange().createContextualFragment(html);
    // Update table body
    const updatedTableBody = fragment.getElementById("table-content");
    existingTableBody.innerHTML = updatedTableBody.innerHTML;
    if (updatePaginationFooter) {
        // Update pagination footer
        const updatedPaginationFooter = fragment.getElementById("main-container-footer");
        existingPaginationFooter.innerHTML = updatedPaginationFooter.innerHTML;
    }
}

function getNextSortState(currentSortState) {
    /*
    Returns the next element from SortClasses[],
    resets to the start when reaching the end of the Array
    */
    const indexOfCurrentState = Object.keys(SortClasses).indexOf(currentSortState);
    const size = Object.keys(SortClasses).length;
    return Object.keys(SortClasses)[(indexOfCurrentState + 1) % size];
}

function toggleSortDirection(sortDirectionElement, currentSortState, requestedSortState) {
    sortDirectionElement.classList.remove(SortClasses[currentSortState]);
    sortDirectionElement.classList.add(SortClasses[requestedSortState]);
}

function resetSortClassesForOtherColumns(excludedHeader) {
    const allHeaders = document.querySelectorAll("th");

    allHeaders.forEach(header => {
        // for every other header
        if (header !== excludedHeader) {
            const sortDirectionElement = header.querySelector("span.sort-direction");
            // clear it from all appended sort classes (if any)
            Object.values(SortClasses).forEach(sortClass => sortDirectionElement.classList.remove(sortClass));
            // add the default sort class
            sortDirectionElement.classList.add(SortClasses.default);
        }
    });
}

function generateVisibleURL(sortColumnName, requestedSortState, requestedSortClass) {
    const newVisibleURL = new URL(window.location.href);

    const params = new URLSearchParams(newVisibleURL.searchParams);
    if (requestedSortClass === SortClasses.default)
        params.delete("sort");
    else
        params.set("sort", SORT_PARAM_FORMAT.replace("{column}", sortColumnName).replace("{sortDirection}", requestedSortState));

    newVisibleURL.search = params.toString();
    return newVisibleURL;
}

function attachSortStates() {
    const params = new URLSearchParams(window.location.search);
    const sortParam = params.get("sort");
    if (sortParam) {
        const columnName = sortParam.split("_")[0];
        const sortDirection = sortParam.split("_")[1];

        const targetHeader = document.querySelector(`th[data-column-name='${columnName}']`);
        targetHeader.querySelector("span.sort-direction").classList.add(SortClasses[sortDirection]);

        document.querySelectorAll("th").forEach(th => {
            if (th !== targetHeader)
                th.querySelector("span.sort-direction").classList.add(SortClasses.default);
        });
    } else {
        document.querySelectorAll("th").forEach(th =>
            th.querySelector("span.sort-direction").classList.add(SortClasses.default)
        );
    }
}