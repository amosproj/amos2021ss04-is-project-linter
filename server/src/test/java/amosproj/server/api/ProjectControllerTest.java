package amosproj.server.api;

import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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
        projectRepository.save(new Project("amos-testz", "https://gitlab.cs.fau.de/ib49uquh/amos-testz", 69, "gitlab.cs.fau.de"));
        // do test request
        this.mockMvc.perform(get("/projects")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void testGetProject() throws Exception {
        // insert test data
        Project proj = projectRepository.save(new Project("ChiefExam", "https://gitlab.cs.fau.de/it62ajow/chiefexam", 420, "gitlab.cs.fau.de"));
        LintingResult lintingResult = lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now()));
        // do test requests
        this.mockMvc.perform(get("/project/" + proj.getId().toString())).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/project/9999999999")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void testLintProject() throws Exception {
        mockMvc.perform(post("/projects").contentType(MediaType.TEXT_PLAIN_VALUE)
                .content("https://gitlab.cs.fau.de/ib49uquh/amos-testz"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/projects").contentType(MediaType.TEXT_PLAIN_VALUE)
                .content("https://gitlab.cs.fau.de/ib49uquh/repo-welches-garantiert-nicht-existiert"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetProjectLintsLastMonth() throws Exception {
        Project proj = new Project("ChiefExam", "https://gitlab.cs.fau.de/it62ajow/chiefexam", 420, "gitlab.cs.fau.de");
        lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now()));
        lintingResultRepository.save(new LintingResult(proj, LocalDateTime.now().minusDays(50)));
        proj = projectRepository.save(proj);

        mockMvc.perform(get("/project/" + proj.getId() + "/lastMonth")).andExpect(status().isOk());
        // TODO check that only ONE linting result is returned
    }

}
