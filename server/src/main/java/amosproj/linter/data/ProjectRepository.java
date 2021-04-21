package amosproj.linter.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findByLastName(String lastName);

    Project findById(long id);
}
