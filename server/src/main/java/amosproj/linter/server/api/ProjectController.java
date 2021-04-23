package amosproj.linter.server.api;

import amosproj.linter.server.data.Project;
import amosproj.linter.server.data.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @GetMapping("/projects")
    public Iterable<Project> allProjects() {
        return repository.findAll();
    }

    @GetMapping("/project/{id}")
    public Project getProject(@PathVariable("id") Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found")
        );
    }

}
