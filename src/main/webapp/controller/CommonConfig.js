// === BACKEND BASE URL ===

(function () {
    const pathSegments = window.location.pathname.split("/");
    const contextPath = pathSegments[1] || "";

    window.baseURL = window.location.origin + "/" + contextPath + "/";

    console.log("[CommonConfig] baseURL initialized:", window.baseURL);
})();

