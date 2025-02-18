package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
}
