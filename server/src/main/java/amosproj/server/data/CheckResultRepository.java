package amosproj.server.data;

import org.springframework.data.repository.CrudRepository;

/**
 * Das CheckResultRepository ist das Interface zur Datenbank für die CheckResults.
 */
public interface CheckResultRepository extends CrudRepository<CheckResult, Long> {
}
