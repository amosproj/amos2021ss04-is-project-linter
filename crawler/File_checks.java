import javax.persitance.Entity;
import javax.persitance.GeneratedValue;
import javax.persitance.Id;
import javax.persitance.Table;

@Enitiy
@Table(name = "File_checks")
public class File_checks {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE);
    private Long id;

    private Boolean has_readme;

    public Long getId() {
        return id;
    }

    public Boolean getHas_readme() {
        return has_readme;
    }

    public void setHas_readme(Boolean has_readme) {
        this.has_readme = has_readme;
    }
}
