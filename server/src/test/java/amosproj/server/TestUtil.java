package amosproj.server;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtil {

    public static String[] getTestRepos() {
        Resource resource = new ClassPathResource("repoList.txt");
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

}
