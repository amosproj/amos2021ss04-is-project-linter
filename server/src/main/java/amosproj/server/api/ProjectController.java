package amosproj.server.api;

import amosproj.server.Config;
import amosproj.server.api.schemas.CheckResultSchema;
import amosproj.server.api.schemas.CrawlerStatusSchema;
import amosproj.server.api.schemas.ProjectSchema;
import amosproj.server.data.*;
import amosproj.server.linter.Crawler;
import amosproj.server.linter.Linter;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.*;

/**
 * Der ProjectController ist die API-Schnittstelle nach außen.
 * <p>
 * Die Dokumentation der API-Endpoints finden sie im ordner docs/
 */
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST}, maxAge = 600L)
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

    //**********************************
    //                GET
    //**********************************

    @GetMapping("/projects")
    public List<ProjectSchema> allProjects(@RequestParam(name = "extended", required = false) Boolean extended,
                                           @RequestParam(name = "tag", required = false) String tag) {
        HashMap<String, String> map = getTags();
        var projectList = projectRepository.findAll();
        var it = projectList.iterator();
        var res = new LinkedList<ProjectSchema>();
        while (it.hasNext()) {
            Project projAlt = it.next();
            ProjectSchema proj;
            if (extended != null && extended) {
                LocalDateTime localDateTime = LocalDateTime.now(Clock.systemUTC());
                proj = new ProjectSchema(projAlt, lintingResultRepository.findByLintTimeBetweenAndProjectIdIs
                        (localDateTime.minusDays(30).minusMinutes(5), localDateTime, projAlt.getId()));
            } else {
                proj = new ProjectSchema(projAlt, new LinkedList<>());
            }
            res.add(proj);
        }
        // Sort by checks passed in tag
        if (extended != null && tag != null) {
            res.sort(
                Comparator.comparingInt(x ->
                    x.getLintingResults().size() == 0 ? 0 :
                    checksPassedByTag(x.getLintingResults()
                        .get(x.getLintingResults().size() - 1)
                        .getCheckResults()
                        .toArray(new CheckResultSchema[
                                x.getLintingResults()
                                        .get(x.getLintingResults().size() - 1)
                                        .getCheckResults()
                                        .size()
                                ])
                                , tag
                    )
                )
            );
        }
        return res;
    }

    /**
     * API Endpoint, der alle Projekte durch geht und zählt, wie viele davon alle Checks bestanden haben
     * @return TreeMap, die, sortiert nach lintTime, die Anzahl der Projekte mit bestandenen checks ausgibt
     */
    @GetMapping("/projects/allTags")
    public TreeMap<LocalDateTime, HashMap<String, Long>> projectsByAllTags() {
        HashMap<String, String> map = getTags();

        var projectList = projectRepository.findAll();
        var it = projectList.iterator();
        var res = new TreeMap<LocalDateTime, HashMap<String, Long>>();

        while(it.hasNext()) {
            Project project = it.next();
            List<LintingResult> lintingResults = project.getResults();
            for (LintingResult lr: lintingResults) {
                List<CheckResult> checkResults = lr.getCheckResults();
                var allChecksPassed = new HashMap<String, Boolean>();
                for (CheckResult checkResult: checkResults) {
                    String checkCategory = map.get(checkResult.getCheckName());
                    if (!checkResult.getResult()) { // Did not pass all the checks for the category
                        allChecksPassed.put(checkCategory, false);
                        if (allChecksPassed.values().stream().allMatch(x -> x.equals(false))) { //No category passed all checks
                            break;
                        }
                    } else {
                        allChecksPassed.putIfAbsent(checkCategory, true); // So far all checks have passed in this category
                    }
                }
                var categories = allChecksPassed.keySet();
                res.putIfAbsent(lr.getLintTime(), new HashMap<String, Long>());
                for (String category: categories) {
                    if (allChecksPassed.get(category)) {
                        HashMap<String, Long> resMap = res.get(lr.getLintTime());
                        resMap.putIfAbsent(category, 0L);
                        resMap.computeIfPresent(category, (key, value) -> value+1L);
                    }
                }
            }
        }
        return res;
    }

    @GetMapping("/projects/byTag")
    public TreeMap<LocalDateTime, Long> projectsPassedByCategory(@RequestParam(name = "tag", required = false) String category) {
        HashMap<String, String> map = getTags();

        var projectList = projectRepository.findAll();
        var it = projectList.iterator();

        var res = new TreeMap<LocalDateTime, Long>();

        while(it.hasNext()) {
            Project project = it.next();
            List<LintingResult> lintingResults = project.getResults();
            for (LintingResult lr: lintingResults) {
                List<CheckResult> checkResults = lr.getCheckResults();
                boolean allChecksPassed = true;
                for (CheckResult checkResult: checkResults) {
                    String checkCategory = map.get(checkResult.getCheckName());
                    if (!checkCategory.equals(category) || !checkResult.getResult()) { // Did not pass all the checks
                        allChecksPassed = false;
                        break;
                    }
                }
                res.putIfAbsent(lr.getLintTime(), 0L);
                if (allChecksPassed) {
                    res.computeIfPresent(lr.getLintTime(), (key, val) -> val + 1);
                }
            }
        }
        return res;
    }

    @GetMapping("/crawler")
    public CrawlerStatusSchema statusCrawler() {
        return crawler.crawlerStatus();
    }

    @GetMapping("/config")
    public JsonNode sendConfig() {
        return Config.getConfigNode();
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

    //**********************************
    //                POST
    //**********************************

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

    //**********************
    //         Helper
    //**********************

    private HashMap<String, String> getTags() {
        HashMap<String, String> map = new HashMap<>();
        JsonNode node = Config.getConfigNode().get("checks");
        Iterator<String> iterator = node.fieldNames();
        while (iterator.hasNext()) {
            String checkName = iterator.next();
            String checkCategory = node.get(checkName).get("tag").asText();
            map.put(checkName, checkCategory);
        }
        return map;
    }

    private int checksPassedByTag(CheckResultSchema checkResults[], String tag) {
        if (checkResults == null || tag == null) {
            return 0;
        }

        for (CheckResultSchema resultSchema: checkResults) {
            if (resultSchema == null) {
                return 0;
            }
        }

        int i = 0;
        var map = getTags();
        for (CheckResultSchema checkResult: checkResults) {
            String checkCategory = map.get(checkResult.getCheckName());
            if (checkResult.getResult() && checkCategory.equals(tag)) {
                i++;
            }
        }
        return -i;
    }
}
