function changePageSize() {
    const determineEndpointURL = () => {
        const selectedPageSize = document.getElementById("page-entries-select").value;

        const fetchURL = new URL(window.location.origin + window.location.pathname + window.location.search);

        const existingParams = fetchURL.searchParams;
        const newParams = new URLSearchParams();
        existingParams.forEach((value, key) => newParams.set(key, value));
        newParams.set("size", selectedPageSize);

        fetchURL.search = newParams.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            const fragment = document.createRange().createContextualFragment(html);
            const updatedTableBody = fragment.getElementById("table-content");
            const updatedPageFooter = fragment.getElementById("pagination-footer");

            // update the relevant elements only
            document.getElementById("table-content").innerHTML = updatedTableBody.innerHTML;
            document.getElementById("pagination-footer").innerHTML = updatedPageFooter.innerHTML;
        })
        .catch(error => console.error("Error: "+error));
}

function changePageNumber(requestPageNumber) {
    const determineEndpointURL = () => {
        const selectedPageSize = document.getElementById("page-entries-select").value;

        const fetchURL = new URL(window.location.origin + window.location.pathname + window.location.search);

        const existingParams = fetchURL.searchParams;
        const newParams = new URLSearchParams();
        existingParams.forEach((value, key) => newParams.set(key, value));
        newParams.set("size", selectedPageSize);
        newParams.set("page", requestPageNumber);

        fetchURL.search = newParams.toString();
        return fetchURL;
    };

    fetch(determineEndpointURL())
        .then(response => response.text())
        .then(html => {
            const fragment = document.createRange().createContextualFragment(html);
            const updatedTableBody = fragment.getElementById("table-content");
            const updatedPageFooter = fragment.getElementById("pagination-footer");

            // update the relevant elements only
            document.getElementById("table-content").innerHTML = updatedTableBody.innerHTML;
            document.getElementById("pagination-footer").innerHTML = updatedPageFooter.innerHTML;

            scrollToTop();
        })
        .catch(error => console.error("Error: "+error));
}

function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}