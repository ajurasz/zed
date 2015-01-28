package zed.panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zed.panel.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
