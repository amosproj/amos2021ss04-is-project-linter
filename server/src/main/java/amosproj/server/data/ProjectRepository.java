package amosproj.server.data;

import org.springframework.data.repository.CrudRepository;

/**
 * Das ProjectRepository ist das Interface zur Datenbank für die Projects.
 */
public interface ProjectRepository extends CrudRepository<Project, Long> {
    Project findByUrl(String url);

    Project findByGitlabProjectId(Integer Id);
}
