create extension if not exists pgcrypto;

-- Create the storage bucket in Supabase Dashboard > Storage, or run this with the SQL editor.
insert into storage.buckets (id, name, public)
values ('panic-evidence', 'panic-evidence', false)
on conflict (id) do nothing;
update storage.buckets set public = false where id = 'panic-evidence';

create table if not exists users (
    id uuid primary key default gen_random_uuid(),
    unique_numeric_id varchar(32),
    username varchar(255),
    email varchar(320) not null unique,
    password varchar(255) not null,
    phone_number varchar(64),
    otp varchar(32),
    verified boolean not null default false,
    default_risk_level varchar(32),
    role varchar(32) not null default 'USER'
);
alter table users add column if not exists role varchar(32) not null default 'USER';

create table if not exists trusted_contacts (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255) not null,
    name varchar(255) not null,
    phone_number varchar(64) not null,
    relationship varchar(128)
);
create index if not exists idx_trusted_contacts_user on trusted_contacts(user_id);

create table if not exists emergency_contacts (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255) not null,
    name varchar(255) not null,
    phone_number varchar(64) not null
);
create index if not exists idx_emergency_contacts_user on emergency_contacts(user_id);

create table if not exists danger_zones (
    id uuid primary key default gen_random_uuid(),
    name varchar(255),
    center_lat double precision,
    center_lng double precision,
    radius_meters double precision,
    risk_level varchar(32)
);

create table if not exists incidents (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255), status varchar(64), type varchar(128),
    latitude double precision, longitude double precision,
    event_type varchar(128), description text, timestamp timestamp
);
create index if not exists idx_incidents_user_time on incidents(user_id, timestamp desc);

create table if not exists location_updates (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255), lat double precision, lng double precision, timestamp timestamp
);
create index if not exists idx_location_updates_user_time on location_updates(user_id, timestamp desc);

create table if not exists user_locations (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255), latitude double precision, longitude double precision, timestamp timestamp
);

create table if not exists sensor_data (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255), type varchar(128), heart_rate double precision,
    lat double precision, lng double precision, timestamp timestamp
);

create table if not exists sos_incidents (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255), status varchar(64), is_silent boolean not null default false, timestamp timestamp
);
create index if not exists idx_sos_incidents_user_time on sos_incidents(user_id, timestamp desc);

create table if not exists fake_call_logs (
    id uuid primary key default gen_random_uuid()
);

create table if not exists risk_sessions (
    id uuid primary key default gen_random_uuid(),
    user_id varchar(255) unique, current_score double precision,
    zone varchar(128), last_updated timestamp, active_incident_id varchar(255)
);
create table if not exists risk_session_events (
    session_id uuid not null references risk_sessions(id) on delete cascade,
    type varchar(128), weight double precision, timestamp timestamp
);

create table if not exists user_trusted_contacts (
    user_id uuid not null references users(id) on delete cascade,
    trusted_contact_ids varchar(255)
);
