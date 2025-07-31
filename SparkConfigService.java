package com.example.scope.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfigService {

    @Bean
    public SparkSession sparkSession() {
        SparkConf conf = new SparkConf()
                .setAppName("ScopeCompletenessCheck")
                .setMaster(System.getProperty("spark.master", "local[*]"));
        return SparkSession.builder().config(conf).getOrCreate();
    }
}
