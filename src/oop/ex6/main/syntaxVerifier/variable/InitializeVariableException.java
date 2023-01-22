package oop.ex6.main.syntaxVerifier.variable;

/**
 * exception used when variable is being initialized
 */
public class InitializeVariableException extends GeneralVariableException {
    public InitializeVariableException(String s) {
        super("invalid initialization -> " + s);
    }
}
