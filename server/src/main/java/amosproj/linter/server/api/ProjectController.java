package amosproj.linter.server.api;

import amosproj.linter.server.data.Project;
import amosproj.linter.server.data.ProjectRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
@CrossOrigin
public class ProjectController {

    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/projects")
    public Iterable<Project> allProjects() {
        return repository.findAll();
    }

    @GetMapping("/project/{id}")
    public Project getProject(@PathParam("id") Long id) {
        var proj = repository.findById(id);
        return proj.orElse(null);
    }

}
