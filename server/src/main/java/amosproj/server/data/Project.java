package amosproj.server.data;

import org.aspectj.weaver.Lint;

import javax.persistence.*;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;

    @OneToMany(targetEntity = LintingResult.class)
    private List<LintingResult> results;

    protected Project() {
    } // only for JPA, dont use directly!

    public Project(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Long getId() {
        return id;
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

    public List<LintingResult> getResults() {
        return results;
    }

    public void setResults(List<LintingResult> results) {
        this.results = results;
    }
}
