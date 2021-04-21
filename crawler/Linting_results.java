import javax.persitance.Entity;
import javax.persitance.GeneratedValue;
import javax.persitance.Id;
import javax.persitance.Table;
import java.time.LocalDateTime;

@Enitiy
@Table(name = "Linting_results")
public class Linting_results {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE);
    private Long id;

    private String repo_link;

    private String maintainer;

    private LocalDateTime last_lint;

    private Long file_check_id;

    public Long getId() {
        return id;
    }

    public String getRepo_link() {
        return repo_link;
    }

    public void setRepo_link(String repo_link) {
        this.repo_link = repo_link;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public LocalDateTime getLast_lint() {
        return last_lint;
    }

    public void setLast_lint(LocalDateTime last_lint) {
        this.last_lint = last_lint;
    }

    public Long getFile_check_id() {
        return file_check_id;
    }

    public void setFile_check_id(Long file_check_id) {
        this.file_check_id = file_check_id;
    }
}
