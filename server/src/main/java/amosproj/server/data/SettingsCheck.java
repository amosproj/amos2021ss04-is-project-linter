package amosproj.server.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SettingsCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long lintId;
    private Boolean isPublic;
    private Boolean hasRequestAccess;
    private Boolean usesGuestRole;
    private Boolean usesDeveloperRole;
    private Boolean usesGitLabPages;

    public SettingsCheck(LintingResult lintingResults, Boolean isPublic, Boolean hasRequestAccess, Boolean usesGuestRole,
                         Boolean usesDeveloperRole, Boolean usesGitLabPages){
        this.lintId = lintingResults.getId();
        this.isPublic = isPublic;
        this.hasRequestAccess = hasRequestAccess;
        this.usesGuestRole = usesGuestRole;
        this.usesDeveloperRole = usesDeveloperRole;
        this.usesGitLabPages = usesGitLabPages;
    }

    public SettingsCheck(LintingResult lintingResults, boolean setAllTo) {
        // method for easy creating "empty" ojects;
        this.lintId = lintingResults.getId();
        if(setAllTo) {
            this.isPublic = true;
            this.hasRequestAccess = true;
            this.usesGuestRole = true;
            this.usesDeveloperRole = true;
            this.usesGitLabPages = true;
        } else {
            this.isPublic = false;
            this.hasRequestAccess = false;
            this.usesGuestRole = false;
            this.usesDeveloperRole = false;
            this.usesGitLabPages = false;
        }
    }

    protected SettingsCheck() { }

    @Override
    public String toString() {
        return "SettingChecks{" +
                "id=" + id +
                ", lintId=" + lintId +
                ", isPublic=" + isPublic +
                ", hasRequestAccess=" + hasRequestAccess +
                ", usesGuestRole=" + usesGuestRole +
                ", usesDeveloperRole=" + usesDeveloperRole +
                ", usesGitLabPages=" + usesGitLabPages +
                '}';
    }

    public Long getId() {
        return id;
    }

    public Long getLintId() {
        return lintId;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getHasRequestAccess() {
        return hasRequestAccess;
    }

    public void setHasRequestAccess(Boolean hasRequestAccess) {
        this.hasRequestAccess = hasRequestAccess;
    }

    public Boolean getUsesGuestRole() {
        return usesGuestRole;
    }

    public void setUsesGuestRole(Boolean usesGuestRole) {
        this.usesGuestRole = usesGuestRole;
    }

    public Boolean getUsesDeveloperRole() {
        return usesDeveloperRole;
    }

    public void setUsesDeveloperRole(Boolean usesDeveloperRole) {
        this.usesDeveloperRole = usesDeveloperRole;
    }

    public Boolean getUsesGitLabPages() {
        return usesGitLabPages;
    }

    public void setUsesGitLabPages(Boolean usesGitLabPages) {
        this.usesGitLabPages = usesGitLabPages;
    }
}
