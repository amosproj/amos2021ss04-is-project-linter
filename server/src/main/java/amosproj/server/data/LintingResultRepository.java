package amosproj.server.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Das LintingResultRepository ist das Interface zur Datenbank für die LintingResults.
 * Hier werden die Datenbank-Queries ausgeführt.
 * Der Name der Methoden ist die SQL query.
 */
public interface LintingResultRepository extends CrudRepository<LintingResult, Long> {

    /**
     * @param projectId Foreign-Key für das zugehörige Project
     * @return das neueste LintingResult
     */
    LintingResult findFirstByProjectIdOrderByLintTimeDesc(Long projectId);

    List<LintingResult> findAllByOrderByLintTimeAsc();

    Integer countLintingResultsByLintTime(LocalDateTime localDateTime);

    @Query("SELECT l FROM LintingResult l JOIN FETCH l.checkResults")
    Iterable<LintingResult> findAll();
}
