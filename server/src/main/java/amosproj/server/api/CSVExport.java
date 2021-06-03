package amosproj.server.api;

import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
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


    /*public void exportResults(Writer writer) throws IOException {
        CSVWriter w = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);

        List<String> columns = new LinkedList<>();
        columns.add("ProjectId");
        columns.add("LintingResultId");

        LocalDateTime run = LocalDateTime.of(2021, 6, 1, 1, 1);
        while (run.isBefore(LocalDateTime.now())) {
            columns.add(run.format(DateTimeFormatter.ISO_DATE));
            run = run.plusDays(1);
        }

        w.writeNext(columns.toArray(new String[0]));
        for (LintingResult lr : results) {
            w.writeNext(new String[]{lr.getProjectId().toString(), lr.getId().toString(), "test"});
        }
        w.close();
    }*/

    public void exportProject(Writer writer, Project project) throws IOException {
        // create writer
        CSVWriter w = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
        // create header
        List<String> header = new LinkedList<>();
        header.add("LintingResultId");
        header.add("CheckName");
        for (LintingResult lr : project.getResults()) {
            header.add(lr.getLintTime().format(DateTimeFormatter.ISO_DATE_TIME));
        }
        w.writeNext(header.toArray(new String[0]));
        // create values (für jeden check eine zeile
        for (LintingResult lr : project.getResults()) {
            w.writeNext(new String[]{"test"});
        }
        w.close();
    }

}
