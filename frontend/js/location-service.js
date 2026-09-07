// location-service.js
function startLocationTracking() {
    if (navigator.geolocation) {
        navigator.geolocation.watchPosition((position) => {
            const coords = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            // Location ko browser ki storage mein save kar dein
            localStorage.setItem('current_lat', coords.lat);
            localStorage.setItem('current_lng', coords.lng);
            console.log("Tracking:", coords);
        }, (error) => {
            console.error("Location access denied", error);
        }, { enableHighAccuracy: true });
    }
}

// Page load hote hi start ho jaye
window.onload = startLocationTracking;