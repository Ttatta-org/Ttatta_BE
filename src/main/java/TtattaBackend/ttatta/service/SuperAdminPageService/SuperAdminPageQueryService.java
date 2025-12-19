package TtattaBackend.ttatta.service.SuperAdminPageService;

import TtattaBackend.ttatta.domain.LocationAccessLogs;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface SuperAdminPageQueryService {
    Page<LocationAccessLogs> search(int page, int size, String keyword, LocalDate fromDate, LocalDate toDate);
}
