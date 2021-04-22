package amosproj.linter.server.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FileChecks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private Boolean hasReadme;
    private Boolean hasContributing;
    private Boolean hasMaintainers;
    private Boolean hasOwners;

    public FileChecks(Long projectId, Boolean hasReadme, Boolean hasContributing, Boolean hasMaintainers, Boolean hasOwners) {
        this.projectId = projectId;
        this.hasReadme = hasReadme;
        this.hasContributing = hasContributing;
        this.hasMaintainers = hasMaintainers;
        this.hasOwners = hasOwners;
    }

    protected FileChecks() { }

    @Override
    public String toString() {
        return "FileChecks{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", hasReadme=" + hasReadme +
                ", hasContributing=" + hasContributing +
                ", hasMaintainers=" + hasMaintainers +
                ", hasOwners=" + hasOwners +
                '}';
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Boolean getHasReadme() {
        return hasReadme;
    }

    public void setHasReadme(Boolean hasReadme) {
        this.hasReadme = hasReadme;
    }

    public Boolean getHasContributing() {
        return hasContributing;
    }

    public void setHasContributing(Boolean hasContributing) {
        this.hasContributing = hasContributing;
    }

    public Boolean getHasMaintainers() {
        return hasMaintainers;
    }

    public void setHasMaintainers(Boolean hasMaintainers) {
        this.hasMaintainers = hasMaintainers;
    }

    public Boolean getHasOwners() {
        return hasOwners;
    }

    public void setHasOwners(Boolean hasOwners) {
        this.hasOwners = hasOwners;
    }

}
