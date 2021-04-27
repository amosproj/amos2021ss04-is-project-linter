package amosproj.server.api;

import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Linter;
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

    @Autowired
    private Linter linter;

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

    @GetMapping("/result")
    public LintingResult getLintResult() {
        return linter.getOrCreateResult("https://gitlab.com/altaway/herbstluftwm");
    }

}
