package amosproj.server.api;

import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Linter;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "*") // origins, methods, allowedHeaders, exposedHeaders, allowCredentials, maxAge
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private Linter linter;

    @GetMapping("/projects")
    public Iterable<Project> allProjects() {
        Iterable<Project> projectList = (List<Project>) repository.findAll();
        var it = projectList.iterator();
        while (it.hasNext()) { // FIXME
            it.next().setResults(null);
        }
        return projectList;
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
        System.out.println(url);
        try {
            linter.runLint(url);
        } catch (GitLabApiException e) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "gitlab repo doesn't exist").toString();
        }
        return "ok";
    }

}
