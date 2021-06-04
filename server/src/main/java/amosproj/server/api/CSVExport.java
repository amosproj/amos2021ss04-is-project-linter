package amosproj.server.api;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
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
        List<String> header = new LinkedList<>();
        header.add("ProjectId");
        header.add("LintingResultId");
        List<LocalDateTime> dates = lintingResultRepository.findAllByCustomQuery();
        for (LocalDateTime date : dates) {
            header.add(date.format(DateTimeFormatter.ISO_DATE_TIME));
        }
        // get results
        Iterable<LintingResult> results = lintingResultRepository.findAll();
        // write values
        w.writeNext(header.toArray(new String[0])); // write header
        for (LintingResult res : results) {
            List<String> value = new LinkedList<>();
            value.add(res.getProjectId().toString());

            for (CheckResult check : res.getCheckResults()) {
                value.add(check.getCheckName());
            }

            w.writeNext(value.toArray(new String[0]));
        }
        w.close();
    }
    

}
