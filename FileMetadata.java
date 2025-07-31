package com.example.scope.model;

import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "file_metadata")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FileMetadata {
    @Id @GeneratedValue
    private Long id;
    private String filePath;
    private Timestamp lastModified;
    private Timestamp scannedAt;
    private String status;
    private String targetDirectoryPath;
    @ElementCollection
    private List<String> pairingKeys;
}
