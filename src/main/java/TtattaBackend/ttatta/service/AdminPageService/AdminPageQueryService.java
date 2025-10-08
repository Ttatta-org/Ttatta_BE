package TtattaBackend.ttatta.service.AdminPageService;

import TtattaBackend.ttatta.domain.LocationLogs;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface AdminPageQueryService {
    Page<LocationLogs> search(int page, int size, String keyword, LocalDate fromDate, LocalDate toDate);
}
