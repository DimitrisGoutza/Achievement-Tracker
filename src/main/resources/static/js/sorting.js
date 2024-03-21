const SortClasses = {
    default: "sorted-default",
    asc: "sorted-asc",
    desc: "sorted-desc"
};
const SORT_PARAM_FORMAT = "{column}_{sortDirection}";   // name_desc
const HEADER_ID_FORMAT = "{column}-header"; // name-header

document.addEventListener("DOMContentLoaded", function attachSortStates() {
    const params = new URLSearchParams(window.location.search);
    const sortParam = params.get("sort");
    if (sortParam) {
        const columnName = sortParam.split("_")[0];
        const sortDirection = sortParam.split("_")[1];

        const targetHeader = document.getElementById(HEADER_ID_FORMAT.replace("{column}", columnName));
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

    // Event Listeners
    const allHeaders = document.querySelectorAll("th");
    allHeaders.forEach(th => th.addEventListener("click", () => sortTable(th)));
});

function sortTable(targetHeader) {
    const sortDirectionElement = targetHeader.querySelector("span.sort-direction");
    const sortColumnName = targetHeader.querySelector("span.column-name").innerText
        .trim().replace(/\s+/g, "-").toLowerCase();

    const currentSortState = Object.keys(SortClasses).find(sortState =>
        sortDirectionElement.classList.contains(SortClasses[sortState]));
    const requestedSortState = getNextSortState(currentSortState);
    const requestedSortClass = SortClasses[requestedSortState];

    const determineEndpointURL = () => {
        const selectedPageSize = document.getElementById("page-entries-select").value;

        const fetchURL = new URL(window.location.href);
        const params = new URLSearchParams(fetchURL.searchParams);
        params.set("size", selectedPageSize);
        if (requestedSortClass !== SortClasses.default)
            params.set("sort", SORT_PARAM_FORMAT.replace("{column}", sortColumnName).replace("{sortDirection}", requestedSortState));

        fetchURL.search = params.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            updateTableContent(html);
            toggleSortDirection(sortDirectionElement, currentSortState, requestedSortState);
            resetSortClassesForOtherColumns(targetHeader);

            window.history.replaceState({}, "", generateVisibleURL(sortColumnName, requestedSortState, requestedSortClass));
        })
        .catch(error => console.error("Error: "+error));
}

function getNextSortState(currentSortState) {
    /*
    Returns the next element from SORT_CLASSES[],
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