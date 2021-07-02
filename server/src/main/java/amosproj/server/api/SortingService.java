package amosproj.server.api;

import amosproj.server.Config;
import amosproj.server.api.schemas.ProjectSchema;
import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class SortingService {

    @Autowired
    private ProjectRepository projectRepository;

    @Cacheable(value = "cachedProjects")
    public List<ProjectSchema> cachedProjects(String name, Boolean delta, List<String> allProperties) {
        // get projects (matching name if present)
        Iterable<Project> projectList;
        if (name.equals("")) {
            projectList = projectRepository.findAll();
        } else {
            projectList = projectRepository.findAllByNameContainsIgnoreCaseOrNameSpaceContainsIgnoreCase(name, name);
        }
        // calculate properties required for sorting
        LocalDateTime localDateTime = LocalDateTime.now(Clock.systemUTC());
        // calculate sorting parameters
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
                    for (LintingResult lintingResult : lr) { // TODO go backwards to increase search speed
                        if (lintingResult.getLintTime().isAfter(localDateTime.minusDays(31))) {
                            lrs = lintingResult; // Found the first LintingResult that's at least 30 days old
                            break;
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

        int i = 0;
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

}
