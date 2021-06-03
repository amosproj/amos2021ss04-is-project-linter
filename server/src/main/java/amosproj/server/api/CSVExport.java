package amosproj.server.api;

import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import com.opencsv.CSVWriter;
import org.aspectj.weaver.Lint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;

@Service
public class CSVExport {

    @Autowired
    private LintingResultRepository lintingResultRepository;

    public void exportResults(Writer writer) throws IOException {
        exportResults(writer, null, null);
    }

    public void exportResults(Writer writer, LocalDateTime start, LocalDateTime end) throws IOException {
        CSVWriter w = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
        Iterable<LintingResult> results;
        if (start == null && end == null) {
            results = lintingResultRepository.findByLintTimeBetween(LocalDateTime.of(1970, 1, 1, 1, 1), LocalDateTime.now());
        } else if (start == null) {
            results = lintingResultRepository.findByLintTimeBetween(LocalDateTime.of(1970, 1, 1, 1, 1), end);
        } else if (end == null) {
            results = lintingResultRepository.findByLintTimeBetween(start, LocalDateTime.now());
        } else {
            results = lintingResultRepository.findByLintTimeBetween(start, end);
        }

        w.writeNext(new String[]{"ProjectId", "LintingResultsId", "Datum"});
        for (LintingResult lr : results) {
            w.writeNext(new String[]{lr.getProjectId().toString(), lr.getId().toString(), "test"});
        }
        w.close();
    }

}
