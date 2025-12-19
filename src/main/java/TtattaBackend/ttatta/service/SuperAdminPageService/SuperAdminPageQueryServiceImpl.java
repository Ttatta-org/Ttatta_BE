package TtattaBackend.ttatta.service.SuperAdminPageService;

import TtattaBackend.ttatta.domain.LocationAccessLogs;
import TtattaBackend.ttatta.repository.LocationAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SuperAdminPageQueryServiceImpl implements SuperAdminPageQueryService{

    private final LocationAccessLogRepository locationAccessLogRepository;

    @Override
    public Page<LocationAccessLogs> search(int page, int size, String keyword, LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        LocalDateTime safeMin = LocalDateTime.of(1000, 1, 1, 0, 0);
        LocalDateTime safeMax = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

        LocalDateTime from = (fromDate != null) ? fromDate.atStartOfDay() : safeMin;
        LocalDateTime toEx = (toDate != null) ? toDate.plusDays(1).atStartOfDay() : safeMax;

        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : "";

        return locationAccessLogRepository.searchWithKeywordAndDate(
                pageable, from, toEx, kw
        );
    }
}
