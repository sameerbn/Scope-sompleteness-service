package com.example.scope.service;

import com.azure.storage.file.datalake.*;
import com.azure.storage.file.datalake.models.PathItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileScannerService {

    private final DataLakeFileSystemClient fileSystemClient;

    public Set<String> scanFilesModifiedAfter(String directoryPath, Timestamp fromTime) {
        Set<String> paths = new HashSet<>();
        fileSystemClient.getDirectoryClient(directoryPath).listPaths(true)
            .forEach(item -> {
                if (!item.isDirectory() && item.getLastModified().toInstant().isAfter(fromTime.toInstant())) {
                    paths.add(item.getName());
                }
            });
        return paths;
    }
}
