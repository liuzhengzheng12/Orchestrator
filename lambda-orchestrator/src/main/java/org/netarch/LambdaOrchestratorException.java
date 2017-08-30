package org.netarch;

public class LambdaOrchestratorException extends Exception {
    protected String message;
    public LambdaOrchestratorException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
