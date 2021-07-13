package amosproj.server.api;

import amosproj.server.Config;
import amosproj.server.api.schemas.CrawlerStatusSchema;
import amosproj.server.api.schemas.ProjectSchema;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Crawler;
import amosproj.server.linter.Linter;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Der APIController ist die API-Schnittstelle nach außen.
 * <p>
 * Die Dokumentation der API-Endpoints finden sie im ordner docs/
 */
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST}, maxAge = 600L)
public class APIController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LintingResultRepository lintingResultRepository;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private Linter linter;

    @Autowired
    private Crawler crawler;

    @Autowired
    private CSVExport csvExport;

    @Autowired
    private CachingService cachingService;

    //**********************************
    //                GET
    //**********************************

    @GetMapping("/projects")
    public Page<ProjectSchema> allProjects(@RequestParam(name = "delta", required = false, defaultValue = "false") Boolean delta,
                                           @RequestParam(name = "name", required = false, defaultValue = "") String name,
                                           Pageable pageable) {

        // get sort criteria
        Set<String> allProperties = pageable.getSort().map(Sort.Order::getProperty).toSet();
        // get result set
        List<ProjectSchema> res = sortingService.cachedSorting(delta, allProperties);
        // name search
        if (!name.equals("")) {
            res = res.stream().filter(
                    (x) -> sortingService.searchFilter(x, name)
            ).collect(Collectors.toList());
        }
        // do pagination stuff
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), res.size());
        if (start > end) {
            return new PageImpl<>(new ArrayList<>(), pageable, res.size());
        } else {
            return new PageImpl<>(res.subList(start, end), pageable, res.size());
        }
    }

    /**
     * API Endpoint, der alle Projekte durch geht und zählt, wie viele davon alle Checks bestanden haben
     *
     * @return TreeMap, die, sortiert nach lintTime, die Anzahl der Projekte mit bestandenen checks ausgibt
     */
    @GetMapping("/projects/allTags")
    public TreeMap<LocalDateTime, HashMap<String, Object>> projectsByAllTags(@RequestParam(name = "type", required = true, defaultValue = "absolute") String type) {
        return sortingService.projectsByAllTags(type);
    }

    @GetMapping("/projects/top")
    public TreeMap<LocalDateTime, TreeMap<Long, Object>> topXProjects(@RequestParam(name = "type", required = true, defaultValue = "absolute") String type) {
        return sortingService.topXProjects(type);
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

    @PostMapping("/crawler")
    public void crawl() {
        if (!crawler.getCrawlerActive()) {
            crawler.runCrawler();
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Crawler is already running, slow down!");
        }
    }

}
