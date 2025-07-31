package com.example.scope.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.file.datalake.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdlsClientConfig {

    @Value("${adls.account-name}")
    private String accountName;
    @Value("${adls.source-container}")
    private String sourceContainer;
    @Value("${adls.result-container}")
    private String resultContainer;

    @Bean
    public DataLakeServiceClient serviceClient() {
        String endpoint = String.format("https://%s.dfs.core.windows.net", accountName);
        return new DataLakeServiceClientBuilder()
                .credential(new DefaultAzureCredentialBuilder().build())
                .endpoint(endpoint)
                .buildClient();
    }

    @Bean
    public DataLakeFileSystemClient fileSystemClient(DataLakeServiceClient svc) {
        return svc.getFileSystemClient(sourceContainer);
    }

    @Bean
    public DataLakeFileSystemClient resultFileSystemClient(DataLakeServiceClient svc) {
        return svc.getFileSystemClient(resultContainer);
    }
}
