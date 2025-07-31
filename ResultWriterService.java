package com.example.scope.service;

import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResultWriterService {

    @Value("${adls.output-container-name}")
    private String resultContainer;

    @Value("${adls.output-path-prefix}")
    private String prefix;

    private final SparkSession spark;

    public void writeResults(String groupKey, String runId,
                             Dataset<Row> missingInTarget,
                             Dataset<Row> missingInMetadata) {
        String base = String.format("abfss://%s@%s.dfs.core.windows.net/%s/%s", resultContainer,
                spark.sparkContext().hadoopConfiguration().get("fs.defaultFS"),
                prefix, runId);

        missingInTarget.write().mode("overwrite").json(base + "/missing_in_target.json");
        missingInMetadata.write().mode("overwrite").json(base + "/missing_in_metadata.json");
    }
}
