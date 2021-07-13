package amosproj.server.api;

import amosproj.server.Config;
import amosproj.server.TestUtil;
import amosproj.server.data.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class APIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LintingResultRepository lintingResultRepository;

    @Autowired
    private CheckResultRepository checkResultRepository;

    @BeforeEach
    public void delete() {
        projectRepository.deleteAll();
        lintingResultRepository.deleteAll();
        checkResultRepository.deleteAll();
    }

    @Test
    public void testAllProjects() throws Exception {
        // insert test data
        projectRepository.save(new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com", "", 0, LocalDateTime.now(Clock.systemUTC())));
        // do test request
        this.mockMvc.perform(get("/projects")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void testGetProject() throws Exception {
        // insert test data
        Project proj = projectRepository.save(new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com", "", 0, LocalDateTime.now(Clock.systemUTC())));
        LintingResult lintingResult = lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now()));
        // do test requests
        this.mockMvc.perform(get("/project/" + proj.getId().toString())).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/project/9999999999")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void testProjectsTop() throws Exception {
        Project project = new Project("repo", "https://url.com/repo", 4711, "url.com", "", 0, LocalDateTime.now(Clock.systemUTC()));
        projectRepository.save(project);

        LocalDateTime timeStamp = LocalDateTime.now(Clock.systemUTC());
        LintingResult lintingResult = new LintingResult(project, timeStamp);
        lintingResultRepository.save(lintingResult);

        JsonNode node = Config.getConfigNode();
        var checkNames = node.fieldNames();
        while (checkNames.hasNext()) {
            String checkName = checkNames.next();
            var check = new CheckResult(lintingResult, checkName, true);
            checkResultRepository.save(check);
        }

        mockMvc.perform(get("/projects/top").param("type", "absolute")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());

    }

    @Test
    public void testProjectsAllTags() throws Exception {
        Project project = new Project("repo", "https://url.com/repo", 4711, "url.com", "", 0, LocalDateTime.now(Clock.systemUTC()));
        projectRepository.save(project);

        LocalDateTime timeStamp = LocalDateTime.now(Clock.systemUTC());
        LintingResult lintingResult = new LintingResult(project, timeStamp);
        lintingResultRepository.save(lintingResult);

        JsonNode node = Config.getConfigNode();
        var checkNames = node.fieldNames();
        while (checkNames.hasNext()) {
            String checkName = checkNames.next();
            var check = new CheckResult(lintingResult, checkName, true);
            checkResultRepository.save(check);
        }

        mockMvc.perform(get("/projects/allTags").param("type", "absolute")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());

    }

}
