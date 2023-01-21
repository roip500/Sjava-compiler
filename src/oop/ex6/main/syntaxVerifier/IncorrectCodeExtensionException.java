package oop.ex6.main.syntaxVerifier;

public class IncorrectCodeExtensionException extends RuntimeException {
    public IncorrectCodeExtensionException(String errorMessage) {
        super(errorMessage);
    }
}
