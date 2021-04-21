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

    private Boolean hasReadme;

    public FileChecks(Long id, Boolean hasReadme) {
        this.id = id;
        this.hasReadme = hasReadme;
    }

    protected FileChecks() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getHasReadme() {
        return hasReadme;
    }

    public void setHasReadme(Boolean hasReadme) {
        this.hasReadme = hasReadme;
    }
}
