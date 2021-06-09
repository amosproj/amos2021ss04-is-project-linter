package amosproj.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class Config {

    private static String configFile = "/home/amos/config.json"; // FIXME default value da sonst nicht in docker läuft.

    /*public Config(@Value("${CONFIG_FILE}") String CONFIG_FILE) {
        configFile = CONFIG_FILE;
    }*/

    @Value("${CONFIG_FILE}")
    public void setConfigFile(String CONFIG_FILE) {
        configFile = CONFIG_FILE;
    }

    /**
     * Gets the config.json and parses it into a JsonNode
     *
     * @return JsonNode of the parsed config.json
     */
    public static JsonNode getConfigNode() {
        File file = new File(configFile);
        assert file.exists();
        try {
            InputStream fileStream = new FileInputStream(file);
            ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
            return objectMapper.readTree(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
