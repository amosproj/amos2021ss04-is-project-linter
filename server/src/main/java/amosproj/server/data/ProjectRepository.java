package amosproj.server.data;

import org.springframework.data.repository.CrudRepository;


public interface ProjectRepository extends CrudRepository<Project, Long> {
    Project findByUrl(String url);

    Project findByGitlabProjectId(Integer id);
}
