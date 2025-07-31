package com.example.scope.service;

import com.example.scope.model.*;
import com.example.scope.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScopeCompletenessRunner {

    private final FileMetadataRepository metadataRepo;
    private final CompletenessCheckJobRepository jobRepo;
    private final RetryTrackerRepository retryRepo;
    private final FileScannerService scanner;
    private final RecordComparisonService comparer;
    private final ResultWriterService writer;

    private static final int MAX_RETRY = 5;

    public void run(String groupKey) {
        CompletenessCheckJob job = jobRepo.findByGroupKey(groupKey).get(0);
        job.setStatus("RUNNING");
        jobRepo.save(job);

        List<FileMetadata> group = metadataRepo.findByTargetDirectoryPathAndScannedAtBetween(
                job.getGroupKey(), job.getScheduledAt(), Timestamp.from(Instant.now()));

        if (group.isEmpty()) return;

        List<String> expectedPaths = group.stream()
                .map(FileMetadata::getFilePath)
                .toList();

        String targetDirectory = group.get(0).getTargetDirectoryPath();
        Timestamp earliest = group.stream().map(FileMetadata::getLastModified).min(Timestamp::compareTo).get();

        List<String> actualPaths = scanner.scanFilesModifiedAfter(targetDirectory, earliest).stream().toList();

        RecordComparisonService.ComparisonResult result = comparer.compareDatasets(expectedPaths, actualPaths);
        boolean hasMissingInTarget = !result.missingInTarget.isEmpty();

        String runId = groupKey + "_" + Instant.now().toEpochMilli();
        writer.writeResults(groupKey, runId, result.missingInTarget, result.missingInMetadata);

        retryRepo.save(RetryTracker.builder()
                .jobId(job.getId())
                .attemptedAt(Timestamp.from(Instant.now()))
                .status(hasMissingInTarget ? "RETRYING" : "MATCHED")
                .notes("Missing count: " + result.missingInTarget.count())
                .build());

        if (hasMissingInTarget && job.getRetryCount() < MAX_RETRY) {
            job.setStatus("SCHEDULED");
            job.setRetryCount(job.getRetryCount() + 1);
            job.setScheduledAt(Timestamp.from(Instant.now().plusSeconds(1800)));
        } else {
            job.setStatus(hasMissingInTarget ? "FAILED" : "SUCCESS");
        }
        job.setUpdatedAt(Timestamp.from(Instant.now()));
        jobRepo.save(job);
    }
}
