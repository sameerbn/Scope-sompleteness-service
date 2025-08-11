CREATE TABLE dq_file_processing_schedule (
    id BIGSERIAL PRIMARY KEY,
    target_directory_path TEXT,
    latest_last_modified TIMESTAMP,
    all_file_paths TEXT,
    scheduled_time TIMESTAMP,
    status TEXT,
    pairing_keys TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE dq_retry_tracker (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT,
    attempted_at TIMESTAMP,
    status VARCHAR(1000),
    notes TEXT
);
