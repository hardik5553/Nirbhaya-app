# Nirbhaya API Integration Status

Last checked: 2026-09-11

## Test command

```powershell
cd backend
.\mvnw.cmd test
```

Current automated result: **51 tests passed, 0 failures, 0 errors**.

`ApiEndpointSmokeTest` verifies all 46 REST mappings and invokes every route through Spring MockMvc. These tests use the isolated H2 test profile; they do not prove that the production Supabase database or external SMS/Storage services are available.

## Category 1: Connected to frontend (38)

These routes are now called by the current static frontend. The original 14 routes are followed by the 21 routes integrated in this pass:

- `GET /api/admin/users` - `admin.html`
- `POST /api/auth/register` - `signup.html`
- `POST /api/auth/login` - `login.html`
- `GET /api/auth/recovery-check` - `login.html`
- `POST /api/auth/admin-login` - `login.html`
- `POST /api/evidence/upload` - `global-sos.js`
- `POST /api/location/update` - `global-sos.js`
- `POST /api/sos/trigger` - `global-sos.js`
- `POST /api/sos/cancel/{id}` - `global-sos.js`
- `POST /api/trusted-contacts/add` - `contacts.html`
- `GET /api/trusted-contacts/{userId}` - `contacts.html`, `security.html`, `global-sos.js`
- `DELETE /api/trusted-contacts/delete/{id}` - `contacts.html`
- `PUT /api/users/profile` - `settings.html`
- `PUT /api/users/password` - `settings.html`
- `POST /api/evidence/upload-bytes` - `global-sos.js` fallback upload
- `GET /api/danger-zone/check` - `pages/track-location.html`
- `POST /api/emergency-contacts/add/{userId}` - `contacts.html` sync
- `GET /api/emergency-contacts/{userId}` - `contacts.html` sync
- `POST /api/fake-call/log/{userId}` - `security.html`
- `GET /api/analytics/{userId}` - `dashboard.html`
- `GET /api/location/history/{userId}` - `pages/track-location.html`
- `GET /api/location/{userId}` - `pages/track-location.html`
- `POST /api/location/update/{userId}` - `pages/track-location.html` and `global-sos.js`
- `GET /api/risk/evaluate` - `security.html` and `pages/track-location.html`
- `POST /api/safety/safe-route` - `pages/track-location.html`
- `GET /api/safety/route-deviation/{userId}` - `pages/track-location.html`
- `GET /api/safety/danger-zone-check` - `security.html` and `pages/track-location.html`
- `GET /api/safety/nearby-help` - `dashboard.html` and `pages/track-location.html`
- `POST /api/safety/distress` - `global-sos.js`
- `POST /api/sms/send` - `global-sos.js`
- `POST /api/sms/dispatch-sos` - `global-sos.js`
- `POST /api/sos/silent-trigger` - `security.html`
- `GET /api/sos/history/{userId}` - `history.html`
- `GET /api/trusted-contacts/all` - `admin.html`
- `PUT /api/trusted-contacts/update/{id}` - `contacts.html`
- `POST /api/danger-zone/add` - `pages/danger-zones.html`
- `GET /api/safety/crime-heatmap` - `pages/crime-heatmap.html`
- `GET /api/safety/live-sos-map` - `pages/live-sos-map.html`

Shared frontend API base URL: `http://localhost:8083/api`.

## Category 2: Integrated from existing frontend features (21)

These routes are now wired into existing frontend workflows without adding a hardware integration:

- `POST /api/evidence/upload-bytes` - binary evidence fallback
- `GET /api/danger-zone/check` - danger-zone check before/while tracking
- `POST /api/emergency-contacts/add/{userId}` - emergency contact form
- `GET /api/emergency-contacts/{userId}` - emergency contact list
- `POST /api/fake-call/log/{userId}` - fake-call activity logging
- `GET /api/analytics/{userId}` - dashboard/analytics data
- `GET /api/location/history/{userId}` - track-location history
- `GET /api/location/{userId}` - current saved location
- `POST /api/location/update/{userId}` - authenticated user location update
- `GET /api/risk/evaluate` - risk check during tracking
- `POST /api/safety/safe-route` - safe-route UI
- `GET /api/safety/route-deviation/{userId}` - route tracking warning
- `GET /api/safety/danger-zone-check` - safety screen danger-zone check
- `GET /api/safety/nearby-help` - nearby-help action
- `POST /api/safety/distress` - distress action
- `POST /api/sms/send` - SMS action
- `POST /api/sms/dispatch-sos` - SOS notification action
- `POST /api/sos/silent-trigger` - silent SOS action
- `GET /api/sos/history/{userId}` - history page
- `GET /api/trusted-contacts/all` - admin/contact management
- `PUT /api/trusted-contacts/update/{id}` - edit contact form

## Category 3: Hardware/device telemetry dependent (8)

These need watch/mobile sensor data with a defined payload. Current watch values are demo/browser Bluetooth values and are not posted to these routes yet:

- `POST /api/risk/evaluate-multi`
- `POST /api/risk-session/evaluate/{userId}`
- `POST /api/risk-session/event`
- `GET /api/risk-session/current/{userId}`
- `POST /api/safety/fall-detection`
- `POST /api/safety/heart-rate`
- `POST /api/sensor/data`
- `POST /api/smart-sos/evaluate/{userId}`

The backend also exposes location STOMP destinations, but no frontend STOMP/SockJS client is currently connected:

- WebSocket handshake: `GET /ws-location`
- Send destination: `SEND /app/update`
- Subscription destination: `SUBSCRIBE /topic/location/{userId}`

## Category 4: New workflow pages completed (0 remaining)

The three new admin pages have now been created and connected:

- `POST /api/danger-zone/add` - `frontend/pages/danger-zones.html`
- `GET /api/safety/crime-heatmap` - `frontend/pages/crime-heatmap.html`
- `GET /api/safety/live-sos-map` - `frontend/pages/live-sos-map.html`

## Important runtime checks

- Backend configured port: `8083` in `backend/src/main/resources/application.properties`.
- Frontend shared helper now defaults to `http://localhost:8083/api`.
- Login and signup already default to `8083`.
- Start backend with `cd backend; .\mvnw.cmd spring-boot:run`.
- Start static frontend with `cd frontend; python -m http.server 5500`.
- The smoke tests pass with H2 and do not require the backend process to be running.
- Live API testing requires valid Supabase database credentials and external service configuration.
