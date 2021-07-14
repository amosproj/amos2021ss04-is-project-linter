package amosproj.server.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Das ProjectRepository ist das Interface zur Datenbank für die Projects.
 * Hier werden die Datenbank-Queries ausgeführt.
 * Der Name der Methoden ist die SQL query.
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

    /**
     * @param Id ID des GitLab Projektes (nicht Id des JPA Objekts)
     * @return das erste Project welches zu Id gehört
     */
    Project findFirstByGitlabProjectId(Integer Id);

}
