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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<ProjectSchema> allProjects(@RequestParam(name = "extended", required = false) Boolean extended,
                                           @RequestParam(name = "tag", required = false) String tag,
                                           Pageable pageable) {
        HashMap<String, String> map = Config.getTags();
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
            res.sort( // Sort the resulting List according to the tag, if queried
                Comparator.comparingInt(x ->
                    x.getLintingResults().size() == 0 ? 0 : // If there are no LintingResults, display last
                        checksPassedByTag(x.getLintingResults() // Use Helper function
                            .get(x.getLintingResults().size() - 1)// access the latest LintingResult
                            .getCheckResults() // get the CheckResults from the latest LintingResult and pass to helper
                            , tag // Pass the tag so it can be counted by the helper function
                        )
                )
            );
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), res.size());

        Page<ProjectSchema> page = new PageImpl<ProjectSchema>(res.subList(start, end), pageable, res.size());
        return page;
    }

    /**
     * API Endpoint, der alle Projekte durch geht und zählt, wie viele davon alle Checks bestanden haben
     *
     * @return TreeMap, die, sortiert nach lintTime, die Anzahl der Projekte mit bestandenen checks ausgibt
     */
    @GetMapping("/projects/allTags")
    public TreeMap<LocalDateTime, HashMap<String, Object>> projectsByAllTags(@RequestParam(name = "type") String type) {
        if (type == null) {
            return null;
        }
        if (!type.equals("absolute") && !type.equals("percentage")) { // Not a valid type
            return null;
        }
        HashMap<String, String> map = Config.getTags();

        var projectList = projectRepository.findAll();
        var it = projectList.iterator();
        var res = new TreeMap<LocalDateTime, HashMap<String, Object>>();

        while (it.hasNext()) {
            Project project = it.next();
            List<LintingResult> lintingResults = project.getResults();
            for (LintingResult lr : lintingResults) {
                List<CheckResult> checkResults = lr.getCheckResults();
                var allChecksPassed = new HashMap<String, Boolean>();
                for (CheckResult checkResult : checkResults) {
                    String checkCategory = map.getOrDefault(checkResult.getCheckName(), null);
                    if (checkCategory == null) { // Check was deleted from config file
                        continue;
                    }
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
                res.putIfAbsent(lr.getLintTime(), new HashMap<String, Object>());
                for (String category : categories) {
                    HashMap<String, Object> resMap = res.get(lr.getLintTime());
                    resMap.putIfAbsent(category, 0L);
                    if (allChecksPassed.get(category)) {
                        resMap.computeIfPresent(category, (key, value) -> (Long) value + 1L);
                    }
                }
            }
        }
        if (type.equals("absolute")) { // Only the absolute value is wanted
            return res;
        }
        // Percentage is wanted: Need to divide the total by number of projects
        Set<LocalDateTime> timeSet = res.keySet();
        var percentage = new TreeMap<LocalDateTime, HashMap<String, Object>>();
        for (LocalDateTime localDateTime: timeSet) {
            int projects = lintingResultRepository.countLintingResultsByLintTime(localDateTime);
            var tagPercentages = new HashMap<String, Object>();
            var totals = res.get(localDateTime);
            for (String string : totals.keySet()) {
                tagPercentages.put(string, ((Long) totals.get(string) / (float) projects) * 100.0);
            }
            percentage.put(localDateTime, tagPercentages);
        }
        return percentage;
    }

    @GetMapping("/projects/top")
    public TreeMap<LocalDateTime, TreeMap<Long, Object>> topXProjects(@RequestParam(name = "type") String type){
        if (type == null)
            return null;

        if (!type.equals("absolute") && !type.equals("percentage")) { // Not a valid type
            return null;
        }

        var projects = projectRepository.findAll();
        var it = projects.iterator();
        var priorities = Config.getPriorities();

        JsonNode node = Config.getConfigNode().get("settings").get("mostImportantChecks");

        var res = new TreeMap<LocalDateTime, TreeMap<Long, Object>>();

        while (it.hasNext()) {
            Project project = it.next();
            List<LintingResult> lintingResults = project.getResults();
            for (LintingResult lr : lintingResults) {
                res.putIfAbsent(lr.getLintTime(), new TreeMap<>());
                List<CheckResult> checkResults = lr.getCheckResults();
                var checksPassedByPrio = new TreeMap<Long, Long>();

                var nodeIterator = node.iterator();
                while (nodeIterator.hasNext()) {
                    Long l = nodeIterator.next().asLong();
                    checksPassedByPrio.putIfAbsent(l, 0L);
                }

                Set<Long> keySet = checksPassedByPrio.keySet();
                for (CheckResult checkResult : checkResults) {
                    Long priority = priorities.getOrDefault(checkResult.getCheckName(), Long.MAX_VALUE);
                    if (checkResult.getResult()) { // Did pass the check
                        for (Long key : keySet) {
                            if (key >= priority) {
                                checksPassedByPrio.compute(key, (k,v) -> (Long) v + 1);
                            }
                        }
                    }
                }

                res.putIfAbsent(lr.getLintTime(), new TreeMap<Long, Object>());
                for (Long key: keySet) {
                    TreeMap<Long, Object> resMap = res.get(lr.getLintTime());
                    resMap.putIfAbsent(key, 0L);
                    if (checksPassedByPrio.get(key) == key) // All checks passed
                        resMap.compute(key, (k,v) -> (Long) v + 1);
                }
            }
        }
        if (type.equals("absolute")) { // Only the absolute value is wanted
            return res;
        }
        // Percentage is wanted: Need to divide the total by number of projects
        Set<LocalDateTime> timeSet = res.keySet();
        var percentage = new TreeMap<LocalDateTime, TreeMap<Long, Object>>();
        for (LocalDateTime localDateTime: timeSet) {
            int projectCount = lintingResultRepository.countLintingResultsByLintTime(localDateTime);
            var tagPercentages = new TreeMap<Long, Object>();
            var totals = res.get(localDateTime);
            for (Long key : totals.keySet()) {
                tagPercentages.put(key, ((Long) totals.get(key) / (float) projectCount) * 100.0);
            }
            percentage.put(localDateTime, tagPercentages);
        }
        return percentage;
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

    private int checksPassedByTag(List<CheckResultSchema> checkResults, String tag) {
        if (checkResults == null || tag == null) {
            return 0;
        }

        for (CheckResultSchema resultSchema : checkResults) {
            if (resultSchema == null) {
                return 0;
            }
        }

        int i = 0;
        var map = Config.getTags();
        for (CheckResultSchema checkResult : checkResults) {
            String checkCategory = map.get(checkResult.getCheckName());
            if (checkResult.getResult() && checkCategory.equals(tag)) {
                i++;
            }
        }
        return -i;
    }
}
