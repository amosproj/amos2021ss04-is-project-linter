package amosproj.server.api;

import amosproj.server.TestUtil;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Iterator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LintingResultRepository lintingResultRepository;

    @Test
    public void testAllProjects() throws Exception {
        // insert test data
        projectRepository.save(new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com"));
        // do test request
        this.mockMvc.perform(get("/projects")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void testGetProject() throws Exception {
        // insert test data
        Project proj = projectRepository.save(new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com"));
        LintingResult lintingResult = lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now()));
        // do test requests
        this.mockMvc.perform(get("/project/" + proj.getId().toString())).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/project/9999999999")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void testLintProject() throws Exception {
        JsonNode node = TestUtil.getTestConfig();
        for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
            String repo = it.next();

            mockMvc.perform(post("/projects").contentType(MediaType.TEXT_PLAIN_VALUE)
                    .content(repo)).andExpect(status().isOk());
        }

        mockMvc.perform(post("/projects").contentType(MediaType.TEXT_PLAIN_VALUE)
                .content(node.get("https://gitlab.cs.fau.de/ib49uquh/allcheckstrue").get("gitlabInstance").asText() + "/be15piel/repo-welches-garantiert-nicht-existiert"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProjectLintsLastMonth() throws Exception {
        Project proj = new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com");
        lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now()));
        lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now().minusDays(50)));
        proj = projectRepository.save(proj);

        mockMvc.perform(get("/project/" + proj.getId() + "/lastMonth")).andExpect(status().isOk());
        // TODO check that only ONE linting result is returned
    }

}
