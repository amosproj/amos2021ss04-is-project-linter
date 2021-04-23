package amosproj.server.linter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class LinterTest {

    private String[] getTestRepos() {
        Resource resource = new ClassPathResource("classpath:repoList.txt");
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);
            String data = new String(binaryData, StandardCharsets.UTF_8);
            return data.split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    @Test
    public void testGetResult() {
        Linter cw = new Linter();

        for (String repo : getTestRepos()) {
            var actual = cw.getResult(repo);
            System.out.println(actual);
        }

    }

}
