package amosproj.server.api.schemas;

import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class ProjectSchemaTest {

    @Autowired
    LintingResultRepository lintingResultRepository;


    @Test
    public void testProjectSchema() {
        // insert test data
        Project project = new Project("amos-testz", "https://gitlab.cs.fau.de/ib49uquh/amos-testz", 69, "gitlab.cs.fau.de");
        ProjectSchema projectSchema = new ProjectSchema(project, lintingResultRepository, false);

        // some assertions
        assertEquals(projectSchema.getId(), project.getId());
//        assertEquals(projectSchema.getLintingResults(), project.getResults()); FIXME
        assertEquals(projectSchema.getGitlabProjectId(), project.getGitlabProjectId());
        assertEquals(projectSchema.getGitlabInstance(), project.getGitlabInstance());
        assertEquals(projectSchema.getName(), project.getName());
        assertEquals(projectSchema.getUrl(), project.getUrl());
        assertEquals(projectSchema.getDescription(), project.getDescription());
        assertEquals(projectSchema.getForkCount(), project.getForkCount());
        assertEquals(projectSchema.getLastCommit(), project.getLastCommit());
    }

}
