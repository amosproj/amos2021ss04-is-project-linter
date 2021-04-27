package amosproj.server.api;

import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Linter;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private Linter linter;

    @GetMapping("/projects")
    public Iterable<Project> allProjects() {
        return repository.findAll();
    }

    @GetMapping("/project/{id}")  // id is the project id in our database
    public Project getProject(@PathVariable("id") Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found")
        );
    }

    @PostMapping("/projects")
    public @ResponseBody
    String lintProject(@RequestBody String url) {
        try {
            linter.runLint(url);
        } catch (GitLabApiException e) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "gitlab repo doesen't exist").toString();
        }
        return "ok";
    }

}
