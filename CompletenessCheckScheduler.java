import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompletenessCheckScheduler {

    private final FileProcessingScheduleRepository jobRepo;
    private final FileProcessingService fileProcessingService;

    // A new service to handle local Spark job execution
    private final CompletenessCheckLocalRunner localRunner; 

    // Configuration property to enable/disable local mode
    @Value("${local.mode.enabled:false}")
    private boolean localModeEnabled;

    // Livy-related fields
    @Value("${livy.endpoint}")
    private String livyEndpoint;
    @Value("${livy.username}")
    private String livyUsername;
    @Value("${livy.password}")
    private String livyPassword;
    @Value("${livy.jar.path}")
    private String livyJarPath;

    @Scheduled(cron = "0 */3 * * * *")
    public void schedule() throws Exception {
        // Step 1: Create the new scheduled entries
        fileProcessingService.createProcessingSchedule();

        // Step 2: Fetch the scheduled jobs from the database
        List<FileProcessingSchedule> jobs = jobRepo.findByStatusAndScheduledAtBefore("NEW", Timestamp.from(Instant.now()));

        // Step 3: Iterate through the jobs and run them
        for (FileProcessingSchedule job : jobs) {
            try {
                if (localModeEnabled) {
                    // Call the local runner directly
                    localRunner.runCompletenessCheck(job.getTargetDirectoryPath());
                } else {
                    // Submit the job to Livy
                    submitLivyJob(job);
                }
                
                // Update the job status in the database
                job.setStatus("SCHEDULED");
                jobRepo.save(job);
            } catch (Exception e) {
                // Handle submission error or local execution error
                // Log the exception and update the status to 'FAILED'
            }
        }
    }

    private void submitLivyJob(FileProcessingSchedule job) throws Exception {
        // ... (existing Livy submission code)
    }
}
