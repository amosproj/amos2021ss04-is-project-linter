package amosproj.server.api;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Service
public class CSVExport {

    @Autowired
    private LintingResultRepository lintingResultRepository;

    public void exportResults(Writer writer) throws IOException {
        // create the writer
        CSVWriter w = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
        // build the header
        // TODO
        // get results
        Iterable<LintingResult> results = lintingResultRepository.findAll();
        // write header
        //w.writeNext(header.toArray(new String[0]));
        // write values
        for (LintingResult res : results) {
            List<String> value = new LinkedList<>();
            value.add(res.getId().toString());
            value.add(res.getLintTime().format(DateTimeFormatter.ISO_DATE)); // TODO datetime
            for (CheckResult check : res.getCheckResults()) {
                value.add(check.getResult() ? "1" : "0");
            }
            w.writeNext(value.toArray(new String[0]));
        }
        w.close();
    }


}
