package com.example.scope.scheduler;

import com.example.scope.model.CompletenessCheckJob;
import com.example.scope.repository.CompletenessCheckJobRepository;
import com.example.scope.service.ScopeCompletenessRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompletenessCheckScheduler {

    private final CompletenessCheckJobRepository jobRepo;
    private final ScopeCompletenessRunner runner;

    @Scheduled(fixedRate = 60000)
    public void schedule() {
        Timestamp now = Timestamp.from(Instant.now());
        List<CompletenessCheckJob> jobs = jobRepo.findByStatusAndScheduledAtBefore("SCHEDULED", now);
        for (CompletenessCheckJob job : jobs) {
            runner.run(job.getGroupKey());
        }
    }
}
