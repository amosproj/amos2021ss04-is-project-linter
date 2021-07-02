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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
public class Config {

    private static String configFile = "/home/amos/config.json"; // FIXME default value da sonst nicht in docker läuft.

    public Config(@Value("${CONFIG_FILE}") String CONFIG_FILE) {
        Config.configFile = CONFIG_FILE;
    }

    @Value("${CONFIG_FILE}")
    public void setConfigFile(String CONFIG_FILE) {
        Config.configFile = CONFIG_FILE;
    }

    /**
     * Gets the config.json and parses it into a JsonNode
     *
     * @return JsonNode of the parsed config.json
     */
    public static JsonNode getConfigNode() {
        File file = new File(Config.configFile);
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

    /**
     * gets all unique tags
     *
     * @return list of tags
     */
    public static Set<String> getAllTags() {
        JsonNode node = Config.getConfigNode().get("checks");
        Set<String> res = new HashSet<>();
        for (JsonNode n : node) {
            res.add(n.get("tag").asText());
        }
        return res;
    }

    /**
     * Gets tags for all checks
     *
     * @return Map(checkName, tag)
     */
    public static HashMap<String, String> getTags() {
        HashMap<String, String> map = new HashMap<>();
        JsonNode node = Config.getConfigNode().get("checks");
        Iterator<String> iterator = node.fieldNames();
        while (iterator.hasNext()) {
            String checkName = iterator.next();
            String checkCategory = node.get(checkName).get("tag").asText();
            map.put(checkName, checkCategory);
        }
        return map;
    }

    public static HashMap<String, Long> getPriorities() {
        HashMap<String, Long> map = new HashMap<>();
        JsonNode node = Config.getConfigNode().get("checks");
        Iterator<String> iterator = node.fieldNames();
        while (iterator.hasNext()) {
            String checkName = iterator.next();
            Long priority = node.get(checkName).get("priority").asLong();
            map.put(checkName, priority);
        }
        return map;
    }
}
