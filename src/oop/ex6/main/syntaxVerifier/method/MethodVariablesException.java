package oop.ex6.main.syntaxVerifier.method;

/**
 * exception used for method variable setup
 */
public class MethodVariablesException extends GeneralMethodException {
    public MethodVariablesException(String s) {
        super("invalid variable initialized -> " + s);
    }
}
