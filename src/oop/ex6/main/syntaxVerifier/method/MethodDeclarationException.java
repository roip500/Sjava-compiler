package oop.ex6.main.syntaxVerifier.method;

/**
 * exception used for method declaration
 */
public class MethodDeclarationException extends GeneralMethodException {
    public MethodDeclarationException(String s) {
        super("invalid method declaration -> " + s);
    }
}
