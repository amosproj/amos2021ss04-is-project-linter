package amosproj.server.api;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.linter.Linter;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class CSVExport {

    @Autowired
    private LintingResultRepository lintingResultRepository;

    public void exportResults(Writer writer) throws IOException {
        // create the writer
        CSVWriter w = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
        // get all checkNames in arraylist
        ArrayList<String> checks = new ArrayList<>();
        for (Iterator<String> it = Linter.getConfigNode().get("checks").fieldNames(); it.hasNext(); ) {
            checks.add(it.next());
        }
        // write header
        List<String> header = new LinkedList<>();
        header.add("projectId");
        header.add("lintResultId");
        header.add("lintTime");
        header.addAll(checks);
        w.writeNext(header.toArray(new String[0]));
        // get results
        Iterable<LintingResult> results = lintingResultRepository.findAll();
        // write values
        for (LintingResult res : results) {
            List<String> value = new LinkedList<>();
            // add metainformation
            value.add(res.getProjectId().toString());
            value.add(res.getId().toString());
            value.add(res.getLintTime().format(DateTimeFormatter.ISO_DATE_TIME));
            // add check Results
            for (String check : checks) {
                String val = "-1";
                for (CheckResult cr : res.getCheckResults()) {
                    if (cr.getCheckName().equals(check)) {
                        val = cr.getResult() ? "1" : "0";
                        break;
                    }
                }
                value.add(val);
            }
            w.writeNext(value.toArray(new String[0]));
        }
        w.close();
    }


}
