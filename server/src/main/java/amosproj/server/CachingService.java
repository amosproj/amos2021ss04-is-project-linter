package amosproj.server;

import amosproj.server.api.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CachingService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SortingService sortingService;

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    public void repopulateCaches() {
        Set<String> tags = Config.getAllTags();
        List<Set<String>> tagsPermutated = allTagSets(tags);
        sortingService.projectsByAllTags("absolute");
        sortingService.projectsByAllTags("percentage");
        sortingService.topXProjects("absolute");
        sortingService.topXProjects("percentage");
        for (Set<String> tag : tagsPermutated) {
            sortingService.cachedSorting(true, tag);
            sortingService.cachedSorting(false, tag);
        }
    }

    private static List<Set<String>> allTagSets(Set<String> tags) {
        List<Set<String>> res = new LinkedList<>();
        String[] set = tags.toArray(new String[0]);
        int n = tags.size();
        for (int i = 0; i < (1 << n); i++) {
            Set<String> p = new HashSet<>();
            // Print current subset
            for (int j = 0; j < n; j++)
                if ((i & (1 << j)) > 0)
                    p.add(set[j]);

            res.add(p);
        }
        return res;
    }

}
