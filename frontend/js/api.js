const NIRBHAYA_API_BASE_URL = window.NIRBHAYA_API_BASE_URL || "http://localhost:8083/api";

async function apiRequest(path, options = {}) {
    const requestOptions = { ...options };
    const headers = new Headers(requestOptions.headers || {});

    if (requestOptions.body && !(requestOptions.body instanceof FormData) && !headers.has("Content-Type")) {
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

// Category 2 API helpers. Pages use these helpers so query and JSON contracts stay consistent.
async function uploadEvidenceBytes(userId, bytes) {
    return apiRequest(`/evidence/upload-bytes?userId=${encodeURIComponent(userId)}`, {
        method: "POST",
        headers: { "Content-Type": "application/octet-stream" },
        body: bytes
    });
}

async function checkDangerZone(lat, lng) {
    return apiRequest(`/danger-zone/check?lat=${encodeURIComponent(lat)}&lng=${encodeURIComponent(lng)}`);
}

async function addDangerZone(payload) {
    return apiRequest("/danger-zone/add", { method: "POST", body: JSON.stringify(payload) });
}

async function getCrimeHeatmap() {
    return apiRequest("/safety/crime-heatmap");
}

async function getLiveSosMap() {
    return apiRequest("/safety/live-sos-map");
}

async function addEmergencyContact(userId, name, phoneNumber) {
    return apiRequest(`/emergency-contacts/add/${encodeURIComponent(userId)}?name=${encodeURIComponent(name)}&phoneNumber=${encodeURIComponent(phoneNumber)}`, { method: "POST" });
}

async function getEmergencyContacts(userId) {
    return apiRequest(`/emergency-contacts/${encodeURIComponent(userId)}`);
}

async function logFakeCall(userId, callerName) {
    return apiRequest(`/fake-call/log/${encodeURIComponent(userId)}?callerName=${encodeURIComponent(callerName)}`, { method: "POST" });
}

async function getIncidentAnalytics(userId) {
    return apiRequest(`/analytics/${encodeURIComponent(userId)}`);
}

async function getLocationHistory(userId) {
    return apiRequest(`/location/history/${encodeURIComponent(userId)}`);
}

async function getCurrentLocation(userId) {
    return apiRequest(`/location/${encodeURIComponent(userId)}`);
}

async function updateUserLocation(userId, latitude, longitude) {
    return apiRequest(`/location/update/${encodeURIComponent(userId)}?latitude=${encodeURIComponent(latitude)}&longitude=${encodeURIComponent(longitude)}`, { method: "POST" });
}

async function evaluateRisk(latitude, longitude) {
    return apiRequest(`/risk/evaluate?latitude=${encodeURIComponent(latitude)}&longitude=${encodeURIComponent(longitude)}`);
}

async function requestSafeRoute(payload) {
    return apiRequest("/safety/safe-route", { method: "POST", body: JSON.stringify(payload) });
}

async function checkRouteDeviation(userId, latitude, longitude, plannedRoute = []) {
    return apiRequest(`/safety/route-deviation/${encodeURIComponent(userId)}?latitude=${encodeURIComponent(latitude)}&longitude=${encodeURIComponent(longitude)}`);
}

async function checkSafetyZone(latitude, longitude) {
    return apiRequest(`/safety/danger-zone-check?latitude=${encodeURIComponent(latitude)}&longitude=${encodeURIComponent(longitude)}`);
}

async function findNearbyHelp(latitude, longitude) {
    return apiRequest(`/safety/nearby-help?latitude=${encodeURIComponent(latitude)}&longitude=${encodeURIComponent(longitude)}`);
}

async function reportDistress(payload) {
    return apiRequest("/safety/distress", { method: "POST", body: JSON.stringify(payload) });
}

async function sendSms(phoneNumber, message, isOfflineFallback = false) {
    return apiRequest(`/sms/send?phoneNumber=${encodeURIComponent(phoneNumber)}&message=${encodeURIComponent(message)}&isOfflineFallback=${isOfflineFallback}`, { method: "POST" });
}

async function dispatchSosSms(phoneNumbers, latitude, longitude) {
    const params = new URLSearchParams({ latitude: String(latitude), longitude: String(longitude) });
    phoneNumbers.forEach(phoneNumber => params.append("phoneNumbers", phoneNumber));
    return apiRequest(`/sms/dispatch-sos?${params.toString()}`, { method: "POST" });
}

async function triggerSilentSos(userId) {
    return apiRequest("/sos/silent-trigger", { method: "POST", body: JSON.stringify({ userId }) });
}

async function getSosHistory(userId) {
    return apiRequest(`/sos/history/${encodeURIComponent(userId)}`);
}

async function getAllTrustedContacts() {
    return apiRequest("/trusted-contacts/all");
}

async function updateTrustedContact(id, payload) {
    return apiRequest(`/trusted-contacts/update/${encodeURIComponent(id)}`, { method: "PUT", body: JSON.stringify(payload) });
}
