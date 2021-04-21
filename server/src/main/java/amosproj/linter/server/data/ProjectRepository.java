package amosproj.linter.server.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "project", path = "project")
public interface ProjectRepository extends CrudRepository<Project, Long> {
    Project findById(long id);
}
