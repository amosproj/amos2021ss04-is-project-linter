package amosproj.server.api;

import amosproj.server.Config;
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
import org.springframework.data.domain.Sort;
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
    public Page<ProjectSchema> allProjects(@RequestParam(name = "delta", required = false) Boolean delta,
                                           @RequestParam(name = "name", required = false) String name,
                                           Pageable pageable) {
        LinkedList<String> allProperties = new LinkedList<>();
        Iterator<Sort.Order> iterator = pageable.getSort().stream().iterator();
        while (iterator.hasNext()) {
            Sort.Order next = iterator.next();
            allProperties.add(next.getProperty());
        }

        Iterable<Project> projectList;
        if (name == null || name.equals("")) {
            projectList = projectRepository.findAll();
        } else {
            projectList = projectRepository.findAllByNameContainsIgnoreCase(name);
        }
        Iterator<Project> it = projectList.iterator();
        LinkedList<ProjectSchema> res = new LinkedList<ProjectSchema>();
        while (it.hasNext()) {
            Project projAlt = it.next();
            ProjectSchema proj = new ProjectSchema(projAlt, new LinkedList<>());;

            LocalDateTime localDateTime = LocalDateTime.now(Clock.systemUTC());
            LinkedList<LintingResult> lr = lintingResultRepository.findByLintTimeBetweenAndProjectIdIs
                    (localDateTime.minusDays(30).minusMinutes(5), localDateTime, projAlt.getId());
            int lastIdx = Math.max(lr.size() - 1, 0);
            if (lastIdx < lr.size()) { // At least one LintingResult exists
                proj.setLatestPassedByTag(checksPassedByTags(lr.get(lastIdx).getCheckResults()));
                proj.setPassedByTag30DaysAgo(checksPassedByTags(lr.get(0).getCheckResults()));
            }

            int allRequestedProperties = 0;
            int allRequested30DaysAgo = 0;
            var latest = proj.getLatestPassedByTag();
            var oldest = proj.getPassedByTag30DaysAgo();
            for (String property : allProperties) {
                allRequestedProperties += latest.getOrDefault(property, 0L);
                allRequested30DaysAgo += oldest.getOrDefault(property, 0L);
            }
            proj.setLatestPassedTotal(allRequestedProperties);
            proj.setDelta(allRequestedProperties - allRequested30DaysAgo);

            res.add(proj);
        }
        // Sort by checks passed in tag
        if (delta == null || !delta) {
            res.sort(Comparator.comparingInt(x -> -x.getLatestPassedTotal()));
        } else {
            res.sort(Comparator.comparingInt(x -> -x.getDelta()));
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), res.size());

        if (start > end) {
            return new PageImpl<>(new LinkedList<>(), pageable, res.size());
        } else {
            Page<ProjectSchema> page = new PageImpl<ProjectSchema>(res.subList(start, end), pageable, res.size());
            return page;
        }
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
        for (LocalDateTime localDateTime : timeSet) {
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
    public TreeMap<LocalDateTime, TreeMap<Long, Object>> topXProjects(@RequestParam(name = "type") String type) {
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
                                checksPassedByPrio.compute(key, (k, v) -> (Long) v + 1);
                            }
                        }
                    }
                }

                res.putIfAbsent(lr.getLintTime(), new TreeMap<Long, Object>());
                for (Long key : keySet) {
                    TreeMap<Long, Object> resMap = res.get(lr.getLintTime());
                    resMap.putIfAbsent(key, 0L);
                    if (checksPassedByPrio.get(key).equals(key)) // All checks passed
                        resMap.compute(key, (k, v) -> (Long) v + 1);
                }
            }
        }
        if (type.equals("absolute")) { // Only the absolute value is wanted
            return res;
        }
        // Percentage is wanted: Need to divide the total by number of projects
        Set<LocalDateTime> timeSet = res.keySet();
        var percentage = new TreeMap<LocalDateTime, TreeMap<Long, Object>>();
        for (LocalDateTime localDateTime : timeSet) {
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


    private HashMap<String, Long> checksPassedByTags(List<CheckResult> checkResults) {
        if (checkResults == null) {
            return new HashMap<>();
        }

        int i = 0;
        var map = Config.getTags();
        var res = new HashMap<String, Long>();
        for (CheckResult checkResult : checkResults) {
            if (checkResult == null) {
                return new HashMap<String, Long>();
            }
            String checkCategory = map.get(checkResult.getCheckName());
            if (checkResult.getResult()) {
                res.putIfAbsent(checkCategory, 0L);
                res.compute(checkCategory, (key, value) -> value + 1L);
            }
        }
        return res;
    }
}
