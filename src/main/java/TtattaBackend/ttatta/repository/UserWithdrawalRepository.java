package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.UsersWithdrawals;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWithdrawalRepository extends JpaRepository<UsersWithdrawals, Long> {
}