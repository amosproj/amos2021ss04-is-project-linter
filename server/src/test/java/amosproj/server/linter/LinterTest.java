package amosproj.server.linter;

import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import org.junit.jupiter.api.Test; // need JUnit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@RunWith(MockitoJUnitRunner.class)
public class LinterTest {

//    private String[] getTestRepos() {
//        Resource resource = new ClassPathResource("classpath:repoList.txt");
//        try {
//            InputStream inputStream = resource.getInputStream();
//            byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);
//            String data = new String(binaryData, StandardCharsets.UTF_8);
//            return data.split("\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new String[]{};
//    }

//    @Test
//    public void testGetResult() {
//        Linter cw = new Linter();
//
//        for (String repo : getTestRepos()) {
//            var actual = cw.getResult(repo);
//            System.out.println(actual);
//        }
//
//    }

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    Linter linter;

    @Test
    public void isPublicPositive() {
        var url = "https://gitlab.com/altaway/herbstluftwm";
        linter.getResult(url);

        Project project = projectRepository.findByUrl(url);
        var result = project.getResults().get(0);
        var isPublic = result.getSettingsCheck().getPublic();
        assert isPublic;

    }

}
