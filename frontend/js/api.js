const NIRBHAYA_API_BASE_URL = window.NIRBHAYA_API_BASE_URL || "http://localhost:8082/api";

async function apiRequest(path, options = {}) {
    const requestOptions = { ...options };
    const headers = new Headers(requestOptions.headers || {});

    if (requestOptions.body && !(requestOptions.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
    }

    requestOptions.headers = headers;

    const response = await fetch(`${NIRBHAYA_API_BASE_URL}${path}`, requestOptions);
    const contentType = response.headers.get("content-type") || "";
    const body = contentType.includes("application/json")
        ? await response.json()
        : await response.text();

    if (!response.ok) {
        const message = body && typeof body === "object" ? body.message : body;
        throw new Error(message || `Request failed with status ${response.status}`);
    }

    return body;
}

function getStoredUser() {
    const rawUser = localStorage.getItem("nirbhaya_user");
    return rawUser ? JSON.parse(rawUser) : null;
}

function getStoredUserId() {
    return localStorage.getItem("nirbhaya_user_id") || getStoredUser()?.userId || null;
}

function storeAuthenticatedUser(user) {
    localStorage.setItem("nirbhaya_user", JSON.stringify(user));
    if (user.userId) localStorage.setItem("nirbhaya_user_id", user.userId);
    if (user.email) {
        localStorage.setItem("nirbhaya_current_user_email", user.email);
        localStorage.setItem("nirbhaya_user_email", user.email);
    }
    if (user.username) localStorage.setItem("nirbhaya_username", user.username);
    if (user.phoneNumber) localStorage.setItem("nirbhaya_userphone", user.phoneNumber);
}
