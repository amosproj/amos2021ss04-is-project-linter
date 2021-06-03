package amosproj.server.api;

import amosproj.server.data.LintingResultRepository;
import com.opencsv.CSVWriter;
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
        w.writeNext(new String[]{"test", "kek"});
        w.close();
    }

}
