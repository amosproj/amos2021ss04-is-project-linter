package amosproj.server.api.schemas;

import org.gitlab4j.api.utils.JacksonJson;

import java.time.LocalDateTime;

public class CrawlerStatusSchema {
    private String status;
    private String lastError;
    private LocalDateTime errorTime;
    private Boolean crawlerActive;
    private Long size;
    private Long lintingProgress;
    private Long lintingTime;

    public CrawlerStatusSchema(String status, String lastError, LocalDateTime errorTime, Boolean crawlerActive, Long size, Long lintingProgress, Long lintingTime) {
        this.status = status;
        this.lastError = lastError;
        this.errorTime = errorTime;
        this.crawlerActive = crawlerActive;
        this.size = size;
        this.lintingProgress = lintingProgress;
        this.lintingTime = lintingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Boolean getCrawlerActive() {
        return crawlerActive;
    }

    public void setCrawlerActive(Boolean crawlerActive) {
        this.crawlerActive = crawlerActive;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getLintingProgress() {
        return lintingProgress;
    }

    public void setLintingProgress(Long lintingProgress) {
        this.lintingProgress = lintingProgress;
    }

    public Long getLintingTime() {
        return lintingTime;
    }

    public void setLintingTime(Long lintingTime) {
        this.lintingTime = lintingTime;
    }

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }
}
