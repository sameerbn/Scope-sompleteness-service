package com.example.scope.service;

import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordComparisonService {

    private final SparkSession spark;

    public Dataset<Row> loadDataset(String path) {
        if (path.endsWith(".avro")) {
            return spark.read().format("avro").load(path);
        }
        return spark.read().parquet(path);
    }

    public ComparisonResult compareDatasets(List<String> expectedPaths, List<String> actualPaths) {
        Dataset<Row> expected = expectedPaths.stream()
                .map(this::loadDataset)
                .reduce(null, Dataset::union);
        Dataset<Row> actual = actualPaths.stream()
                .map(this::loadDataset)
                .reduce(null, Dataset::union);

        Dataset<Row> missingInTarget = expected.except(actual);
        Dataset<Row> missingInMetadata = actual.except(expected);

        return new ComparisonResult(missingInTarget, missingInMetadata);
    }

    @RequiredArgsConstructor
    public static class ComparisonResult {
        public final Dataset<Row> missingInTarget;
        public final Dataset<Row> missingInMetadata;
    }
}
