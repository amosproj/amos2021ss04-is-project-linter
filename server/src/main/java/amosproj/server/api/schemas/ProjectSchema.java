package amosproj.server.api.schemas;

import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Dies ist das Schema Objekt, welches von der API an das Frontend gesendet wird.
 * ProjectSchema kann benutzt werden, um die Projects aus der Datenbank zu dekorieren mit Daten aus der config-Datei.
 * Damit kann Redundanz in der Datenbank umgangen werden.
 */
public class ProjectSchema {
    // core attributes
    private Long Id;
    private String name;
    private String url;
    private Integer gitlabProjectId;
    private String nameSpace;
    private String description;
    private Integer forkCount;
    private LocalDateTime lastCommit;
    // relations
    private List<LintingResultSchema> lintingResults;
    // extra info
    private HashMap<String, Long> latestPassedByTag = new HashMap<>();
    private HashMap<String, Long> passedByTag30DaysAgo = new HashMap<>();
    private int latestPassedTotal;
    private int delta;

    public ProjectSchema(Project proj, List<LintingResult> lintingResults) {
        BeanUtils.copyProperties(proj, this);
        if (description == null) {
            description = "";
        }
        this.lintingResults = new LinkedList<>();
        for (LintingResult lr : lintingResults) {
            this.lintingResults.add(new LintingResultSchema(lr));
        }
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getGitlabProjectId() {
        return gitlabProjectId;
    }

    public void setGitlabProjectId(Integer gitlabProjectId) {
        this.gitlabProjectId = gitlabProjectId;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getForkCount() {
        return forkCount;
    }

    public void setForkCount(Integer forkCount) {
        this.forkCount = forkCount;
    }

    public LocalDateTime getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(LocalDateTime lastCommit) {
        this.lastCommit = lastCommit;
    }

    public List<LintingResultSchema> getLintingResults() {
        return lintingResults;
    }

    public void setLintingResults(List<LintingResultSchema> lintingResults) {
        this.lintingResults = lintingResults;
    }

    public HashMap<String, Long> getLatestPassedByTag() {
        return latestPassedByTag;
    }

    public void setLatestPassedByTag(HashMap<String, Long> latestPassedByTag) {
        this.latestPassedByTag = latestPassedByTag;
    }

    public HashMap<String, Long> getPassedByTag30DaysAgo() {
        return passedByTag30DaysAgo;
    }

    public void setPassedByTag30DaysAgo(HashMap<String, Long> passedByTag30DaysAgo) {
        this.passedByTag30DaysAgo = passedByTag30DaysAgo;
    }

    public int getLatestPassedTotal() {
        return latestPassedTotal;
    }

    public void setLatestPassedTotal(int latestPassedTotal) {
        this.latestPassedTotal = latestPassedTotal;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
