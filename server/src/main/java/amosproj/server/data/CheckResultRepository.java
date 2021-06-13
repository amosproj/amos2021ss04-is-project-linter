package amosproj.server.data;

import org.springframework.data.repository.CrudRepository;

/**
 * Das CheckResultRepository ist das Interface zur Datenbank für die CheckResults.
 * Hier werden die Datenbank-Queries ausgeführt.
 */
public interface CheckResultRepository extends CrudRepository<CheckResult, Long> {

    CheckResult findFirstByCheckName(String checkName);
}
