package TtattaBackend.ttatta.service.AdminService;

import TtattaBackend.ttatta.domain.LocationLogs;
import TtattaBackend.ttatta.repository.LocationLogRepository;
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
public class AdminPageQueryServiceImpl implements AdminPageQueryService {

    private final LocationLogRepository locationLogRepository;

    @Override
    public Page<LocationLogs> search(int page, int size, String keyword, LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime from = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime toEx = toDate != null ? toDate.plusDays(1).atStartOfDay() : LocalDateTime.MAX;
        String kw = (keyword == null) ? "" : keyword;

        return locationLogRepository.searchWithKeywordAndDate(
                pageable, from, toEx, kw
        );
    }
}
