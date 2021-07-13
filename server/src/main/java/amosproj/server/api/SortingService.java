package amosproj.server.api;

import amosproj.server.Config;
import amosproj.server.api.schemas.ProjectSchema;
import amosproj.server.data.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SortingService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LintingResultRepository lintingResultRepository;

    @CachePut("cachedSorting")
    public List<ProjectSchema> updateCachedSorting(Boolean delta, Set<String> allProperties) {
        return cachedSorting(delta, allProperties);
    }


    @Cacheable(value = "cachedSorting")
    public List<ProjectSchema> cachedSorting(Boolean delta, Set<String> allProperties) {
        Iterable<Project> projectList = projectRepository.findAll();
        // calculate properties required for sorting
        LocalDateTime localDateTime = LocalDateTime.now(Clock.systemUTC());
        LinkedList<ProjectSchema> res = new LinkedList<>();
        for (Project projAlt : projectList) {
            // create schema
            ProjectSchema proj = new ProjectSchema(projAlt, new LinkedList<>());
            // get results
            List<LintingResult> lr = projAlt.getResults();
            if (!lr.isEmpty()) {
                // TODO performance
                if (delta) {
                    LintingResult lrs = null;
                    for (int idx = lr.size() - 1; idx >= 0; idx--) {
                        LintingResult lintingResult = lr.get(idx);
                        if (lintingResult.getLintTime().isAfter(localDateTime.minusDays(31))) {
                            lrs = lintingResult; // Found a LintingResult that's at least 30 days old
                        } else {
                            break; // No LintingResult is younger than 30 days any more
                        }
                    }
                    if (lrs != null) {
                        proj.setPassedByTag30DaysAgo(checksPassedByTags(lrs.getCheckResults()));
                    }
                }
                proj.setLatestPassedByTag(checksPassedByTags(lr.get(lr.size() - 1).getCheckResults()));

                int allRequestedProperties = 0;
                int allRequested30DaysAgo = 0;
                HashMap<String, Long> latest = proj.getLatestPassedByTag();
                HashMap<String, Long> oldest = proj.getPassedByTag30DaysAgo();
                for (String property : allProperties) {
                    allRequestedProperties += latest.getOrDefault(property, 0L);
                    allRequested30DaysAgo += oldest.getOrDefault(property, 0L);
                }
                proj.setLatestPassedTotal(allRequestedProperties);
                proj.setDelta(allRequestedProperties - allRequested30DaysAgo);
                // ~~~~ TODO performance
            }
            res.add(proj);
        }
        // Sort by checks passed in tag
        if (!delta) {
            res.sort(Comparator.comparingInt(x -> -x.getLatestPassedTotal()));
        } else {
            res.sort(Comparator.comparingInt(x -> -x.getDelta()));
        }
        return res;
    }

    private HashMap<String, Long> checksPassedByTags(List<CheckResult> checkResults) {
        if (checkResults == null) {
            return new HashMap<>();
        }
        HashMap<String, String> map = Config.getTags();
        HashMap<String, Long> res = new HashMap<>();
        for (CheckResult checkResult : checkResults) {
            if (checkResult == null) {
                return new HashMap<>();
            }
            String checkCategory = map.get(checkResult.getCheckName());
            if (checkResult.getResult()) {
                res.putIfAbsent(checkCategory, 0L);
                res.compute(checkCategory, (key, value) -> value + 1L);
            }
        }
        return res;
    }

    public boolean searchFilter(ProjectSchema s, String query) {
        boolean matchesNamespace = s.getNameSpace() != null && s.getNameSpace().toLowerCase().contains(query.toLowerCase());
        boolean matchesName = s.getName() != null && s.getName().toLowerCase().contains(query.toLowerCase());
        return matchesNamespace || matchesName;
    }

    @CachePut("allTags")
    public TreeMap<LocalDateTime, HashMap<String, Object>> updateProjectsByAllTags(String type) {
        return projectsByAllTags(type);
    }


    @Cacheable("allTags")
    public TreeMap<LocalDateTime, HashMap<String, Object>> projectsByAllTags(String type) {
        if (type == null) {
            return null;
        }
        if (!type.equals("absolute") && !type.equals("percentage")) { // Not a valid type
            return null;
        }
        HashMap<String, String> map = Config.getTags();
        Set<String> tags = Config.getAllTags();

        Iterable<Project> projectList = projectRepository.findAll();
        Iterator<Project> it = projectList.iterator();
        TreeMap<LocalDateTime, HashMap<String, Object>> res = new TreeMap<>();

        while (it.hasNext()) {
            Project project = it.next();
            List<LintingResult> lintingResults = project.getResults();
            for (LintingResult lr : lintingResults) {
                List<CheckResult> checkResults = lr.getCheckResults();
                HashMap<String, Boolean> allChecksPassed = new HashMap<String, Boolean>();
                for (String tag : tags) {
                    allChecksPassed.put(tag, true);
                }
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
                    }
                }
                res.putIfAbsent(lr.getLintTime(), new HashMap<String, Object>());
                for (String category : tags) {
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
        TreeMap<LocalDateTime, HashMap<String, Object>> percentage = new TreeMap<LocalDateTime, HashMap<String, Object>>();
        for (LocalDateTime localDateTime : timeSet) {
            int projects = lintingResultRepository.countLintingResultsByLintTime(localDateTime);
            HashMap<String, Object> tagPercentages = new HashMap<String, Object>();
            HashMap<String, Object> totals = res.get(localDateTime);
            for (String string : totals.keySet()) {
                tagPercentages.put(string, ((Long) totals.get(string) / (float) projects) * 100.0);
            }
            percentage.put(localDateTime, tagPercentages);
        }
        return percentage;
    }

    @CachePut("top")
    public TreeMap<LocalDateTime, TreeMap<Long, Object>> updateTopXProjects(String type) {
        return topXProjects(type);
    }

    @Cacheable("top")
    public TreeMap<LocalDateTime, TreeMap<Long, Object>> topXProjects(String type) {
        if (type == null)
            return null;

        if (!type.equals("absolute") && !type.equals("percentage")) { // Not a valid type
            return null;
        }

        Iterable<Project> projects = projectRepository.findAll();
        Iterator<Project> it = projects.iterator();
        HashMap<String, Long> priorities = Config.getPriorities();

        JsonNode node = Config.getConfigNode().get("settings").get("mostImportantChecks");

        TreeMap<LocalDateTime, TreeMap<Long, Object>> res = new TreeMap<LocalDateTime, TreeMap<Long, Object>>();

        while (it.hasNext()) {
            Project project = it.next();
            List<LintingResult> lintingResults = project.getResults();
            for (LintingResult lr : lintingResults) {
                res.putIfAbsent(lr.getLintTime(), new TreeMap<>());
                List<CheckResult> checkResults = lr.getCheckResults();
                TreeMap<Long, Long> checksPassedByPrio = new TreeMap<Long, Long>();

                for (JsonNode jsonNode : node) {
                    Long l = jsonNode.asLong();
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
        TreeMap<LocalDateTime, TreeMap<Long, Object>> percentage = new TreeMap<LocalDateTime, TreeMap<Long, Object>>();
        for (LocalDateTime localDateTime : timeSet) {
            int projectCount = lintingResultRepository.countLintingResultsByLintTime(localDateTime);
            TreeMap<Long, Object> tagPercentages = new TreeMap<Long, Object>();
            TreeMap<Long, Object> totals = res.get(localDateTime);
            for (Long key : totals.keySet()) {
                tagPercentages.put(key, ((Long) totals.get(key) / (float) projectCount) * 100.0);
            }
            percentage.put(localDateTime, tagPercentages);
        }
        return percentage;
    }

}
