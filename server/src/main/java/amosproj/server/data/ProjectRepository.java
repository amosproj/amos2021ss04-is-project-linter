package amosproj.server.data;

import amosproj.server.Config;
import amosproj.server.api.schemas.ProjectSchema;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Das ProjectRepository ist das Interface zur Datenbank für die Projects.
 * Hier werden die Datenbank-Queries ausgeführt.
 * Der Name der Methoden ist die SQL query.
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

    /**
     * @param url URL zum GitLab repo.
     * @return das erste Project welches zu url gehört
     */
    Project findFirstByUrl(String url);

    /**
     * @param Id ID des GitLab Projektes (nicht Id des JPA Objekts)
     * @return das erste Project welches zu Id gehört
     */
    Project findFirstByGitlabProjectId(Integer Id);

    Iterable<Project> findAllByNameContainsIgnoreCaseOrNameSpaceContainsIgnoreCase(String name, String nameSpace);

    @Cacheable(value = "cachedProjects")
    default List<ProjectSchema> cachedProjects(String name, Boolean delta, List<String> allProperties) {
        System.out.println(name + "   " + name.hashCode());
        System.out.println(delta + "   " + delta.hashCode());
        System.out.println(allProperties + "   " + allProperties.hashCode());

        // get projects (matching name if present)
        Iterable<Project> projectList;
        if (name.equals("")) {
            projectList = this.findAll();
        } else {
            projectList = this.findAllByNameContainsIgnoreCaseOrNameSpaceContainsIgnoreCase(name, name);
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

    default HashMap<String, Long> checksPassedByTags(List<CheckResult> checkResults) {
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
