# Checks

## Neue Checks hinzufügen

Um neue Checks hinzuzufügen muss lediglich eine neue Klasse in dem Modul [Checks](../server/src/main/java/amosproj/server/linter/checks) mit dem Check-Namen als Klassennamen angelegt werden, die von `Check` erbt.
Darin muss dann die Methode `boolean evaluate(GitLab gitlab, Project project)` implementiert werden, die den Check durchführt.
Dabei sollte `true` dann zurückgegeben werden, wenn der Check erfolgreich war und das Kriterium erfüllt wurde, `false` andernfalls.

Beispieldatei:

```java
package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

public class HasBadges extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        try {
            var badgelist = gitLab.getApi().getProjectApi().getBadges(project);
            if (!badgelist.isEmpty()) {
                return true;
            }
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return false;
    }
}
```

Damit der Check ausgeführt wird, muss in der `config.json` in `checks` noch der neue Check hinzugefügt und `enabled=true` gesetzt werden (siehe [Config](Config-File.md)).

## Alle verfügbaren Checks:

Einstellungen die Standardmäßig verfügar sind: `enable`, `severity`, `tag`

| CheckName                      | Category        | Description                                                                                          | Error Message                                                                  | Fix                                                                                         |
| ------------------------------ | --------------- | ---------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------- |
| checkReadmeExistence           | file_checks     | Überprüft, ob eine README Datei existiert.                                                           | Keine README Datei gefunden!                                                   | Legen Sie eine README Datei in der Projektwurzel an.                                        |
| checkContributingExistence     | file_checks     | Überprüft, ob eine CONTRIBUTING Datei existiert.                                                     | Keine CONTRIBUTING Datei gefunden!                                             | Legen Sie eine CONTRIBUTING Datei in der Projektwurzel an.                                  |
| checkMaintainersExistence      | file_checks     | Überprüft, ob eine MAINTAINERS Datei existiert.                                                      | Keine MAINTAINERS Datei gefunden!                                              | Legen Sie eine MAINTAINERS Datei in der Projektwurzel an.                                   |
| hasMergeRequestEnabled         | settings_checks | Überprüft, ob Merge Requests erlaubt sind.                                                           | Merge requests sind nicht erlaubt!                                             | Ändern Sie die Einstellungen des Repositories um Merge Requests zu erlauben.                |
| hasIssuesEnabled               | settings_checks | Überprüft, ob Issues aktiviert sind.                                                                 | Issues sind nicht aktiviert!                                                   | Ändern Sie die Einstellungen des Repositories um Issues zu aktivieren.                      |
| isPublic                       | settings_checks | Überprüft, ob das Repository öffentlich ist.                                                         | Das Repository ist nicht öffentlich!                                           | Ändern Sie die Einstellungen des Repositories um es öffentlich zu machen.                   |
| gitlabWikiDisabled             | settings_checks | Überprüft, ob das Repository Wiki Pages nutzt.                                                       | Das Repository nutzt Wiki Pages!                                               | Ändern Sie die Einstellungen des Repositories um Wiki Pages zu deaktivieren.                |
| hasForkingEnabled              | settings_checks | Überprüft, ob Forks erlaubt sind.                                                                    | Das Repository erlaubt keine Forks!                                            | Ändern Sie die Einstellungen des Repositories um Forks zu erlauben.                         |
| hasAvatar                      | settings_checks | Überprüft, ob das Repository ein Avatar (Bild) hat.                                                  | Das Repository hat keinen Avatar!                                              | Fügen Sie einen Avatar hinzu.                                                               |
| hasDescription                 | settings_checks | Überprüft, ob das Repository eine Beschreibung hat.                                                  | Das Repository hat keine Beschreibung!                                         | Fügen Sie eine Beschreibung hinzu.                                                          |
| hasSquashingEnabled            | settings_checks | Überprüft, ob squashing commits erlaubt ist                                                          | Squashing commits sind erlaubt                                                 | Ändern Sie die Einstellungen des Repositories um squash commits zu verbieten.               |
| guestRoleEnabled               | role_checks     | Überprüft, ob die Guest Rolle aktiviert ist.                                                         | Das Repository hat die Guest Rolle deaktiviert!                                | Ändern Sie die Einstellungen des Repositories um Guests zu erlauben.                        |
| developerRoleEnabled           | role_checks     | Überprüft, ob die Developer Rolle aktiviert ist.                                                     | Das Repository hat die Developer Rolle deaktiviert!                            | Ändern Sie die Einstellugen des Repositories um Developers zu erlauben.                     |
| hasBadges                      | settings_checks | Überprüft, ob das Projekt 'Badges' verwendet.                                                        | Im Repository wurden keine Badges gefunden!                                    | Fügen Sie Badges für z. B. den Build status in den Projekteigenschaften hinzu.              |
| checkReadmeHasLinks            | file_checks     | Überprüft, ob in der Readme eine Verlinkung zu Confluence oder anderen Dokus eingetragen ist.        | Keine Verlinkung zu Confluence oder online.bk.datev.de/documentation gefunden! | Fügen Sie die benötigte Verlinkung in die Readme ein.                                       |
| checkNoContributingChain       | file_checks     | Überprüft, dass sich in der CONTRIBUTING-Datei keine links auf andere CONTRIBUTING-Dateien befinden. | Es wurde ein Link zu einer andere CONTRIBUTING.MD gefunden!                    | Entfernen Sie die Verlinkung zu der anderen CONTRIBUTING.MD.                                |
| hasServiceDeskDisabled         | settings_checks | Überprüft, ob der Service Desk deaktiviert ist oder nicht.                                           | Service Desk ist aktiviert!                                                    | Deaktivieren Sie Service Desk in ihrem Projekt.                                             |
| eitherOwnersOrMaintainersExist | file_checks     | Überprüft, ob eine MAINTAINERS.md oder OWNERS.md Datei existiert.                                    | Keine MAINTAINERS.md oder OWNERS.md Datei gefunden!                            | Legen Sie eine MAINTAINERS.md oder OWNERS.md Datei in der Projektwurzel an.                 |
| notDefaultReadme               | file_checks     | Überprüft, ob die README Datei die Default-Readme ist.                                               | Die Readme ist die Default-Readme!                                             | Legen Sie eine README Datei in der Projektwurzel an, die nicht automatisch generiert wurde. |
