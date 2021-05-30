package amosproj.server.api;

import amosproj.server.api.schemas.CrawlerStatusSchema;
import amosproj.server.api.schemas.ProjectSchema;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Crawler;
import amosproj.server.linter.Linter;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Der ProjectController ist die API-Schnittstelle nach außen.
 * <p>
 * Die Dokumentation der API-Endpoints finden Sie in der
 * <a href="../../../api.yaml">api.yaml</a>
 */
@RestController
@CrossOrigin(origins = "*") // origins, methods, allowedHeaders, exposedHeaders, allowCredentials, maxAge
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private LintingResultRepository lintingResultRepository;

    @Autowired
    private Linter linter;

    @Autowired
    private Crawler crawler;

    @GetMapping("/projects")
    public List<ProjectSchema> allProjects() {
        var projectList = repository.findAll();
        var it = projectList.iterator();
        var res = new LinkedList<ProjectSchema>();
        while (it.hasNext()) {
            Project projAlt = it.next();
            ProjectSchema proj = new ProjectSchema(projAlt, new LinkedList<>());
            res.add(proj);
        }
        return res;
    }

    @GetMapping("/project/{id}/lastMonth")
    public  ProjectSchema getProjectLintsLastMonth(@PathVariable("id") Long id) {
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime after = before.minusDays(30);
        LinkedList<LintingResult> list = lintingResultRepository.findByLintTimeBetweenAndProjectIdIs(after, before, id);
        return new ProjectSchema(repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found")
        ), list);
    }

    @GetMapping("/project/{id}")  // id is the project id in _our_ database
    public ProjectSchema getProject(@PathVariable("id") Long id) {
        LinkedList<LintingResult> list = new LinkedList<>();
        list.add(lintingResultRepository.findFirstByProjectIdOrderByLintTimeDesc(id));
        return new ProjectSchema(repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found")
        ), list);
    }

    @PostMapping("/projects")
    public @ResponseBody
    String lintProject(@RequestBody String url) {
        try {
            linter.runLint(url);
            return "OK";
        } catch (GitLabApiException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "gitlab repo doesn't exist");
        }
    }

    @GetMapping("/lintAll")
    public void lint() {
        crawler.runCrawler();
    }

    @GetMapping("/crawler/status")
    public CrawlerStatusSchema statusCrawler() {
        return crawler.crawlerStatus();
    }

}
