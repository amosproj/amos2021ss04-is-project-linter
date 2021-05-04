package amosproj.server.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class ProjectTest {

    @Autowired ProjectRepository projectRepository;

    private Project testProj = null;

    @BeforeEach
    void setUp() {
        testProj = new Project("ChiefExam", "https://gitlab.cs.fau.de/it62ajow/chiefexam", 1, "https://gitlab.cs.fau.de");
        projectRepository.save(testProj);
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
    }

    @Test
    public void checkDataBaseIntegrity() {
        assert testProj != null;
        assert projectRepository.findByGitlabProjectId(1) != null;

        Iterable<Project> projectIterable = projectRepository.findAll();
        Iterator<Project> projectIterator = projectIterable.iterator();

        while(projectIterator.hasNext()) {
            Project integrity = projectIterator.next();
            assertNotNull(integrity);
            assertEquals(testProj.getId(), integrity.getId());
            assertEquals(testProj.getGitlabProjectId(), integrity.getGitlabProjectId());
            assertEquals(testProj.getGitlabInstance(), integrity.getGitlabInstance());
            assertEquals(testProj.getResults(), integrity.getResults());
            assertEquals(testProj.getName(), integrity.getName());
            assertEquals(testProj.getUrl(), integrity.getUrl());
        }
    }

}
