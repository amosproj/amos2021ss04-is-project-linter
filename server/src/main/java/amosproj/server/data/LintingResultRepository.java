package amosproj.server.data;

import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Das LintingResultRepository ist das Interface zur Datenbank für die LintingResults.
 */
public interface LintingResultRepository extends CrudRepository<LintingResult, Long> {
    LintingResult findFirstByProjectIdOrderByLintTimeDesc(Long projectId);
    List<LintingResult> findByLintTimeBetweenAndProjectIdIs(LocalDateTime start, LocalDateTime end, Long projectId);
}
