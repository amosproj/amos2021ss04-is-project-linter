package amosproj.server.linter.checks;

public class HasSquashingEnabled extends Check {

    @Override
    protected boolean evaluate() {
        System.out.println("ich bin in der run methode");
        return false;
    }
}
