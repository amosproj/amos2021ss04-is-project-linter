package amosproj.server.api;

import amosproj.server.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class CachingService {

    @Autowired
    private SortingService sortingService;
    
    public void repopulateCaches() {
        Set<String> tags = Config.getAllTags();
        List<Set<String>> tagsPermutated = allTagSets(tags);
        sortingService.updateTopXProjects("absolute");
        sortingService.updateTopXProjects("percentage");
        sortingService.updateProjectsByAllTags("percentage");
        sortingService.updateProjectsByAllTags("absolute");
        for (Set<String> tag : tagsPermutated) {
            sortingService.updateCachedSorting(true, tag);
            sortingService.updateCachedSorting(false, tag);
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
