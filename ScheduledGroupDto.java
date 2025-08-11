import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ScheduledGroupDto {
    private String targetDirectoryPath;
    private LocalDateTime latestLastModified;
    private String allFilePaths;
}
