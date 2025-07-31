package com.example.scope.model;

import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "completeness_check_job")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompletenessCheckJob {
    @Id @GeneratedValue
    private Long id;
    private String groupKey;
    private Timestamp scheduledAt;
    private int retryCount;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
