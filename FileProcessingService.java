import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileProcessingScheduleRepository scheduleRepository;

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes (300,000 milliseconds)
    public void createProcessingSchedule() {
        // Fetch the grouped data using the custom repository query
        List<ScheduledGroupDto> scheduledGroups = fileMetadataRepository.findNewFilesGroupedWithPaths();

        // Convert DTOs to entities for the new table
        List<FileProcessingSchedule> schedulesToSave = scheduledGroups.stream()
            .map(this::createScheduleEntity)
            .collect(Collectors.toList());

        // Save the new schedule entries to the database. Spring handles the insert/update logic.
        scheduleRepository.saveAll(schedulesToSave);
    }

    private FileProcessingSchedule createScheduleEntity(ScheduledGroupDto dto) {
        FileProcessingSchedule schedule = new FileProcessingSchedule();
        schedule.setTargetDirectoryPath(dto.getTargetDirectoryPath());
        schedule.setLatestLastModified(dto.getLatestLastModified());
        schedule.setAllFilePaths(dto.getAllFilePaths());
        
        // Calculate the scheduled time
        schedule.setScheduledTime(dto.getLatestLastModified().plusHours(2));
        
        return schedule;
    }
}
