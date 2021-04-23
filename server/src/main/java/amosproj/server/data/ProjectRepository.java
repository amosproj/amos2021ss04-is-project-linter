package amosproj.server.data;

import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    Project findById(long id);
    Project findByUrl(String url);
}
