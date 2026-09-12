# Nirbhaya integration setup

The application folders are referred to as follows:

- `frontend/` is the current static HTML/JavaScript frontend.
- `backend/` is the **backend** Spring Boot application.

## Development architecture

The frontend calls the backend at `https://nirbhaya-app.onrender.com`. The backend uses Supabase PostgreSQL through Spring Data JPA and Supabase Storage for evidence files. The browser never receives the Supabase service-role key.

The repository contains an earlier React migration plan, but the active UI is currently the static HTML implementation:

```text
frontend/
   src/
      App.jsx
      api.js
      main.jsx
      styles.css
   index.html
   package.json
   vite.config.js
```

The static pages should call the Spring Boot API. New features should not add direct database calls from the browser.

The database is Supabase PostgreSQL. Controllers and frontend API paths remain unchanged so persistence stays behind the repository/service boundary.

## Supabase database setup

1. Open Supabase Dashboard > SQL Editor and run `backend/supabase/schema.sql`.

   If an existing account must be promoted manually, run this once from SQL Editor:

   ```sql
   update users set role = 'ADMIN' where email = 'admin@example.com';
   ```

2. Copy `backend/.env.example` to a local environment file or set these variables in the terminal:

   ```powershell
   $env:SUPABASE_DB_URL = "jdbc:postgresql://aws-0-region.pooler.supabase.com:6543/postgres?sslmode=require"
   $env:SUPABASE_DB_USERNAME = "postgres.project_ref"
   $env:SUPABASE_DB_PASSWORD = "your_database_password"
   $env:SUPABASE_URL = "https://your-project-ref.supabase.co"
   $env:SUPABASE_SERVICE_ROLE_KEY = "your_service_role_key"
   $env:SUPABASE_STORAGE_BUCKET = "panic-evidence"
   $env:ADMIN_REGISTRATION_KEY = "your_private_admin_key"
   ```

3. Start the backend:

   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run
   ```

The backend listens on port `8083`.

Admin accounts require this same predefined key during both registration and login. The key is read only by the backend from `ADMIN_REGISTRATION_KEY`; it is never placed in frontend code. A successful admin login receives a short-lived server-side admin session token used by the admin page.

## Supabase Storage setup

1. Create a private `panic-evidence` bucket in Supabase Storage, or run the bucket statement in `backend/supabase/schema.sql`.
2. Set `SUPABASE_URL`, `SUPABASE_SERVICE_ROLE_KEY`, and `SUPABASE_STORAGE_BUCKET` only in backend environment variables.
3. Do not place the service-role key in frontend files or commit it.
4. The backend endpoint `POST /api/evidence/upload` accepts multipart fields `userId` and `file`, uploads to the `<userId>/...` path, and returns a one-hour signed URL.
5. Configure an upload limit and Storage policies before production. Evidence ownership must be checked from the authenticated user after JWT is added.

## Frontend setup

Serve the static frontend with a local server:

```powershell
cd frontend
python -m http.server 5500
```

Open `http://localhost:5500`. Set `window.NIRBHAYA_API_BASE_URL` before the page scripts when the backend is deployed somewhere other than `https://nirbhaya-app.onrender.com`.

## Tests

Run backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

The tests use an isolated H2 profile and do not require Supabase credentials. Never use production data for tests.

## Supabase production checklist

1. Keep database credentials in deployment secrets, never in frontend files.
2. Apply `backend/supabase/schema.sql` to staging first and enable RLS policies before production.
3. Set `SUPABASE_DB_URL`, `SUPABASE_DB_USERNAME`, and `SUPABASE_DB_PASSWORD` in the backend host.
4. Keep the frontend pointed at the deployed Spring Boot API; the existing Supabase-backed legacy pages retain their features.
5. Enable HTTPS, JWT authentication, restricted CORS, Cloudinary signed uploads where appropriate, backups, and monitoring before launch.

## Current limitations

- JWT authentication is not implemented yet; Spring Security currently permits requests for development.
- `login.html` and `signup.html` use `/api/auth/login` and `/api/auth/register`. Other legacy pages still contain direct Supabase calls and are not connected to the Spring Boot API yet.
- The backend test suite uses H2 for isolated tests; Supabase staging integration tests still need to be added.
- Password recovery has no backend endpoint yet; the CAPTCHA flow cannot safely log a user in or reset a password.
