package org.netarch;

public class CompilerException extends Exception {
    protected String message;
    CompilerException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
