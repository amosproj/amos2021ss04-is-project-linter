package main.java.amosproj.linter.server.data;

import main.java.amosproj.linter.server.data.LintingResults;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FileChecks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long lintId;
    private String fileName;
    private Boolean exists;

    public FileChecks(LintingResults lintingResults, String fileName, Boolean exists) {
        this.lintId = lintingResults.getId();
        this.fileName = fileName;
        this.exists = exists;
    }

    protected FileChecks() { }

    @Override
    public String toString() {
        return "FileChecks{" +
                "id=" + id +
                ", lintId=" + lintId +
                ", fileName=" + fileName +
                ", exists=" + exists +
                '}';
    }

    public Long getId() {
        return id;
    }

    public Long getLintId() {
        return lintId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }
}
