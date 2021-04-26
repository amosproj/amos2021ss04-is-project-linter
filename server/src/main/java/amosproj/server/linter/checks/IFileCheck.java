package amosproj.server.linter.checks;

public interface IFileCheck {

    boolean fileExists(String filename);

    boolean containsContent(String filename, String content);


}
