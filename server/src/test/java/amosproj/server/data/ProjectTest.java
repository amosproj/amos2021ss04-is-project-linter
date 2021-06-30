package amosproj.server.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class ProjectTest {

    @Autowired
    ProjectRepository projectRepository;

    private Project testProj = null;

    @BeforeEach
    void setUp() {
        testProj = new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com", "Beschreibung", 1, LocalDateTime.now(ZoneId.of("UTC")));
        projectRepository.save(testProj);
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
    }

    @Test
    public void checkDataBaseIntegrity() {
        assert testProj != null;
        assert projectRepository.findFirstByGitlabProjectId(69) != null;

        Iterable<Project> projectIterable = projectRepository.findAll();
        Iterator<Project> projectIterator = projectIterable.iterator();

        while (projectIterator.hasNext()) {
            Project integrity = projectIterator.next();
            assertNotNull(integrity);
            assertEquals(testProj.getId(), integrity.getId());
            assertEquals(testProj.getGitlabProjectId(), integrity.getGitlabProjectId());
            assertEquals(testProj.getNameSpace(), integrity.getNameSpace());
            assertEquals(testProj.getResults(), integrity.getResults());
            assertEquals(testProj.getName(), integrity.getName());
            assertEquals(testProj.getUrl(), integrity.getUrl());
            assertEquals(testProj.getDescription(), integrity.getDescription());
            assertEquals(testProj.getForkCount(), integrity.getForkCount());
            assertEquals(testProj.getLastCommit(), integrity.getLastCommit());
        }
    }

}
