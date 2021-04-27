package amosproj.server.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ProjectRepository extends CrudRepository<Project, Long> {
    Project findByUrl(String url);
    Project findByProjectId(Integer id);

    @Query("SELECT id, name, url, projectId, gitlabInstance FROM Project")
    Iterable<Object> selectAll();
}
