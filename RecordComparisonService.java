package com.example.completenesscheck.service;

import org.apache.spark.sql.*;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.functions.*;

@Service
public class RecordComparisonService {

    private final SparkSession spark;

    @Autowired
    public RecordComparisonService(SparkSession spark) {
        this.spark = spark;
    }

    public void compareRecords(String pathA,
                                String pathB,
                                List<String> pairingKeys,
                                String outputPath) {

        long startTime = System.currentTimeMillis();
        System.out.println("🔍 Starting comparison: " + pathA + " vs " + pathB);

        // 1️⃣ Read with predicate pushdown & schema merging disabled for performance
        Dataset<Row> dfA = readDataset(pathA).selectExpr(pairingKeys.toArray(new String[0]));
        Dataset<Row> dfB = readDataset(pathB).selectExpr(pairingKeys.toArray(new String[0]));

        // 2️⃣ Cache because we'll reuse in two anti-joins
        dfA.cache();
        dfB.cache();

        // 3️⃣ Find missing in B (exists in A, not in B)
        Dataset<Row> missingInB = dfA.join(dfB, toJoinCondition(dfA, dfB, pairingKeys), "left_anti");

        // 4️⃣ Find missing in A (exists in B, not in A)
        Dataset<Row> missingInA = dfB.join(dfA, toJoinCondition(dfB, dfA, pairingKeys), "left_anti");

        // 5️⃣ Persist results in separate folders (coalesce to avoid tiny files)
        writeJson(missingInA, outputPath + "/missing_in_A");
        writeJson(missingInB, outputPath + "/missing_in_B");

        // 6️⃣ Log runtime
        long endTime = System.currentTimeMillis();
        System.out.println("✅ Comparison completed in " + (endTime - startTime) / 1000 + "s");
    }

    private Dataset<Row> readDataset(String path) {
        if (path.endsWith(".avro")) {
            return spark.read()
                    .format("avro")
                    .option("mergeSchema", "false")
                    .load(path);
        } else {
            return spark.read()
                    .format("parquet")
                    .option("mergeSchema", "false")
                    .load(path);
        }
    }

    private Column toJoinCondition(Dataset<Row> left, Dataset<Row> right, List<String> keys) {
        Column condition = lit(true);
        for (String key : keys) {
            condition = condition.and(left.col(key).equalTo(right.col(key)));
        }
        return condition;
    }

    private void writeJson(Dataset<Row> df, String path) {
        df.coalesce(1) // fewer files for easier consumption
          .write()
          .mode(SaveMode.Overwrite)
          .json(path);
    }
}
