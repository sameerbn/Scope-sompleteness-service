package com.example.scope.repository;

import com.example.scope.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.sql.Timestamp;
import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByStatus(String status);
    List<FileMetadata> findByTargetDirectoryPathAndScannedAtBetween(String path, Timestamp start, Timestamp end);
}

public interface CompletenessCheckJobRepository extends JpaRepository<CompletenessCheckJob, Long> {
    List<CompletenessCheckJob> findByStatusAndScheduledAtBefore(String status, Timestamp time);
    List<CompletenessCheckJob> findByGroupKey(String groupKey);
}

public interface RetryTrackerRepository extends JpaRepository<RetryTracker, Long> {
    List<RetryTracker> findByJobIdOrderByAttemptedAt(Long jobId);
}
