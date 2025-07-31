package com.example.scope.model;

import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "retry_tracker")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RetryTracker {
    @Id @GeneratedValue
    private Long id;
    private Long jobId;
    private Timestamp attemptedAt;
    private String status;
    @Column(length = 1000)
    private String notes;
}
