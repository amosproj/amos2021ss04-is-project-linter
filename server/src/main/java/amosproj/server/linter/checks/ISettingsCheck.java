package amosproj.server.linter.checks;

import org.springframework.boot.ApplicationContextFactory;

interface ISettingsCheck {

    /**
     * checks if a settings field has the expected value
     */
    boolean checkValue(org.gitlab4j.api.models.Project project, String field, String expected);




}
