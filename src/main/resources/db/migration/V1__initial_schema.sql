create table users (
    id uuid primary key,
    name varchar(255) not null,
    email varchar(255) not null unique,
    telegram_chat_id varchar(255),
    webhook_url varchar(1000),
    timezone varchar(64) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table watch_jobs (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    provider_type varchar(64) not null,
    train_number varchar(32) not null,
    journey_date date not null,
    source_station varchar(32) not null,
    destination_station varchar(32) not null,
    boarding_station varchar(32) not null,
    quota varchar(16) not null,
    travel_class varchar(16) not null,
    origin_departure_time timestamp not null,
    boarding_departure_time timestamp,
    quiet_hours_start time,
    quiet_hours_end time,
    status varchar(32) not null,
    next_poll_at timestamptz not null,
    last_checked_at timestamptz,
    last_alert_at timestamptz,
    current_status varchar(32) not null,
    current_raw_status varchar(255),
    booking_open boolean not null default false,
    chart_prepared boolean not null default false,
    available_seats integer,
    notify_telegram boolean not null default true,
    notify_email boolean not null default false,
    notify_webhook boolean not null default false,
    note varchar(1000),
    consecutive_failures integer not null default 0,
    circuit_state varchar(32) not null,
    circuit_open_until timestamptz,
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table train_status_history (
    id uuid primary key,
    watch_job_id uuid not null references watch_jobs(id) on delete cascade,
    provider_type varchar(64) not null,
    polled_at timestamptz not null,
    normalized_status varchar(32) not null,
    raw_status varchar(255) not null,
    booking_open boolean not null default false,
    chart_prepared boolean not null default false,
    available_seats integer,
    response_time_ms bigint,
    provider_reference varchar(255),
    note varchar(2000)
);

create table alerts (
    id uuid primary key,
    watch_job_id uuid not null references watch_jobs(id) on delete cascade,
    user_id uuid not null references users(id) on delete cascade,
    alert_type varchar(32) not null,
    channel varchar(32) not null,
    dedupe_key varchar(255) not null,
    title varchar(255) not null,
    body varchar(4000) not null,
    strong_alert boolean not null default false,
    sent_at timestamptz not null,
    delivery_status varchar(32) not null,
    metadata varchar(2000)
);

create table provider_errors (
    id uuid primary key,
    watch_job_id uuid not null references watch_jobs(id) on delete cascade,
    provider_type varchar(64) not null,
    occurred_at timestamptz not null,
    error_type varchar(255) not null,
    message varchar(2000) not null,
    consecutive_failures integer not null,
    http_status integer,
    retriable boolean not null
);

create index idx_watch_jobs_status_next_poll on watch_jobs(status, next_poll_at);
create index idx_train_status_history_job_polled_at on train_status_history(watch_job_id, polled_at desc);
create index idx_alerts_job_sent_at on alerts(watch_job_id, sent_at desc);
create index idx_provider_errors_job_occurred_at on provider_errors(watch_job_id, occurred_at desc);
