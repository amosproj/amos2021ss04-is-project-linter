package amosproj.server.api;

import amosproj.server.Config;
import amosproj.server.api.schemas.CrawlerStatusSchema;
import amosproj.server.api.schemas.ProjectSchema;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Crawler;
import amosproj.server.linter.Linter;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
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
    private ProjectRepository projectRepository;

    @Autowired
    private LintingResultRepository lintingResultRepository;

    @Autowired
    private Linter linter;

    @Autowired
    private Crawler crawler;

    @Autowired
    private CSVExport csvExport;

    @GetMapping("/projects")
    public List<ProjectSchema> allProjects(@RequestParam(name = "extended", required = false) Boolean extended) {
        var projectList = projectRepository.findAll();
        var it = projectList.iterator();
        var res = new LinkedList<ProjectSchema>();
        while (it.hasNext()) {
            Project projAlt = it.next();
            ProjectSchema proj;
            if (extended != null && extended) proj = new ProjectSchema(projAlt, projAlt.getResults());
            else proj = new ProjectSchema(projAlt, new LinkedList<>());
            res.add(proj);
        }
        return res;
    }

    @GetMapping("/project/{id}/lastMonth")
    public ProjectSchema getProjectLintsLastMonth(@PathVariable("id") Long id) {
        LocalDateTime before = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime after = before.minusDays(30);
        LinkedList<LintingResult> list = lintingResultRepository.findByLintTimeBetweenAndProjectIdIs(after, before, id);
        return new ProjectSchema(projectRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found")
        ), list);
    }

    @GetMapping("/project/{id}")  // id is the project id in _our_ database
    public ProjectSchema getProject(@PathVariable("id") Long id) {
        LinkedList<LintingResult> list = new LinkedList<>();
        list.add(lintingResultRepository.findFirstByProjectIdOrderByLintTimeDesc(id));
        return new ProjectSchema(projectRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found")
        ), list);
    }

    /**
     * API endpoint der eine CSV mit allen results returned
     *
     * @param response
     * @throws Exception
     */
    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws Exception {
        //set file name and content type
        String filename = "results.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        // csv
        try {
            csvExport.exportResults(response.getWriter());
        } catch (IOException e) {
            response.getWriter().flush();
            response.getWriter().write(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        response.getWriter().close();
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

    @PostMapping("/crawler")
    public ResponseEntity<String> crawl() {
        if (!crawler.getCrawlerActive()) {
            crawler.runCrawler();
            return new ResponseEntity("ok", HttpStatus.OK);
        } else {
            return new ResponseEntity("Crawler is already running, slow down!", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/crawler")
    public CrawlerStatusSchema statusCrawler() {
        return crawler.crawlerStatus();
    }

    @GetMapping("/config")
    public JsonNode sendConfig() {
        return Config.getConfigNode();
    }

}
