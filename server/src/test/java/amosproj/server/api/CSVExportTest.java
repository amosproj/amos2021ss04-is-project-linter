package amosproj.server.api;

import amosproj.server.Config;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class CSVExportTest {

    @Autowired
    private CSVExport csvExport;

    @Test
    public void testCSVExport() {
        try {
            StringWriter actual = new StringWriter();
            StringWriter writer = new StringWriter();
            CSVWriter expected = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
            csvExport.exportResults(actual);

            ArrayList<String> checks = new ArrayList<>();
            for (Iterator<String> it = Config.getConfigNode().get("checks").fieldNames(); it.hasNext(); ) {
                checks.add(it.next());
            }
            // write header
            List<String> header = new LinkedList<>();
            header.add("repoUrl");
            header.add("lintTime");
            header.addAll(checks);

            expected.writeNext(header.toArray(new String[0]));

            assertEquals(writer.getBuffer().toString(), actual.getBuffer().toString());
        } catch (IOException e) {
            fail();
        }
    }
}
