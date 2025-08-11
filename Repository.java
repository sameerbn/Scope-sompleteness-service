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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    @Query(value = "SELECT new com.example.ScheduledGroupDto(f.targetDirectoryPath, MAX(f.lastModified), string_agg(f.filePath, ',')) " +
                   "FROM FileMetadata f " +
                   "WHERE f.status = 'NEW' " +
                   "GROUP BY f.targetDirectoryPath")
    List<ScheduledGroupDto> findNewFilesGroupedWithPaths();
}
